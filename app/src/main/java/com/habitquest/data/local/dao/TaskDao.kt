package com.habitquest.data.local.dao

import androidx.room.*
import com.habitquest.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :completed, completedAt = :completedAt WHERE id = :taskId")
    suspend fun setCompletion(taskId: Long, completed: Boolean, completedAt: Long?)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE reminderEnabled = 1 AND reminderAtMillis IS NOT NULL AND isCompleted = 0")
    suspend fun getPendingReminderTasks(): List<TaskEntity>

    @Query("UPDATE tasks SET reminderWorkId = :workId WHERE id = :taskId")
    suspend fun updateReminderWorkId(taskId: Long, workId: String?)
}
