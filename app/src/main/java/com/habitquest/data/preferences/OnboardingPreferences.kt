package com.habitquest.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.onboardingDataStore by preferencesDataStore(name = "onboarding_preferences")

class OnboardingPreferences(
    private val context: Context
) {
    val hasSeenOnboarding: Flow<Boolean?> =
        context.onboardingDataStore.data.map { preferences ->
            preferences[HasSeenOnboardingKey]
        }

    suspend fun setHasSeenOnboarding(value: Boolean) {
        context.onboardingDataStore.edit { preferences ->
            preferences[HasSeenOnboardingKey] = value
        }
    }

    private companion object {
        val HasSeenOnboardingKey = booleanPreferencesKey("has_seen_onboarding")
    }
}
