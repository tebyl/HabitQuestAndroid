package com.habitquest.ui.screen.statistics

import com.habitquest.data.repository.HabitRepositoryImpl
import com.habitquest.domain.model.Habit
import com.habitquest.domain.model.Task
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class DayStats(val date: LocalDate, val count: Int, val xp: Int = 0)

data class HabitStreak(
    val habitId: Long,
    val name: String,
    val icon: String,
    val streak: Int,
    val category: String
)

data class StatisticsSnapshot(
    val weeklyHabits: List<DayStats>,
    val weeklyTasks: List<DayStats>,
    val weeklyXP: List<DayStats>,
    val categoryBreakdown: Map<String, Int>,
    val streaks: List<HabitStreak>,
    val totalHabitsCompleted: Int,
    val totalTasksCompleted: Int,
    val maxStreak: Int,
    val completionRateToday: Float,
    val totalHabits: Int
)

object StatisticsCalculator {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun calculate(
        habits: List<Habit>,
        tasks: List<Task>,
        today: LocalDate = LocalDate.now(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): StatisticsSnapshot {
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

        val weeklyTasks = (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            val count = tasks.count { task ->
                val completedAt = task.completedAt ?: return@count false
                val taskDate = Instant.ofEpochMilli(completedAt)
                    .atZone(zoneId).toLocalDate()
                task.isCompleted && taskDate == date
            }
            DayStats(date = date, count = count)
        }

        val categoryBreakdown = habits
            .groupBy { it.category }
            .mapValues { (_, list) -> list.size }

        val streaks = habits
            .filter { it.streakCount > 0 }
            .sortedByDescending { it.streakCount }
            .map {
                HabitStreak(
                    habitId = it.id,
                    name = it.name,
                    icon = it.icon,
                    streak = it.streakCount,
                    category = it.category
                )
            }

        val completedToday = habits.count { it.completedToday }

        return StatisticsSnapshot(
            weeklyHabits = weeklyHabits,
            weeklyTasks = weeklyTasks,
            weeklyXP = weeklyHabits,
            categoryBreakdown = categoryBreakdown,
            streaks = streaks,
            totalHabitsCompleted = completedToday,
            totalTasksCompleted = tasks.count { it.isCompleted && it.completedAt != null },
            maxStreak = habits.maxOfOrNull { it.streakCount } ?: 0,
            completionRateToday = if (habits.isEmpty()) 0f else completedToday.toFloat() / habits.size,
            totalHabits = habits.size
        )
    }
}
