package com.habitquest.notification

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerTaskReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : TaskReminderScheduler {

    override fun schedule(taskId: Long, taskName: String, reminderAtMillis: Long): String? {
        val now = System.currentTimeMillis()
        val actualDelayMillis = reminderAtMillis - now
        val debugBuild = (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        val delayMillis = calculateDelayMillis(now, reminderAtMillis, debugBuild)
        if (delayMillis == null) {
            Log.d(
                TAG,
                "Skipping past reminder taskId=$taskId reminderAtMillis=$reminderAtMillis now=$now actualDelayMillis=$actualDelayMillis"
            )
            return null
        }

        Log.d(
            TAG,
            "Scheduling task reminder taskId=$taskId workName=${TaskReminderScheduler.taskWorkName(taskId)} delayMillis=$delayMillis actualDelayMillis=$actualDelayMillis debug=$debugBuild"
        )

        val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    TaskReminderWorker.KEY_TASK_ID to taskId,
                    TaskReminderWorker.KEY_TASK_NAME to taskName
                )
            )
            .addTag(TaskReminderScheduler.taskWorkName(taskId))
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            TaskReminderScheduler.taskWorkName(taskId),
            ExistingWorkPolicy.REPLACE,
            request
        )

        return request.id.toString()
    }

    override fun cancel(taskId: Long, workId: String?) {
        Log.d(TAG, "Cancelling task reminder taskId=$taskId workId=$workId")
        workId
            ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            ?.let { WorkManager.getInstance(context).cancelWorkById(it) }

        WorkManager.getInstance(context).cancelUniqueWork(TaskReminderScheduler.taskWorkName(taskId))
    }

    companion object {
        private const val TAG = "TaskReminderScheduler"
        private const val DEBUG_DELAY_MILLIS = 5_000L
        private const val PAST_GRACE_MILLIS = 60_000L

        internal fun calculateDelayMillis(now: Long, reminderAtMillis: Long, debugBuild: Boolean): Long? {
            val actualDelayMillis = reminderAtMillis - now
            if (actualDelayMillis > 0L) {
                return if (debugBuild) DEBUG_DELAY_MILLIS else actualDelayMillis
            }

            return if (-actualDelayMillis <= PAST_GRACE_MILLIS) {
                if (debugBuild) DEBUG_DELAY_MILLIS else 1_000L
            } else {
                null
            }
        }
    }
}
