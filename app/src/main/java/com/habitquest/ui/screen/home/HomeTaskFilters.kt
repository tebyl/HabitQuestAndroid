package com.habitquest.ui.screen.home

import com.habitquest.domain.model.Task
import com.habitquest.domain.model.Habit

fun pendingTasksForHome(
    tasks: List<Task>
): List<Task> =
    tasks.filter { task -> !task.isCompleted }

fun shouldShowHomeEmptyState(habits: List<Habit>, tasks: List<Task>): Boolean =
    habits.isEmpty() && tasks.isEmpty()
