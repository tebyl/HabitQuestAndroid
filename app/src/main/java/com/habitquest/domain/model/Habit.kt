package com.habitquest.domain.model

data class Habit(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val category: String,
    val streakCount: Int = 0,
    val totalDays: Int = 0,
    val completedToday: Boolean = false,
    val lastCompletedDate: String = "",
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 8,
    val reminderMinute: Int = 0
)
