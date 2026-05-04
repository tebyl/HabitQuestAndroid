package com.habitquest.ui.screen.profile

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitquest.R
import com.habitquest.data.preferences.AmbientSoundPreferences
import com.habitquest.data.repository.HabitRepository
import com.habitquest.domain.model.Habit
import com.habitquest.domain.model.Level
import com.habitquest.domain.model.Levels
import com.habitquest.domain.gamification.PetReactionPolicy
import com.habitquest.domain.gamification.PetReactionSnapshot
import com.habitquest.domain.gamification.PetReactionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AvatarOption(
    val emoji: String,
    val label: String,
    @DrawableRes val imageRes: Int
)

val AVATAR_OPTIONS = listOf(
    AvatarOption("\uD83D\uDC69\uD83C\uDFFB\u200D\uD83E\uDDB0", "Chica 1", R.drawable.avatar_default),
    AvatarOption("\uD83D\uDC69\uD83C\uDFFD", "Chica 2", R.drawable.avatar_2),
    AvatarOption("\uD83D\uDC69\uD83C\uDFFB", "Chica 3", R.drawable.avatar_3),
    AvatarOption("\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBB", "Chica 4", R.drawable.avatar_4),
    AvatarOption("\uD83D\uDC69\u200D\uD83E\uDDB1", "Chica 5", R.drawable.avatar_5),
    AvatarOption("\uD83D\uDC71\u200D\u2640\uFE0F", "Chica 6", R.drawable.avatar_6),
)

val DEFAULT_AVATAR = AVATAR_OPTIONS.first().emoji

fun resolveSupportedAvatar(avatar: String): String =
    AVATAR_OPTIONS.firstOrNull { it.emoji == avatar }?.emoji ?: DEFAULT_AVATAR

enum class Rank(val title: String, val icon: String, val color: Color) {
    INICIO("Inicio", "🌱", Color(0xFF6B7280)),
    EXPLORADORA("Exploradora", "✨", Color(0xFF10B981)),
    CONSTANTE("Constante", "💗", Color(0xFFFB7185)),
    CREADORA_DE_HABITOS("Creadora de hábitos", "🌿", Color(0xFF8B5CF6)),
    INSPIRADORA("Inspiradora", "👑", Color(0xFFF59E0B))
}

fun rankFromLevel(level: Int): Rank = when (level) {
    1 -> Rank.INICIO
    2 -> Rank.EXPLORADORA
    3 -> Rank.CONSTANTE
    4 -> Rank.CREADORA_DE_HABITOS
    else -> Rank.INSPIRADORA
}

