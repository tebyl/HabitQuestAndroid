package com.habitquest.ui.screen.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitquest.data.repository.HabitRepository
import com.habitquest.data.repository.HabitRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    data class DayStats(val date: LocalDate, val count: Int, val xp: Int = 0)

    data class HabitStreak(
        val habitId: Long,
        val name: String,
        val icon: String,
        val streak: Int,
        val category: String
    )

    data class UiState(
        val weeklyHabits: List<DayStats> = emptyList(),
        val weeklyTasks: List<DayStats> = emptyList(),
        val weeklyXP: List<DayStats> = emptyList(),
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

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val systemZone = ZoneId.systemDefault()

    init {
        viewModelScope.launch {
            combine(
                repository.getHabits(),
                repository.getTasks(),
                repository.getUserStats()
            ) { habits, tasks, stats -> Triple(habits, tasks, stats) }
                .collect { (habits, tasks, stats) ->
                    val today = LocalDate.now()

                    // Weekly habits: use lastCompletedDate as proxy for each past day
                    val weeklyHabits = (6 downTo 0).map { daysAgo ->
                        val date = today.minusDays(daysAgo.toLong())
                        val dateStr = date.format(dateFormatter)
                        val completed = if (daysAgo == 0) {
                            habits.filter { it.completedToday }
                        } else {
                            habits.filter { it.lastCompletedDate == dateStr }
                        }
                        val xp = completed.sumOf { HabitRepositoryImpl.xpForCategory(it.category) }
                        DayStats(date = date, count = completed.size, xp = xp)
                    }

                    // Weekly tasks: bucket by createdAt date (best available proxy)
                    val weeklyTasks = (6 downTo 0).map { daysAgo ->
                        val date = today.minusDays(daysAgo.toLong())
                        val count = tasks.count { task ->
                            val taskDate = Instant.ofEpochMilli(task.createdAt)
                                .atZone(systemZone).toLocalDate()
                            taskDate == date
                        }
                        DayStats(date = date, count = count)
                    }

                    // Category distribution (count of habits per category)
                    val categoryBreakdown = habits
                        .groupBy { it.category }
                        .mapValues { (_, list) -> list.size }

                    // Active streaks sorted descending
                    val streaks = habits
                        .filter { it.streakCount > 0 }
                        .sortedByDescending { it.streakCount }
                        .map {
                            HabitStreak(
                                habitId  = it.id,
                                name     = it.name,
                                icon     = it.icon,
                                streak   = it.streakCount,
                                category = it.category
                            )
                        }

                    val completedToday = habits.count { it.completedToday }

                    _uiState.update {
                        it.copy(
                            weeklyHabits         = weeklyHabits,
                            weeklyTasks          = weeklyTasks,
                            weeklyXP             = weeklyHabits,
                            categoryBreakdown    = categoryBreakdown,
                            streaks              = streaks,
                            totalXP              = stats.totalXP,
                            totalHabitsCompleted = completedToday,
                            totalTasksCompleted  = tasks.count { t -> t.isCompleted },
                            maxStreak            = habits.maxOfOrNull { h -> h.streakCount } ?: 0,
                            completionRateToday  = if (habits.isEmpty()) 0f
                                                   else completedToday.toFloat() / habits.size,
                            totalHabits          = habits.size,
                            isLoading            = false
                        )
                    }
                }
        }
    }
}
