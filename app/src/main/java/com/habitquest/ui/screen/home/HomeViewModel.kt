package com.habitquest.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitquest.data.repository.HabitRepository
import com.habitquest.data.repository.HabitRepositoryImpl
import com.habitquest.domain.model.Habit
import com.habitquest.domain.model.Level
import com.habitquest.domain.model.Levels
import com.habitquest.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
        val toast: ToastState? = null,
        val showQuickAdd: Boolean = false,
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

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
            val xpGained  = repository.completeHabit(habitId)
            val newXP     = prevXP + xpGained
            val newLevel  = Levels.getCurrentLevel(newXP)

            val toast = if (newLevel.level > prevLevel.level) {
                ToastState("¡Subiste a ${newLevel.name}! 🎉", isLevelUp = true)
            } else {
                ToastState("+$xpGained XP${if (xpGained > 50) " (bonus racha!)" else ""}")
            }

            _uiState.update { it.copy(toast = toast) }
            delay(2000)
            _uiState.update { it.copy(toast = null) }
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
            repository.completeTask(taskId)
            val newXP    = prevXP + HabitRepositoryImpl.XP_PER_TASK
            val newLevel = Levels.getCurrentLevel(newXP)

            val toast = if (newLevel.level > prevLevel.level) {
                ToastState("¡Subiste a ${newLevel.name}! 🎉", isLevelUp = true)
            } else {
                ToastState("+${HabitRepositoryImpl.XP_PER_TASK} XP · Tarea completada ✅")
            }

            _uiState.update { it.copy(toast = toast) }
            delay(2000)
            _uiState.update { it.copy(toast = null) }
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

    fun createHabit(name: String, icon: String, category: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addHabit(
                com.habitquest.domain.model.Habit(
                    name = name.trim(), icon = icon, category = category
                )
            )
            _uiState.update { it.copy(showQuickAdd = false) }
        }
    }

    fun createTask(name: String, category: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addTask(
                com.habitquest.domain.model.Task(
                    name = name.trim(), category = category
                )
            )
            _uiState.update { it.copy(showQuickAdd = false) }
        }
    }
}
