package com.habitquest.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.habitquest.data.local.dao.TaskDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TaskReminderRestoreWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val taskDao: TaskDao,
    private val taskReminderScheduler: TaskReminderScheduler
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val now = System.currentTimeMillis()
        taskDao.getPendingReminderTasks().forEach { task ->
            val reminderAtMillis = task.reminderAtMillis ?: return@forEach
            if (reminderAtMillis <= now) return@forEach
            val workId = taskReminderScheduler.schedule(task.id, task.name, reminderAtMillis)
            taskDao.updateReminderWorkId(task.id, workId)
        }
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "task_reminder_restore"

        fun enqueue(context: Context) {
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                androidx.work.ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<TaskReminderRestoreWorker>().build()
            )
        }
    }
}
