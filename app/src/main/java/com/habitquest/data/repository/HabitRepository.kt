package com.habitquest.data.repository

import com.habitquest.domain.model.Habit
import com.habitquest.domain.model.Task
import com.habitquest.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getHabits(): Flow<List<Habit>>
    fun getUserStats(): Flow<UserStats>
    fun getTasks(): Flow<List<Task>>
    suspend fun completeHabit(habitId: Long): Int
    suspend fun resetDailyHabitsIfNeeded()
    suspend fun addHabit(habit: Habit): Long
    suspend fun deleteHabit(habitId: Long)
    suspend fun updateUserName(name: String)
    suspend fun updateUserAvatar(avatar: String)
    suspend fun addTask(task: Task): Long
    suspend fun completeTask(taskId: Long)
    suspend fun deleteTask(taskId: Long)
    suspend fun uncompleteHabit(habitId: Long)
    suspend fun uncompleteTask(taskId: Long)
}
