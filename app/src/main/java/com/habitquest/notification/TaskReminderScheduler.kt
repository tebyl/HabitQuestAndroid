package com.habitquest.notification

interface TaskReminderScheduler {
    fun schedule(taskId: Long, taskName: String, reminderAtMillis: Long): String?
    fun cancel(taskId: Long, workId: String?)

    companion object {
        fun taskWorkName(taskId: Long): String = "task_reminder_$taskId"
    }
}
