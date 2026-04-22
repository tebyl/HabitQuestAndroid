package com.habitquest.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.habitquest.data.local.HabitQuestDatabase
import com.habitquest.data.local.dao.HabitDao
import com.habitquest.data.local.dao.TaskDao
import com.habitquest.data.local.dao.UserStatsDao
import com.habitquest.data.local.entity.UserStatsEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HabitQuestDatabase {
        var db: HabitQuestDatabase? = null

        fun seedData() {
            CoroutineScope(Dispatchers.IO).launch {
                db?.let { instance ->
                    instance.habitDao().insertHabits(HabitQuestDatabase.defaultHabits())
                    instance.userStatsDao().insertOrUpdate(UserStatsEntity(totalXP = 0))
                    HabitQuestDatabase.defaultTasks().forEach { task ->
                        instance.taskDao().insertTask(task)
                    }
                }
            }
        }

        val callback = object : RoomDatabase.Callback() {
            override fun onCreate(database: SupportSQLiteDatabase) {
                super.onCreate(database)
                seedData()
            }

            override fun onDestructiveMigration(database: SupportSQLiteDatabase) {
                super.onDestructiveMigration(database)
                seedData()
            }
        }

        db = Room.databaseBuilder(
            context,
            HabitQuestDatabase::class.java,
            "habit_quest.db"
        )
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
        return db
    }

    @Provides
    fun provideHabitDao(db: HabitQuestDatabase): HabitDao = db.habitDao()

    @Provides
    fun provideUserStatsDao(db: HabitQuestDatabase): UserStatsDao = db.userStatsDao()

    @Provides
    fun provideTaskDao(db: HabitQuestDatabase): TaskDao = db.taskDao()
}
