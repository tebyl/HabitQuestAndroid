package com.habitquest.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlarmTaskReminderSchedulerTest {

    @Test
    fun calculateTriggerMillis_futureReminder_usesActualTime() {
        val result = AlarmTaskReminderScheduler.calculateTriggerMillis(
            now = 1_000L,
            reminderAtMillis = 30_000L
        )
        assertEquals(30_000L, result)
    }

    @Test
    fun calculateTriggerMillis_recentPastWithinGrace_schedulesInOneSecond() {
        val now = 120_000L
        val result = AlarmTaskReminderScheduler.calculateTriggerMillis(
            now = now,
            reminderAtMillis = 60_000L
        )
        assertEquals(now + 1_000L, result)
    }

    @Test
    fun calculateTriggerMillis_staleReminder_isSkipped() {
        // now - reminderAtMillis = 640_000ms > PAST_GRACE_MILLIS (10 min = 600_000ms)
        val result = AlarmTaskReminderScheduler.calculateTriggerMillis(
            now = 700_000L,
            reminderAtMillis = 60_000L
        )
        assertNull(result)
    }

    @Test
    fun selectAlarmMethod_exactAlarmAllowed_usesExact() {
        val result = AlarmTaskReminderScheduler.selectAlarmMethod(exactAlarmAllowed = true)

        assertEquals("exact", result.logName)
    }

    @Test
    fun selectAlarmMethod_exactAlarmNotAllowed_usesInexactFallback() {
        val result = AlarmTaskReminderScheduler.selectAlarmMethod(exactAlarmAllowed = false)

        assertEquals("inexact fallback", result.logName)
    }

    @Test
    fun requestCodeForTaskId_isStableForSameTaskId() {
        val first = AlarmTaskReminderScheduler.requestCodeForTaskId(42L)
        val second = AlarmTaskReminderScheduler.requestCodeForTaskId(42L)

        assertEquals(first, second)
    }
}
