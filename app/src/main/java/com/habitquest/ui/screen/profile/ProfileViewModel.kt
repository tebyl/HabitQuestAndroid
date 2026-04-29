package com.habitquest.ui.screen.profile

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitquest.data.repository.HabitRepository
import com.habitquest.domain.model.Habit
import com.habitquest.domain.model.Level
import com.habitquest.domain.model.Levels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AvatarOption(val emoji: String, val label: String)

val AVATAR_OPTIONS = listOf(
    AvatarOption("🐱", "Gato"),
    AvatarOption("🐈", "Gata"),
    AvatarOption("🐕", "Perro"),
    AvatarOption("🐩", "Perra"),
    AvatarOption("🦸", "Humano"),
    AvatarOption("🧝", "Humana"),
)

enum class Rank(val title: String, val icon: String, val color: Color) {
    NOVATO("Novato", "⚔️", Color(0xFF6B7280)),
    EXPLORADOR("Explorador", "🗺️", Color(0xFF10B981)),
    CONSTANTE("Constante", "🛡️", Color(0xFF3B82F6)),
    MAESTRO("Maestro de Hábitos", "⚡", Color(0xFF8B5CF6)),
    LEYENDA("Leyenda", "👑", Color(0xFFF59E0B))
}

fun rankFromLevel(level: Int): Rank = when (level) {
    1 -> Rank.NOVATO
    2 -> Rank.EXPLORADOR
    3 -> Rank.CONSTANTE
    4 -> Rank.MAESTRO
    else -> Rank.LEYENDA
}

data class ProfileAchievement(
    val id: Int,
    val title: String,
    val description: String,
    val icon: String,
    val unlocked: Boolean
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    data class UiState(
        val habits: List<Habit> = emptyList(),
        val totalXP: Int = 0,
        val currentLevel: Level = Levels.all.first(),
        val xpToNextLevel: Int = 200,
        val xpInCurrentLevel: Int = 0,
        val userName: String = "Héroe",
        val userAvatar: String = "🦸",
        val rank: Rank = Rank.NOVATO,
        val achievements: List<ProfileAchievement> = emptyList(),
        val maxStreak: Int = 0,
        val tasksCompleted: Int = 0,
        val isEditingName: Boolean = false,
        val editNameDraft: String = "",
        val showAvatarPicker: Boolean = false,
        val notificationsEnabled: Boolean = false,
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getHabits(),
                repository.getUserStats(),
                repository.getTasks()
            ) { habits, stats, tasks -> Triple(habits, stats, tasks) }
                .collect { (habits, stats, tasks) ->
                    val level = Levels.getCurrentLevel(stats.totalXP)
                    val maxStreak = habits.maxOfOrNull { it.streakCount } ?: 0
                    val tasksCompleted = tasks.count { it.isCompleted }
                    _uiState.update {
                        it.copy(
                            habits           = habits,
                            totalXP          = stats.totalXP,
                            currentLevel     = level,
                            xpToNextLevel    = Levels.getXPToNext(stats.totalXP),
                            xpInCurrentLevel = Levels.getXPInCurrentLevel(stats.totalXP),
                            userName         = stats.userName,
                            userAvatar       = stats.userAvatar,
                            editNameDraft    = if (it.isEditingName) it.editNameDraft else stats.userName,
                            rank             = rankFromLevel(level.level),
                            achievements     = buildAchievements(habits, stats.totalXP, level.level, tasksCompleted, maxStreak),
                            maxStreak        = maxStreak,
                            tasksCompleted   = tasksCompleted,
                            isLoading        = false
                        )
                    }
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
        ProfileAchievement(1, "Primer Hábito",  "Completa al menos 1 hábito",       "🌱", habits.any { it.totalDays >= 1 }),
        ProfileAchievement(2, "Racha 7 Días",   "Mantén una racha de 7 días",        "🔥", maxStreak >= 7),
        ProfileAchievement(3, "10 Tareas",      "Completa 10 tareas",                "✅", tasksCompleted >= 10),
        ProfileAchievement(4, "Nivel 5",        "Alcanza el nivel máximo",           "👑", levelNum >= 5),
        ProfileAchievement(5, "1000 XP",        "Acumula 1000 puntos de experiencia","⚡", totalXP >= 1000),
    )

    fun startEditingName() {
        _uiState.update { it.copy(isEditingName = true, editNameDraft = it.userName) }
    }

    fun updateNameDraft(name: String) {
        _uiState.update { it.copy(editNameDraft = name) }
    }

    fun confirmEditName() {
        val name = _uiState.value.editNameDraft.trim().ifEmpty { "Héroe" }
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
}
