package com.habitquest.ui.screen.home

import com.habitquest.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeTaskFiltersTest {

    @Test
    fun pendingTasksForHome_returnsOnlyIncompleteTasks() {
        val pending = Task(id = 1, name = "Plan day", category = "productividad")
        val completed = Task(
            id = 2,
            name = "Archive notes",
            category = "vida_diaria",
            isCompleted = true,
            completedAt = 1_777_000_000_000L
        )

        assertEquals(listOf(pending), pendingTasksForHome(listOf(pending, completed)))
    }
}
