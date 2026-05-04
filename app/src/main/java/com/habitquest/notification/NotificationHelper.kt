package com.habitquest.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.habitquest.MainActivity
import com.habitquest.R

object NotificationHelper {

    private const val TAG = "HabitQuestNotifications"
    private val OLD_CHANNEL_IDS = listOf("habit_reminders", "habit_reminders_high_v2")
    const val CHANNEL_ID = "habit_reminders_high_v3"
    const val CHANNEL_NAME = "Recordatorios"

    fun createChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        OLD_CHANNEL_IDS.forEach { oldChannelId ->
            if (manager.getNotificationChannel(oldChannelId) != null) {
                manager.deleteNotificationChannel(oldChannelId)
                Log.d(TAG, "Deleted old notification channel: $oldChannelId")
            }
        }

        val existingChannel = manager.getNotificationChannel(CHANNEL_ID)
        if (existingChannel != null) {
            Log.d(TAG, "Notification channel already exists: $CHANNEL_ID importance=${existingChannel.importance}")
            return
        }

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones para recordar tus hábitos y tareas"
            enableVibration(true)
            setShowBadge(true)
        }
        manager.createNotificationChannel(channel)
        Log.d(TAG, "Notification channel ready: $CHANNEL_ID importance=HIGH")
    }

    fun buildHabitReminder(context: Context, habitName: String): android.app.Notification =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_habitquest)
            .setLargeIcon(appLogo(context))
            .setColor(0xFFF59E0B.toInt())
            .setContentTitle("Hora de tu hábito")
            .setContentText("No olvides completar: $habitName")
            .setStyle(NotificationCompat.BigTextStyle().bigText("No olvides completar: $habitName"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(openAppIntent(context))
            .setAutoCancel(true)
            .build()

    fun buildTaskReminder(context: Context, taskName: String): android.app.Notification =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_habitquest)
            .setLargeIcon(appLogo(context))
            .setColor(0xFF8B5CF6.toInt())
            .setContentTitle("Tu tarea te espera")
            .setContentText(taskName)
            .setSubText("HabitQuest")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Un paso pequeño ahora: $taskName"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(openAppIntent(context))
            .setAutoCancel(true)
            .build()

    fun showTaskReminder(context: Context, taskId: Long, taskName: String) {
        createChannel(context)
        val notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        val channelImportance = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .getNotificationChannel(CHANNEL_ID)
            ?.importance
        Log.d(
            TAG,
            "Showing task notification taskId=$taskId title=$taskName notificationsEnabled=$notificationsEnabled channelImportance=$channelImportance"
        )
        NotificationManagerCompat.from(context).notify(
            taskId.toInt(),
            buildTaskReminder(context, taskName)
        )
    }

    private fun appLogo(context: Context) =
        BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_round)

    private fun openAppIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
