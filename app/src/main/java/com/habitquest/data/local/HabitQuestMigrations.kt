package com.habitquest.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object HabitQuestMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            ensureCurrentSchema(db)
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            ensureCurrentSchema(db)
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            ensureCurrentSchema(db)
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            ensureCurrentSchema(db)
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            ensureCurrentSchema(db)
        }
    }

    private fun ensureCurrentSchema(db: SupportSQLiteDatabase) {
        ensureHabitsTable(db)
        ensureUserStatsTable(db)
        ensureTasksTable(db)
    }

    private fun ensureHabitsTable(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `habits` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `icon` TEXT NOT NULL,
                `category` TEXT NOT NULL,
                `frequency` TEXT NOT NULL,
                `streakCount` INTEGER NOT NULL,
                `totalDays` INTEGER NOT NULL,
                `completedToday` INTEGER NOT NULL,
                `lastCompletedDate` TEXT NOT NULL,
                `reminderEnabled` INTEGER NOT NULL,
                `reminderHour` INTEGER NOT NULL,
                `reminderMinute` INTEGER NOT NULL,
                `orderIndex` INTEGER NOT NULL,
                `createdAt` INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.addColumnIfMissing("habits", "frequency", "TEXT NOT NULL DEFAULT 'daily'")
        db.addColumnIfMissing("habits", "streakCount", "INTEGER NOT NULL DEFAULT 0")
        db.addColumnIfMissing("habits", "totalDays", "INTEGER NOT NULL DEFAULT 0")
        db.addColumnIfMissing("habits", "completedToday", "INTEGER NOT NULL DEFAULT 0")
        db.addColumnIfMissing("habits", "lastCompletedDate", "TEXT NOT NULL DEFAULT ''")
        db.addColumnIfMissing("habits", "reminderEnabled", "INTEGER NOT NULL DEFAULT 0")
        db.addColumnIfMissing("habits", "reminderHour", "INTEGER NOT NULL DEFAULT 8")
        db.addColumnIfMissing("habits", "reminderMinute", "INTEGER NOT NULL DEFAULT 0")
        db.addColumnIfMissing("habits", "orderIndex", "INTEGER NOT NULL DEFAULT 0")
        db.addColumnIfMissing("habits", "createdAt", "INTEGER NOT NULL DEFAULT 0")
        db.normalizeHabitsTable()
    }

    private fun ensureUserStatsTable(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `user_stats` (
                `id` INTEGER NOT NULL,
                `totalXP` INTEGER NOT NULL,
                `lastDailyReset` TEXT NOT NULL,
                `userName` TEXT NOT NULL,
                `userAvatar` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )

        db.addColumnIfMissing("user_stats", "lastDailyReset", "TEXT NOT NULL DEFAULT ''")
        db.addColumnIfMissing("user_stats", "userName", "TEXT NOT NULL DEFAULT 'Tu espacio'")
        db.addColumnIfMissing("user_stats", "userAvatar", "TEXT NOT NULL DEFAULT ''")
        db.normalizeUserStatsTable()
    }

    private fun ensureTasksTable(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `tasks` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `category` TEXT NOT NULL,
                `isCompleted` INTEGER NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `completedAt` INTEGER,
                `scheduledDate` TEXT
            )
            """.trimIndent()
        )

        db.addColumnIfMissing("tasks", "isCompleted", "INTEGER NOT NULL DEFAULT 0")
        db.addColumnIfMissing("tasks", "createdAt", "INTEGER NOT NULL DEFAULT 0")
        db.addColumnIfMissing("tasks", "completedAt", "INTEGER DEFAULT NULL")
        db.addColumnIfMissing("tasks", "scheduledDate", "TEXT DEFAULT NULL")
        db.normalizeTasksTable()
    }

    private fun SupportSQLiteDatabase.normalizeHabitsTable() {
        execSQL(
            """
            CREATE TABLE IF NOT EXISTS `habits_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `icon` TEXT NOT NULL,
                `category` TEXT NOT NULL,
                `frequency` TEXT NOT NULL,
                `streakCount` INTEGER NOT NULL,
                `totalDays` INTEGER NOT NULL,
                `completedToday` INTEGER NOT NULL,
                `lastCompletedDate` TEXT NOT NULL,
                `reminderEnabled` INTEGER NOT NULL,
                `reminderHour` INTEGER NOT NULL,
                `reminderMinute` INTEGER NOT NULL,
                `orderIndex` INTEGER NOT NULL,
                `createdAt` INTEGER NOT NULL
            )
            """.trimIndent()
        )
        execSQL(
            """
            INSERT INTO `habits_new` (
                `id`, `name`, `icon`, `category`, `frequency`, `streakCount`, `totalDays`,
                `completedToday`, `lastCompletedDate`, `reminderEnabled`, `reminderHour`,
                `reminderMinute`, `orderIndex`, `createdAt`
            )
            SELECT
                `id`, `name`, `icon`, `category`, `frequency`, `streakCount`, `totalDays`,
                `completedToday`, `lastCompletedDate`, `reminderEnabled`, `reminderHour`,
                `reminderMinute`, `orderIndex`, `createdAt`
            FROM `habits`
            """.trimIndent()
        )
        execSQL("DROP TABLE `habits`")
        execSQL("ALTER TABLE `habits_new` RENAME TO `habits`")
    }

    private fun SupportSQLiteDatabase.normalizeUserStatsTable() {
        execSQL(
            """
            CREATE TABLE IF NOT EXISTS `user_stats_new` (
                `id` INTEGER NOT NULL,
                `totalXP` INTEGER NOT NULL,
                `lastDailyReset` TEXT NOT NULL,
                `userName` TEXT NOT NULL,
                `userAvatar` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        execSQL(
            """
            INSERT INTO `user_stats_new` (
                `id`, `totalXP`, `lastDailyReset`, `userName`, `userAvatar`
            )
            SELECT `id`, `totalXP`, `lastDailyReset`, `userName`, `userAvatar`
            FROM `user_stats`
            """.trimIndent()
        )
        execSQL("DROP TABLE `user_stats`")
        execSQL("ALTER TABLE `user_stats_new` RENAME TO `user_stats`")
    }

    private fun SupportSQLiteDatabase.normalizeTasksTable() {
        execSQL(
            """
            CREATE TABLE IF NOT EXISTS `tasks_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `category` TEXT NOT NULL,
                `isCompleted` INTEGER NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `completedAt` INTEGER,
                `scheduledDate` TEXT
            )
            """.trimIndent()
        )
        execSQL(
            """
            INSERT INTO `tasks_new` (`id`, `name`, `category`, `isCompleted`, `createdAt`, `completedAt`, `scheduledDate`)
            SELECT `id`, `name`, `category`, `isCompleted`, `createdAt`, `completedAt`, `scheduledDate`
            FROM `tasks`
            """.trimIndent()
        )
        execSQL("DROP TABLE `tasks`")
        execSQL("ALTER TABLE `tasks_new` RENAME TO `tasks`")
    }

    private fun SupportSQLiteDatabase.addColumnIfMissing(
        tableName: String,
        columnName: String,
        definition: String
    ) {
        if (!hasColumn(tableName, columnName)) {
            execSQL("ALTER TABLE `$tableName` ADD COLUMN `$columnName` $definition")
        }
    }

    private fun SupportSQLiteDatabase.hasColumn(tableName: String, columnName: String): Boolean {
        query("PRAGMA table_info(`$tableName`)").use { cursor ->
            val nameIndex = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                if (cursor.getString(nameIndex) == columnName) return true
            }
        }
        return false
    }
}
