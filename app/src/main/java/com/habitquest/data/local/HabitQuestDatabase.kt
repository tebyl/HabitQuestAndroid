package com.habitquest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.habitquest.data.local.dao.HabitDao
import com.habitquest.data.local.dao.TaskDao
import com.habitquest.data.local.dao.UserStatsDao
import com.habitquest.data.local.entity.HabitEntity
import com.habitquest.data.local.entity.TaskEntity
import com.habitquest.data.local.entity.UserStatsEntity

@Database(
    entities = [HabitEntity::class, UserStatsEntity::class, TaskEntity::class],
    version = 3,
    exportSchema = false
)
abstract class HabitQuestDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun taskDao(): TaskDao

    companion object {
        fun defaultHabits() = listOf(
            HabitEntity(name = "Meditar", icon = "🧘", category = "salud_mental", orderIndex = 0),
            HabitEntity(name = "Hacer ejercicio", icon = "💪", category = "salud_fisica", orderIndex = 1),
            HabitEntity(name = "Leer", icon = "📖", category = "desarrollo", orderIndex = 2),
            HabitEntity(name = "Beber agua", icon = "💧", category = "salud_fisica", orderIndex = 3),
            HabitEntity(name = "Planificar día", icon = "📋", category = "vida_diaria", orderIndex = 4),
        )

        fun defaultTasks() = listOf(
            TaskEntity(name = "Organizar apuntes", category = "productividad"),
            TaskEntity(name = "Revisar correos", category = "vida_diaria"),
            TaskEntity(name = "Planificar semana", category = "productividad"),
        )
    }
}
