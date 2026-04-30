package com.habitquest.data.local

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DatabaseSeedPolicyTest {

    @Test
    fun firstRunDoesNotGenerateDefaultData() {
        assertFalse(DatabaseSeedPolicy.shouldSeedDefaults(isFirstRun = true))
    }

    @Test
    fun firstRunIsMarkedCompleteAfterDatabaseCreate() {
        assertTrue(DatabaseSeedPolicy.shouldMarkFirstRunComplete(isFirstRun = true))
    }
}
