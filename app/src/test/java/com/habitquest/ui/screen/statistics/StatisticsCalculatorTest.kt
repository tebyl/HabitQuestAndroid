package com.habitquest.ui.screen.statistics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
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
}
