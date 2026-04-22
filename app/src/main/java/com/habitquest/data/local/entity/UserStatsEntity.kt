package com.habitquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey
    val id: Int = 1,
    val totalXP: Int = 0,
    val lastDailyReset: String = "",
    val userName: String = "Héroe",
    val userAvatar: String = "🦸"
)