data class ProfileAchievement(
    val id: Int,
    val title: String,
    val description: String,
    val icon: String,
    val unlocked: Boolean,
    val progressText: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: HabitRepository,
    private val ambientSoundPreferences: AmbientSoundPreferences
) : ViewModel() {

    data class UiState(
        val habits: List<Habit> = emptyList(),
        val totalXP: Int = 0,
        val currentLevel: Level = Levels.all.first(),
        val xpToNextLevel: Int = 200,
        val xpInCurrentLevel: Int = 0,
        val userName: String = "Tu espacio",
        val userAvatar: String = DEFAULT_AVATAR,
        val rank: Rank = Rank.INICIO,
        val achievements: List<ProfileAchievement> = emptyList(),
        val maxStreak: Int = 0,
        val tasksCompleted: Int = 0,
        val isEditingName: Boolean = false,
        val editNameDraft: String = "",
        val showAvatarPicker: Boolean = false,
        val notificationsEnabled: Boolean = false,
        val ambientSoundEnabled: Boolean = false,
        val petReactionState: PetReactionState = PetReactionState.IDLE,
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private var previousPetSnapshot: PetReactionSnapshot? = null
    private var petReactionJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                repository.getHabits(),
                repository.getUserStats(),
                repository.getTasks(),
                ambientSoundPreferences.ambientSoundEnabled
            ) { habits, stats, tasks, ambientSoundEnabled ->
                ProfileData(habits, stats, tasks, ambientSoundEnabled)
            }
                .collect { data ->
                    val habits = data.habits
                    val stats = data.stats
                    val tasks = data.tasks
                    val level = Levels.getCurrentLevel(stats.totalXP)
                    val maxStreak = habits.maxOfOrNull { it.streakCount } ?: 0
                    val tasksCompleted = tasks.count { it.isCompleted }
                    val currentSnapshot = PetReactionSnapshot(
                        totalXP = stats.totalXP,
                        level = level.level,
                        streak = maxStreak,
                        completedHabits = habits.sumOf { it.totalDays },
                        completedTasks = tasksCompleted
                    )
                    val reaction = previousPetSnapshot
                        ?.let { PetReactionPolicy.detect(it, currentSnapshot) }
                        ?: PetReactionState.IDLE
                    previousPetSnapshot = currentSnapshot
                    _uiState.update {
                        it.copy(
                            habits           = habits,
                            totalXP          = stats.totalXP,
                            currentLevel     = level,
                            xpToNextLevel    = Levels.getXPToNext(stats.totalXP),
                            xpInCurrentLevel = Levels.getXPInCurrentLevel(stats.totalXP),
                            userName         = stats.userName,
                            userAvatar       = resolveSupportedAvatar(stats.userAvatar),
                            editNameDraft    = if (it.isEditingName) it.editNameDraft else stats.userName,
                            rank             = rankFromLevel(level.level),
                            achievements     = buildAchievements(habits, stats.totalXP, level.level, tasksCompleted, maxStreak),
                            maxStreak        = maxStreak,
                            tasksCompleted   = tasksCompleted,
                            ambientSoundEnabled = data.ambientSoundEnabled,
                            isLoading        = false
                        )
                    }
                    emitPetReaction(reaction)
                }
        }
    }

    private fun buildAchievements(
        habits: List<Habit>,
        totalXP: Int,
        levelNum: Int,
        tasksCompleted: Int,
        maxStreak: Int
    ): List<ProfileAchievement> = listOf(
        ProfileAchievement(1, "Primer paso",       "Completa tu primer hábito",        "🌱", habits.any { it.totalDays >= 1 }),
        ProfileAchievement(2, "Ritmo de 7 días",   "Mantén tu constancia una semana",  "💗", maxStreak >= 7, if (maxStreak >= 7) null else "$maxStreak/7"),
        ProfileAchievement(3, "Rutina organizada", "Completa 10 tareas",               "✅", tasksCompleted >= 10, if (tasksCompleted >= 10) null else "$tasksCompleted/10"),
        ProfileAchievement(4, "Nueva versión",     "Alcanza una nueva etapa",          "👑", levelNum >= 5, if (levelNum >= 5) null else "$levelNum/5"),
        ProfileAchievement(5, "Energía acumulada", "Suma 1000 puntos de progreso",     "✨", totalXP >= 1000, if (totalXP >= 1000) null else "$totalXP/1000"),
    )

    fun startEditingName() {
        _uiState.update { it.copy(isEditingName = true, editNameDraft = it.userName) }
    }

    fun updateNameDraft(name: String) {
        _uiState.update { it.copy(editNameDraft = name) }
    }

    fun confirmEditName() {
        val name = _uiState.value.editNameDraft.trim().ifEmpty { "Tu espacio" }
        _uiState.update { it.copy(isEditingName = false, userName = name) }
        viewModelScope.launch { repository.updateUserName(name) }
    }

    fun cancelEditName() {
        _uiState.update { it.copy(isEditingName = false) }
    }

    fun openAvatarPicker() {
        _uiState.update { it.copy(showAvatarPicker = true) }
    }

    fun selectAvatar(emoji: String) {
        _uiState.update { it.copy(userAvatar = emoji, showAvatarPicker = false) }
        viewModelScope.launch { repository.updateUserAvatar(emoji) }
    }

    fun dismissAvatarPicker() {
        _uiState.update { it.copy(showAvatarPicker = false) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    fun setAmbientSoundEnabled(enabled: Boolean) {
        _uiState.update { it.copy(ambientSoundEnabled = enabled) }
        viewModelScope.launch { ambientSoundPreferences.setAmbientSoundEnabled(enabled) }
    }

    private fun emitPetReaction(reaction: PetReactionState) {
        if (reaction == PetReactionState.IDLE) return
        petReactionJob?.cancel()
        _uiState.update { it.copy(petReactionState = reaction) }
        petReactionJob = viewModelScope.launch {
            delay(PET_REACTION_TIMEOUT_MILLIS)
            _uiState.update { it.copy(petReactionState = PetReactionState.IDLE) }
        }
    }

    private companion object {
        const val PET_REACTION_TIMEOUT_MILLIS = 2_500L
    }
}

private data class ProfileData(
    val habits: List<Habit>,
    val stats: com.habitquest.domain.model.UserStats,
    val tasks: List<com.habitquest.domain.model.Task>,
    val ambientSoundEnabled: Boolean
)
