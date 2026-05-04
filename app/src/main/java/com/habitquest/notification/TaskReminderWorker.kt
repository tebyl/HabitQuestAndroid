package com.habitquest.notification

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.habitquest.data.local.dao.TaskDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TaskReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val taskDao: TaskDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(KEY_TASK_ID, 0L)
        Log.d(TAG, "doWork() started taskId=$taskId workId=$id")
        if (taskId == 0L) return Result.success()

        val task = taskDao.getTaskById(taskId)
        Log.d(
            TAG,
            "Loaded task taskId=$taskId exists=${task != null} completed=${task?.isCompleted} reminderEnabled=${task?.reminderEnabled}"
        )
        if (task == null || task.isCompleted || !task.reminderEnabled) return Result.success()

        val notificationsAllowed =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                applicationContext.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

        Log.d(TAG, "Notification permission allowed=$notificationsAllowed taskId=$taskId")
        if (notificationsAllowed) {
            NotificationHelper.showTaskReminder(applicationContext, task.id, task.name)
        }

        taskDao.updateReminderWorkId(task.id, null)
        Log.d(TAG, "doWork() finished taskId=$taskId")
        return Result.success()
    }

    companion object {
        private const val TAG = "TaskReminderWorker"
        const val KEY_TASK_ID = "task_id"
        const val KEY_TASK_NAME = "task_name"
    }
}

