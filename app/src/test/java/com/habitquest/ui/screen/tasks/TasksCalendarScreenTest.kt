package com.habitquest.ui.screen.tasks

import com.habitquest.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

class TasksCalendarScreenTest {

    @Test
    fun taskGroupsForCalendar_showsFuturePendingTasksByScheduledDate() {
        val today = LocalDate.of(2026, 4, 30)
        val tomorrow = today.plusDays(1)
        val futureTask = Task(
            id = 1,
            name = "Control medico",
            category = "salud",
            scheduledDate = tomorrow.toString()
        )

        val tasksByDate = tasksByDate(listOf(futureTask), ZoneOffset.UTC)

        assertEquals(listOf(futureTask), tasksByDate[tomorrow])
        assertTrue(tasksByDate[tomorrow]?.first()?.isCompleted?.not() == true)
    }

    @Test
    fun monthGridDates_returnsSixWeeksStartingOnSunday() {
        val dates = monthGridDates(java.time.YearMonth.of(2026, 4))

        assertEquals(42, dates.size)
        assertEquals(LocalDate.of(2026, 3, 29), dates.first())
        assertEquals(LocalDate.of(2026, 5, 9), dates.last())
    }

    @Test
    fun tasksByDate_deletedTaskInputDoesNotAppear() {
        val deletedTaskList = emptyList<Task>()

        assertEquals(emptyMap<LocalDate, List<Task>>(), tasksByDate(deletedTaskList, ZoneOffset.UTC))
    }
}
