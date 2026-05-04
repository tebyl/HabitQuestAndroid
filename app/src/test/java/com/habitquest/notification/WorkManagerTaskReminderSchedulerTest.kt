package com.habitquest.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WorkManagerTaskReminderSchedulerTest {

    @Test
    fun calculateDelayMillis_debugBuild_forcesFiveSecondDelayForFutureReminder() {
        val delay = WorkManagerTaskReminderScheduler.calculateDelayMillis(
            now = 1_000L,
            reminderAtMillis = 30_000L,
            debugBuild = true
        )

        assertEquals(5_000L, delay)
    }

    @Test
    fun calculateDelayMillis_currentMinuteGrace_schedulesInsteadOfSkipping() {
        val delay = WorkManagerTaskReminderScheduler.calculateDelayMillis(
            now = 61_000L,
            reminderAtMillis = 60_000L,
            debugBuild = true
        )

        assertEquals(5_000L, delay)
    }

    @Test
    fun calculateDelayMillis_oldPastReminder_isSkipped() {
        val delay = WorkManagerTaskReminderScheduler.calculateDelayMillis(
            now = 180_000L,
            reminderAtMillis = 60_000L,
            debugBuild = true
        )

        assertNull(delay)
    }
}
