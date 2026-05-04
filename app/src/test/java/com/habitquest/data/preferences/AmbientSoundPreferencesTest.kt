package com.habitquest.data.preferences

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AmbientSoundPreferencesTest {

    @Test
    fun ambientSoundToggle_persistsOnAndOff() = runBlocking {
        val preferences = AmbientSoundPreferences(ApplicationProvider.getApplicationContext())

        preferences.setAmbientSoundEnabled(false)
        assertFalse(preferences.ambientSoundEnabled.first())

        preferences.setAmbientSoundEnabled(true)
        assertTrue(preferences.ambientSoundEnabled.first())

        preferences.setAmbientSoundEnabled(false)
        assertFalse(preferences.ambientSoundEnabled.first())
    }
}
