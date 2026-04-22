package com.habitquest.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.habitquest.data.repository.HabitRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class HabitReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: HabitRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        NotificationHelper.createChannel(applicationContext)

        val habits = repository.getHabits().first()
        val pending = habits.filter { !it.completedToday && it.reminderEnabled }

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as android.app.NotificationManager

        pending.forEachIndexed { index, habit ->
            manager.notify(
                habit.id.toInt(),
                NotificationHelper.buildHabitReminder(applicationContext, habit.name)
            )
        }

        return Result.success()
    }

    companion object {
        const val WORK_TAG = "habit_daily_reminder"

        fun schedule(context: Context) {
            val request = androidx.work.PeriodicWorkRequestBuilder<HabitReminderWorker>(
                repeatInterval = 24,
                repeatIntervalTimeUnit = java.util.concurrent.TimeUnit.HOURS
            )
                .addTag(WORK_TAG)
                .setInitialDelay(
                    calculateInitialDelay(),
                    java.util.concurrent.TimeUnit.MILLISECONDS
                )
                .build()

            androidx.work.WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_TAG,
                    androidx.work.ExistingPeriodicWorkPolicy.UPDATE,
                    request
                )
        }

        private fun calculateInitialDelay(): Long {
            val now = java.util.Calendar.getInstance()
            val target = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 9)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                if (before(now)) add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
            return target.timeInMillis - now.timeInMillis
        }
    }
}
