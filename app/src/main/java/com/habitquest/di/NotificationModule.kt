package com.habitquest.di

import com.habitquest.notification.AlarmTaskReminderScheduler
import com.habitquest.notification.TaskReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    @Binds
    abstract fun bindTaskReminderScheduler(
        scheduler: AlarmTaskReminderScheduler
    ): TaskReminderScheduler
}
