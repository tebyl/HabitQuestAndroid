package com.habitquest.notification

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskReminderRestoreWorkerTest {

    @Test
    fun shouldRestoreTaskReminder_futureReminder_isRestored() {
        assertTrue(TaskReminderRestoreWorker.shouldRestoreTaskReminder(now = 1_000L, reminderAtMillis = 2_000L))
    }

    @Test
    fun shouldRestoreTaskReminder_overdueReminder_isNotRestored() {
        assertFalse(TaskReminderRestoreWorker.shouldRestoreTaskReminder(now = 2_000L, reminderAtMillis = 1_000L))
    }
}
