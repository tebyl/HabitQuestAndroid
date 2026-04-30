package com.habitquest.ui.screen.statistics

import com.habitquest.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId
import java.time.LocalDate
import java.time.ZoneOffset

class StatisticsCalculatorTest {
    @Test
    fun calculate_withEmptyLists_returnsEmptySafeSnapshot() {
        val snapshot = StatisticsCalculator.calculate(
            habits = emptyList(),
            tasks = emptyList(),
            today = LocalDate.of(2026, 4, 29),
            zoneId = ZoneOffset.UTC
        )

        assertEquals(7, snapshot.weeklyHabits.size)
        assertEquals(7, snapshot.weeklyTasks.size)
        assertTrue(snapshot.weeklyHabits.all { it.count == 0 && it.xp == 0 })
        assertTrue(snapshot.weeklyTasks.all { it.count == 0 })
        assertEquals(emptyMap<String, Int>(), snapshot.categoryBreakdown)
        assertEquals(emptyList<HabitStreak>(), snapshot.streaks)
        assertEquals(0, snapshot.totalHabitsCompleted)
        assertEquals(0, snapshot.totalTasksCompleted)
        assertEquals(0, snapshot.maxStreak)
        assertEquals(0f, snapshot.completionRateToday)
        assertEquals(0, snapshot.totalHabits)
    }

    @Test
    fun calculate_countsWeeklyTasksByCompletedAtOnly() {
        val zoneId = ZoneId.of("UTC")
        val today = LocalDate.of(2026, 4, 29)
        val yesterdayCompletedAt = today.minusDays(1)
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
        val todayCreatedAt = today
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()

        val snapshot = StatisticsCalculator.calculate(
            habits = emptyList(),
            tasks = listOf(
                Task(
                    id = 1,
                    name = "Completed yesterday",
                    category = "productividad",
                    isCompleted = true,
                    createdAt = todayCreatedAt,
                    completedAt = yesterdayCompletedAt
                ),
                Task(
                    id = 2,
                    name = "Completed without date",
                    category = "vida_diaria",
                    isCompleted = true,
                    createdAt = todayCreatedAt,
                    completedAt = null
                ),
                Task(
                    id = 3,
                    name = "Pending",
                    category = "vida_diaria",
                    isCompleted = false,
                    createdAt = todayCreatedAt,
                    completedAt = todayCreatedAt,
                    scheduledDate = today.plusDays(1).toString()
                )
            ),
            today = today,
            zoneId = zoneId
        )

        assertEquals(1, snapshot.weeklyTasks.first { it.date == today.minusDays(1) }.count)
        assertEquals(0, snapshot.weeklyTasks.first { it.date == today }.count)
        assertEquals(1, snapshot.totalTasksCompleted)
    }
}
