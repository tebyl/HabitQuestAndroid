package com.habitquest.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val appContext = context.applicationContext
            Log.d(TAG, "BOOT_COMPLETED received; restoring future reminders only")
            HabitReminderWorker.schedule(appContext)
            TaskReminderRestoreWorker.enqueue(appContext)
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}
