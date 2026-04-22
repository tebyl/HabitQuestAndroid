package com.habitquest.ui.screen.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitquest.data.repository.HabitRepository
import com.habitquest.domain.model.Achievement
import com.habitquest.domain.model.Achievements
import com.habitquest.domain.model.Habit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    data class UiState(
        val habits: List<Habit> = emptyList(),
        val totalXP: Int = 0,
        val achievements: List<Achievement> = emptyList(),
        val weeklyCompliance: Float = 0f,
        val mostConsistentHabit: Habit? = null,
        val dailyActivity: List<Int> = List(7) { 0 },
        val maxHabits: Int = 1,
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getHabits(),
                repository.getUserStats()
            ) { habits, stats -> habits to stats }
                .collect { (habits, stats) ->
                    _uiState.update {
                        it.copy(
                            habits              = habits,
                            totalXP             = stats.totalXP,
                            achievements        = Achievements.evaluate(habits),
                            weeklyCompliance    = calcWeeklyCompliance(habits),
                            mostConsistentHabit = habits.maxByOrNull { h -> h.totalDays },
                            dailyActivity       = calcDailyActivity(habits),
                            maxHabits           = habits.size.coerceAtLeast(1),
                            isLoading           = false
                        )
                    }
                }
        }
    }

    private fun calcWeeklyCompliance(habits: List<Habit>): Float {
        if (habits.isEmpty()) return 0f
        val today    = LocalDate.now()
        val monday   = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val elapsed  = (today.dayOfWeek.value).coerceAtLeast(1) // 1=Mon … 7=Sun
        val total    = habits.size * elapsed
        val completed = (0 until elapsed).sumOf { i ->
            val date = monday.plusDays(i.toLong()).toString()
            habits.count { it.lastCompletedDate == date }
        }
        return if (total > 0) completed.toFloat() / total else 0f
    }

    private fun calcDailyActivity(habits: List<Habit>): List<Int> {
        val today = LocalDate.now()
        return (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong()).toString()
            habits.count { it.lastCompletedDate == date }
        }
    }
}
