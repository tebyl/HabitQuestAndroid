package com.habitquest.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmTaskReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : TaskReminderScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(taskId: Long, taskName: String, reminderAtMillis: Long): String? {
        val now = System.currentTimeMillis()
        val triggerAtMillis = calculateTriggerMillis(now, reminderAtMillis) ?: run {
            Log.d(TAG, "Skipping stale reminder taskId=$taskId reminderAtMillis=$reminderAtMillis now=$now")
            return null
        }

        val exactAlarmAllowed = canScheduleExactAlarms()
        val alarmMethod = selectAlarmMethod(exactAlarmAllowed)
        val ignoringBatteryOptimizations = isIgnoringBatteryOptimizations()
        Log.d(
            TAG,
            "Scheduling task reminder taskId=$taskId now=$now reminderAtMillis=$reminderAtMillis " +
                "triggerMillis=$triggerAtMillis delayRealMs=${triggerAtMillis - now} " +
                "exactAlarmAllowed=$exactAlarmAllowed canScheduleExactAlarms=$exactAlarmAllowed " +
                "ignoringBatteryOptimizations=$ignoringBatteryOptimizations method=${alarmMethod.logName}"
        )

        val pi = buildPendingIntent(taskId, taskName, reminderAtMillis, triggerAtMillis)
        when (alarmMethod) {
            AlarmMethod.EXACT -> {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
                Log.d(TAG, "Exact alarm set taskId=$taskId")
            }
            AlarmMethod.INEXACT_FALLBACK -> {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
                Log.w(TAG, "Inexact fallback alarm set taskId=$taskId; exact alarm permission is missing and delivery can be delayed")
            }
        }

        return taskId.toString()
    }

    override fun cancel(taskId: Long, workId: String?) {
        Log.d(TAG, "Cancelling alarm taskId=$taskId")
        alarmManager.cancel(buildPendingIntent(taskId, ""))
    }

    private fun canScheduleExactAlarms(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    private fun isIgnoringBatteryOptimizations(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    private fun buildPendingIntent(
        taskId: Long,
        taskName: String,
        reminderAtMillis: Long = 0L,
        triggerAtMillis: Long = 0L
    ): PendingIntent {
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            action = TaskReminderReceiver.ACTION_TASK_REMINDER
            setPackage(context.packageName)
            putExtra(TaskReminderReceiver.EXTRA_TASK_ID, taskId)
            putExtra(TaskReminderReceiver.EXTRA_TASK_NAME, taskName)
            putExtra(TaskReminderReceiver.EXTRA_TASK_TITLE, taskName)
            putExtra(TaskReminderReceiver.EXTRA_REMINDER_AT_MILLIS, reminderAtMillis)
            putExtra(TaskReminderReceiver.EXTRA_TRIGGER_AT_MILLIS, triggerAtMillis)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCodeForTaskId(taskId),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val TAG = "AlarmTaskReminderScheduler"
        // Reminders up to 10 minutes in the past still fire immediately (1 s).
        // Older ones are skipped — prevents notification spam after long reboots.
        internal const val PAST_GRACE_MILLIS = 10 * 60 * 1_000L

        internal enum class AlarmMethod(val logName: String) {
            EXACT("exact"),
            INEXACT_FALLBACK("inexact fallback")
        }

        internal fun calculateTriggerMillis(now: Long, reminderAtMillis: Long): Long? {
            val actualDelayMillis = reminderAtMillis - now
            return when {
                actualDelayMillis > 0L -> reminderAtMillis
                -actualDelayMillis <= PAST_GRACE_MILLIS -> now + 1_000L
                else -> null
            }
        }

        internal fun selectAlarmMethod(exactAlarmAllowed: Boolean): AlarmMethod =
            if (exactAlarmAllowed) AlarmMethod.EXACT else AlarmMethod.INEXACT_FALLBACK

        internal fun requestCodeForTaskId(taskId: Long): Int = taskId.hashCode()
    }
}
