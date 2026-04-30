package com.habitquest.ui.screen.home

import com.habitquest.domain.model.Task

fun pendingTasksForHome(tasks: List<Task>): List<Task> =
    tasks.filter { !it.isCompleted }
