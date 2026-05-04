package com.habitquest.ui.screen.home

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class HabitCreateSheetReminderTest {

    private val zone = ZoneId.of("UTC")
    private val today = LocalDate.of(2026, 5, 4)

    @Test
    fun calculateTaskReminderAtMillis_futureTimeToday_usesTodayAtSelectedTime() {
        val now = today.atTime(9, 0).atZone(zone).toInstant().toEpochMilli()
        val expected = today.atTime(10, 30).atZone(zone).toInstant().toEpochMilli()

        val result = calculateTaskReminderAtMillis(
            scheduledDate = today,
            selectedTime = LocalTime.of(10, 30),
            nowMillis = now,
            zone = zone
        )

        assertEquals(expected, result)
    }

    @Test
    fun calculateTaskReminderAtMillis_pastTimeToday_schedulesTomorrow() {
        val now = today.atTime(11, 0).atZone(zone).toInstant().toEpochMilli()
        val expected = today.plusDays(1).atTime(10, 30).atZone(zone).toInstant().toEpochMilli()

        val result = calculateTaskReminderAtMillis(
            scheduledDate = today,
            selectedTime = LocalTime.of(10, 30),
            nowMillis = now,
            zone = zone
        )

        assertEquals(expected, result)
    }

    @Test
    fun calculateTaskReminderAtMillis_veryCloseFuture_usesMinimumSafeDelay() {
        val now = today.atTime(9, 59, 30).atZone(zone).toInstant().toEpochMilli()
        val expected = now + TASK_REMINDER_MIN_SAFE_DELAY_MILLIS

        val result = calculateTaskReminderAtMillis(
            scheduledDate = today,
            selectedTime = LocalTime.of(10, 0),
            nowMillis = now,
            zone = zone
        )

        assertEquals(expected, result)
    }
}
