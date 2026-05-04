package com.habitquest.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val appContext = context.applicationContext
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, 0L)
        val taskName = intent.getStringExtra(EXTRA_TASK_TITLE)
            ?: intent.getStringExtra(EXTRA_TASK_NAME)
        val reminderAtMillis = intent.getLongExtra(EXTRA_REMINDER_AT_MILLIS, 0L)
        val triggerAtMillis = intent.getLongExtra(EXTRA_TRIGGER_AT_MILLIS, 0L)
        val now = System.currentTimeMillis()
        Log.d(
            TAG,
            "TaskReminderReceiver onReceive called taskId=$taskId taskName=$taskName now=$now reminderAtMillis=$reminderAtMillis " +
                "triggerMillis=$triggerAtMillis deliveryDelayMs=${if (triggerAtMillis > 0L) now - triggerAtMillis else 0L}"
        )

        if (taskId == 0L || taskName.isNullOrEmpty()) return

        val allowed = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            appContext.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

        Log.d(TAG, "Posting notification allowed=$allowed taskId=$taskId")
        if (allowed) {
            NotificationHelper.showTaskReminder(appContext, taskId, taskName)
            Log.d(TAG, "notification shown taskId=$taskId reminderAtMillis=$reminderAtMillis now=$now")
        } else {
            Log.w(TAG, "notification blocked by POST_NOTIFICATIONS taskId=$taskId")
        }
    }

    companion object {
        private const val TAG = "TaskReminderReceiver"
        const val ACTION_TASK_REMINDER = "com.habitquest.notification.ACTION_TASK_REMINDER"
        const val EXTRA_TASK_ID = "task_id"
        const val EXTRA_TASK_NAME = "task_name"
        const val EXTRA_TASK_TITLE = "taskTitle"
        const val EXTRA_REMINDER_AT_MILLIS = "reminder_at_millis"
        const val EXTRA_TRIGGER_AT_MILLIS = "trigger_at_millis"
    }
}
