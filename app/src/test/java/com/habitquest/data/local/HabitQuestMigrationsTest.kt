package com.habitquest.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class HabitQuestMigrationsTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        HabitQuestDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migration6To7_preservesExistingTasksAndAddsReminderDefaults() {
        helper.createDatabase(TEST_DB, 6).apply {
            execSQL(
                """
                INSERT INTO tasks (
                    id, name, category, isCompleted, createdAt, completedAt, scheduledDate
                ) VALUES (
                    1, 'Legacy task', 'vida_diaria', 0, 1777850000000, NULL, '2026-05-04'
                )
                """.trimIndent()
            )
            close()
        }

        helper.runMigrationsAndValidate(TEST_DB, 7, true, HabitQuestMigrations.MIGRATION_6_7)
            .query("SELECT name, reminderAtMillis, reminderEnabled, reminderWorkId FROM tasks WHERE id = 1")
            .use { cursor ->
                cursor.moveToFirst()
                assertEquals("Legacy task", cursor.getString(0))
                assertNull(cursor.getString(1))
                assertEquals(0, cursor.getInt(2))
                assertNull(cursor.getString(3))
            }
    }

    private companion object {
        const val TEST_DB = "habitquest-migration-test"
    }
}
