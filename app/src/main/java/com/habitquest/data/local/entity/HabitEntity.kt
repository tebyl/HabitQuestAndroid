package com.habitquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
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
    val reminderMinute: Int = 0,
    val orderIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
