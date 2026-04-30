package com.habitquest.data.local

internal object DatabaseSeedPolicy {
    fun shouldSeedDefaults(isFirstRun: Boolean): Boolean = false

    fun shouldMarkFirstRunComplete(isFirstRun: Boolean): Boolean = isFirstRun
}
