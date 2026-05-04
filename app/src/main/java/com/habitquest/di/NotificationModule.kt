package com.habitquest.di

import com.habitquest.notification.TaskReminderScheduler
import com.habitquest.notification.WorkManagerTaskReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    @Binds
    abstract fun bindTaskReminderScheduler(
        scheduler: WorkManagerTaskReminderScheduler
    ): TaskReminderScheduler
}
