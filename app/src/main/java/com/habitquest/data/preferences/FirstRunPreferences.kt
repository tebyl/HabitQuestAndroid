package com.habitquest.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.firstRunDataStore by preferencesDataStore(name = "first_run_preferences")

class FirstRunPreferences(
    private val context: Context
) {
    suspend fun isFirstRun(): Boolean =
        context.firstRunDataStore.data
            .map { preferences -> preferences[IsFirstRunKey] ?: true }
            .first()

    suspend fun setFirstRun(value: Boolean) {
        context.firstRunDataStore.edit { preferences ->
            preferences[IsFirstRunKey] = value
        }
    }

    private companion object {
        val IsFirstRunKey = booleanPreferencesKey("is_first_run")
    }
}
