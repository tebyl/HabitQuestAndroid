package com.habitquest.ui.screen.home

import com.habitquest.domain.model.Task
import com.habitquest.domain.model.Habit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeTaskFiltersTest {

    @Test
    fun pendingTasksForHome_futureTaskAppearsWhenPending() {
        val future = Task(
            id = 1,
            name = "Future",
            category = "productividad",
            scheduledDate = "2026-05-01"
        )

        assertEquals(listOf(future), pendingTasksForHome(listOf(future)))
    }

    @Test
    fun pendingTasksForHome_todayTaskAppears() {
        val task = Task(
            id = 1,
            name = "Today",
            category = "productividad",
            scheduledDate = "2026-04-30"
        )

        assertEquals(listOf(task), pendingTasksForHome(listOf(task)))
    }

    @Test
    fun pendingTasksForHome_nullDateTaskAppears() {
        val task = Task(id = 1, name = "Inbox", category = "productividad")

        assertEquals(listOf(task), pendingTasksForHome(listOf(task)))
    }

    @Test
    fun pendingTasksForHome_completedTaskDoesNotAppear() {
        val completed = Task(
            id = 1,
            name = "Archive notes",
            category = "vida_diaria",
            isCompleted = true,
            completedAt = 1_777_000_000_000L,
            scheduledDate = "2026-04-30"
        )

        assertEquals(emptyList<Task>(), pendingTasksForHome(listOf(completed)))
    }

    @Test
    fun shouldShowHomeEmptyState_onlyWhenHabitsAndTasksAreEmpty() {
        assertTrue(shouldShowHomeEmptyState(emptyList(), emptyList()))
        assertFalse(shouldShowHomeEmptyState(listOf(Habit(name = "Leer", icon = "", category = "desarrollo")), emptyList()))
        assertFalse(shouldShowHomeEmptyState(emptyList(), listOf(Task(name = "Agenda", category = "mama_colegio"))))
    }
}
