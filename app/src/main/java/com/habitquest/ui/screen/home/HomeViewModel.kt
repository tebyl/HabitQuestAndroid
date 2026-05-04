package com.habitquest.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitquest.data.repository.HabitRepository
import com.habitquest.data.repository.HabitRepositoryImpl
import com.habitquest.domain.model.Habit
import com.habitquest.domain.model.Level
import com.habitquest.domain.model.Levels
import com.habitquest.domain.model.Task
import com.habitquest.domain.gamification.PetReactionPolicy
import com.habitquest.domain.gamification.PetReactionSnapshot
import com.habitquest.domain.gamification.PetReactionState
import com.habitquest.ui.screen.profile.DEFAULT_AVATAR
import com.habitquest.ui.screen.profile.resolveSupportedAvatar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    data class ToastState(val message: String, val isLevelUp: Boolean = false)

    data class UiState(
        val habits: List<Habit> = emptyList(),
        val tasks: List<Task> = emptyList(),
        val totalXP: Int = 0,
        val currentLevel: Level = Levels.all.first(),
        val xpToNext: Int = 200,
        val xpInCurrentLevel: Int = 0,
        val userAvatar: String = DEFAULT_AVATAR,
        val userName: String = "Tu espacio",
        val toast: ToastState? = null,
        val rewardBanner: RewardBannerState? = null,
        val petReactionState: PetReactionState = PetReactionState.IDLE,
        val showQuickAdd: Boolean = false,
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private var petReactionJob: Job? = null
    // Track achievements shown during this session to avoid spam (not persisted)
    private val unlockedAchievementIdsShown = mutableSetOf<Int>()

    data class RewardBannerState(val primary: String, val secondary: String? = null)

    init {
        viewModelScope.launch { repository.resetDailyHabitsIfNeeded() }

        viewModelScope.launch {
            combine(
                repository.getHabits(),
                repository.getUserStats(),
                repository.getTasks()
            ) { habits, stats, tasks -> Triple(habits, stats, tasks) }
                .collect { (habits, stats, tasks) ->
                    val level = Levels.getCurrentLevel(stats.totalXP)
                    _uiState.update {
                        it.copy(
                            habits           = habits,
                            tasks            = tasks,
                            totalXP          = stats.totalXP,
                            currentLevel     = level,
                            xpToNext         = Levels.getXPToNext(stats.totalXP),
                            xpInCurrentLevel = Levels.getXPInCurrentLevel(stats.totalXP),
                            userAvatar       = resolveSupportedAvatar(stats.userAvatar),
                            userName         = stats.userName,
                            isLoading        = false
                        )
                    }
                }
        }
    }

    fun completeHabit(habitId: Long) {
        val habit = _uiState.value.habits.find { it.id == habitId } ?: return
        if (habit.completedToday) return

        viewModelScope.launch {
            val prevLevel = _uiState.value.currentLevel
            val prevXP    = _uiState.value.totalXP
            val previousSnapshot = _uiState.value.toPetReactionSnapshot()
            val xpGained  = repository.completeHabit(habitId)
            val newXP     = prevXP + xpGained
            val newLevel  = Levels.getCurrentLevel(newXP)
            val currentSnapshot = PetReactionSnapshot(
                totalXP = newXP,
                level = newLevel.level,
                streak = maxOf(previousSnapshot.streak, habit.streakCount + 1),
                completedHabits = previousSnapshot.completedHabits + 1,
                completedTasks = previousSnapshot.completedTasks
            )

            val toast = if (newLevel.level > prevLevel.level) {
                ToastState("¡Subiste a ${newLevel.name}! 🎉", isLevelUp = true)
            } else {
                ToastState("+$xpGained XP${if (xpGained > 50) " (bonus racha!)" else ""}")
            }

            // compute newly unlocked achievements and show a temporary RewardBanner
            val newly = com.habitquest.domain.gamification.AchievementPolicy.newlyUnlocked(previousSnapshot, currentSnapshot)
            val unseen = newly.filter { !unlockedAchievementIdsShown.contains(it.id) }
            if (unseen.isNotEmpty()) {
                // mark as shown for this session
                unlockedAchievementIdsShown.addAll(unseen.map { it.id })
                val titles = unseen.joinToString(", ") { it.title }
                val banner = RewardBannerState(primary = "+$xpGained XP", secondary = "¡Logro desbloqueado! $titles")
                _uiState.update { it.copy(toast = toast, rewardBanner = banner) }
            } else {
                _uiState.update { it.copy(toast = toast) }
            }

            emitPetReaction(PetReactionPolicy.detect(previousSnapshot, currentSnapshot))
            delay(2_000)
            _uiState.update { it.copy(toast = null, rewardBanner = null) }
        }
    }

    fun uncompleteHabit(habitId: Long) {
        val habit = _uiState.value.habits.find { it.id == habitId } ?: return
        if (!habit.completedToday) return

        val xpLost = HabitRepositoryImpl.xpForCategory(habit.category) +
            if (habit.streakCount >= HabitRepositoryImpl.STREAK_BONUS_THRESHOLD)
                HabitRepositoryImpl.STREAK_BONUS else 0

        viewModelScope.launch {
            repository.uncompleteHabit(habitId)
            _uiState.update { it.copy(toast = ToastState("-$xpLost XP · Revertido")) }
            delay(2000)
            _uiState.update { it.copy(toast = null) }
        }
    }

    fun completeTask(taskId: Long) {
        val task = _uiState.value.tasks.find { it.id == taskId } ?: return
        if (task.isCompleted) return

        viewModelScope.launch {
            val prevLevel = _uiState.value.currentLevel
            val prevXP    = _uiState.value.totalXP
            val previousSnapshot = _uiState.value.toPetReactionSnapshot()
            repository.completeTask(taskId)
            val newXP    = prevXP + HabitRepositoryImpl.XP_PER_TASK
            val newLevel = Levels.getCurrentLevel(newXP)
            val currentSnapshot = PetReactionSnapshot(
                totalXP = newXP,
                level = newLevel.level,
                streak = previousSnapshot.streak,
                completedHabits = previousSnapshot.completedHabits,
                completedTasks = previousSnapshot.completedTasks + 1
            )

            val toast = if (newLevel.level > prevLevel.level) {
                ToastState("¡Subiste a ${newLevel.name}! 🎉", isLevelUp = true)
            } else {
                ToastState("+${HabitRepositoryImpl.XP_PER_TASK} XP · Tarea completada ✅")
            }

            val newly = com.habitquest.domain.gamification.AchievementPolicy.newlyUnlocked(previousSnapshot, currentSnapshot)
            val unseen = newly.filter { !unlockedAchievementIdsShown.contains(it.id) }
            if (unseen.isNotEmpty()) {
                unlockedAchievementIdsShown.addAll(unseen.map { it.id })
                val titles = unseen.joinToString(", ") { it.title }
                val banner = RewardBannerState(primary = "+${HabitRepositoryImpl.XP_PER_TASK} XP", secondary = "¡Logro desbloqueado! $titles")
                _uiState.update { it.copy(toast = toast, rewardBanner = banner) }
            } else {
                _uiState.update { it.copy(toast = toast) }
            }

            emitPetReaction(PetReactionPolicy.detect(previousSnapshot, currentSnapshot))
            delay(2_000)
            _uiState.update { it.copy(toast = null, rewardBanner = null) }
        }
    }

    fun uncompleteTask(taskId: Long) {
        val task = _uiState.value.tasks.find { it.id == taskId } ?: return
        if (!task.isCompleted) return

        viewModelScope.launch {
            repository.uncompleteTask(taskId)
            _uiState.update { it.copy(toast = ToastState("-${HabitRepositoryImpl.XP_PER_TASK} XP · Tarea revertida")) }
            delay(2000)
            _uiState.update { it.copy(toast = null) }
        }
    }

    fun showQuickAdd() = _uiState.update { it.copy(showQuickAdd = true) }
    fun hideQuickAdd() = _uiState.update { it.copy(showQuickAdd = false) }

    fun createHabit(name: String, icon: String, category: String, frequency: String = "daily") {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addHabit(
                com.habitquest.domain.model.Habit(
                    name = name.trim(), icon = icon, category = category, frequency = frequency
                )
            )
            _uiState.update { it.copy(showQuickAdd = false) }
        }
    }

    fun updateHabit(habitId: Long, name: String, category: String, frequency: String) {
        if (name.isBlank()) return
        val current = _uiState.value.habits.find { it.id == habitId } ?: return
        viewModelScope.launch {
            repository.updateHabit(
                current.copy(
                    name = name.trim(),
                    category = category,
                    frequency = frequency
                )
            )
            _uiState.update { it.copy(showQuickAdd = false) }
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
        }
    }

    fun createTask(
        name: String,
        category: String,
        scheduledDate: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
        reminderAtMillis: Long? = null
    ) {
        if (name.isBlank()) return
        Log.d(TAG, "createTask() name=${name.trim()} category=$category scheduledDate=$scheduledDate reminderAtMillis=$reminderAtMillis")
        viewModelScope.launch {
            repository.addTask(
                com.habitquest.domain.model.Task(
                    name = name.trim(),
                    category = category,
                    scheduledDate = scheduledDate,
                    reminderAtMillis = reminderAtMillis,
                    reminderEnabled = reminderAtMillis != null
                )
            )
            _uiState.update { it.copy(showQuickAdd = false) }
        }
    }

    fun updateTask(taskId: Long, name: String, category: String, scheduledDate: String, reminderAtMillis: Long?) {
        if (name.isBlank()) return
        val current = _uiState.value.tasks.find { it.id == taskId } ?: return
        Log.d(TAG, "updateTask() taskId=$taskId reminderAtMillis=$reminderAtMillis")
        viewModelScope.launch {
            repository.updateTask(
                current.copy(
                    name = name.trim(),
                    category = category,
                    scheduledDate = scheduledDate,
                    reminderAtMillis = reminderAtMillis,
                    reminderEnabled = reminderAtMillis != null,
                    reminderWorkId = null
                )
            )
            _uiState.update { it.copy(showQuickAdd = false) }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    private companion object {
        const val TAG = "HomeViewModel"
        const val PET_REACTION_TIMEOUT_MILLIS = 2_500L
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

    private fun UiState.toPetReactionSnapshot(): PetReactionSnapshot =
        PetReactionSnapshot(
            totalXP = totalXP,
            level = currentLevel.level,
            streak = habits.maxOfOrNull { it.streakCount } ?: 0,
            completedHabits = habits.sumOf { it.totalDays },
            completedTasks = tasks.count { it.isCompleted }
        )
}
