package com.habitquest.ui.screen.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitquest.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    data class UiState(
        val weeklyHabits: List<DayStats> = emptyList(),
        val weeklyTasks: List<DayStats> = emptyList(),
        val weeklyXP: List<DayStats> = emptyList(),
        val activity14Days: List<DayStats> = emptyList(),
        val categoryBreakdown: Map<String, Int> = emptyMap(),
        val streaks: List<HabitStreak> = emptyList(),
        val totalXP: Int = 0,
        val totalHabitsCompleted: Int = 0,
        val totalTasksCompleted: Int = 0,
        val maxStreak: Int = 0,
        val completionRateToday: Float = 0f,
        val totalHabits: Int = 0,
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getHabits(),
                repository.getTasks(),
                repository.getUserStats()
            ) { habits, tasks, stats -> Triple(habits, tasks, stats) }
                .collect { (habits, tasks, stats) ->
                    val snapshot = StatisticsCalculator.calculate(habits, tasks)

                    _uiState.update {
                        it.copy(
                            weeklyHabits         = snapshot.weeklyHabits,
                            weeklyTasks          = snapshot.weeklyTasks,
                            weeklyXP             = snapshot.weeklyXP,
                            activity14Days       = snapshot.activity14Days,
                            categoryBreakdown    = snapshot.categoryBreakdown,
                            streaks              = snapshot.streaks,
                            totalXP              = stats.totalXP,
                            totalHabitsCompleted = snapshot.totalHabitsCompleted,
                            totalTasksCompleted  = snapshot.totalTasksCompleted,
                            maxStreak            = snapshot.maxStreak,
                            completionRateToday  = snapshot.completionRateToday,
                            totalHabits          = snapshot.totalHabits,
                            isLoading            = false
                        )
                    }
                }
        }
    }
}
