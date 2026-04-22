package com.habitquest.data.repository

import com.habitquest.data.local.dao.HabitDao
import com.habitquest.data.local.dao.TaskDao
import com.habitquest.data.local.dao.UserStatsDao
import com.habitquest.data.local.entity.HabitEntity
import com.habitquest.data.local.entity.TaskEntity
import com.habitquest.data.local.entity.UserStatsEntity
import com.habitquest.domain.model.Habit
import com.habitquest.domain.model.Task
import com.habitquest.domain.model.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val userStatsDao: UserStatsDao,
    private val taskDao: TaskDao
) : HabitRepository {

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    companion object {
        val CATEGORY_HABIT_XP = mapOf(
            "productividad" to 40,
            "salud_fisica"  to 35,
            "desarrollo"    to 35,
            "salud_mental"  to 30,
            "vida_diaria"   to 20
        )
        const val XP_PER_TASK            = 30
        const val STREAK_BONUS           = 25
        const val STREAK_BONUS_THRESHOLD = 3

        fun xpForCategory(category: String) = CATEGORY_HABIT_XP[category] ?: 30
    }

    override fun getHabits(): Flow<List<Habit>> =
        habitDao.getAllHabits().map { list -> list.map { it.toDomain() } }

    override fun getUserStats(): Flow<UserStats> =
        userStatsDao.getUserStats().map { it?.toDomain() ?: UserStats() }

    override fun getTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map { list -> list.map { it.toDomain() } }

    override suspend fun completeHabit(habitId: Long): Int {
        val habit = habitDao.getHabitById(habitId) ?: return 0
        if (habit.completedToday) return 0

        val today     = LocalDate.now().format(dateFormatter)
        val yesterday = LocalDate.now().minusDays(1).format(dateFormatter)

        val newStreak = if (habit.lastCompletedDate == yesterday) habit.streakCount + 1 else 1
        val xpGained  = xpForCategory(habit.category) + if (newStreak >= STREAK_BONUS_THRESHOLD) STREAK_BONUS else 0

        habitDao.updateHabit(
            habit.copy(
                completedToday    = true,
                streakCount       = newStreak,
                totalDays         = habit.totalDays + 1,
                lastCompletedDate = today
            )
        )

        val stats = userStatsDao.getUserStatsOnce()
        if (stats == null) {
            userStatsDao.insertOrUpdate(UserStatsEntity(totalXP = xpGained, lastDailyReset = today))
        } else {
            userStatsDao.addXP(xpGained)
        }

        return xpGained
    }

    override suspend fun uncompleteHabit(habitId: Long) {
        val habit = habitDao.getHabitById(habitId) ?: return
        if (!habit.completedToday) return

        val xpToSubtract = xpForCategory(habit.category) +
            if (habit.streakCount >= STREAK_BONUS_THRESHOLD) STREAK_BONUS else 0

        val newStreak   = (habit.streakCount - 1).coerceAtLeast(0)
        val yesterday   = LocalDate.now().minusDays(1).format(dateFormatter)
        val newLastDate = if (newStreak > 0) yesterday else ""

        habitDao.updateHabit(
            habit.copy(
                completedToday    = false,
                streakCount       = newStreak,
                totalDays         = (habit.totalDays - 1).coerceAtLeast(0),
                lastCompletedDate = newLastDate
            )
        )
        userStatsDao.addXP(-xpToSubtract)
    }

    override suspend fun resetDailyHabitsIfNeeded() {
        val today = LocalDate.now().format(dateFormatter)
        val stats = userStatsDao.getUserStatsOnce()

        if (stats == null) {
            userStatsDao.insertOrUpdate(UserStatsEntity(lastDailyReset = today))
            return
        }

        if (stats.lastDailyReset != today) {
            habitDao.resetDailyCompletions()
            userStatsDao.insertOrUpdate(stats.copy(lastDailyReset = today))
        }
    }

    override suspend fun addHabit(habit: Habit): Long =
        habitDao.insertHabit(habit.toEntity())

    override suspend fun deleteHabit(habitId: Long) {
        habitDao.getHabitById(habitId)?.let { habitDao.deleteHabit(it) }
    }

    override suspend fun updateUserName(name: String) {
        userStatsDao.updateUserName(name)
    }

    override suspend fun updateUserAvatar(avatar: String) {
        userStatsDao.updateUserAvatar(avatar)
    }

    override suspend fun addTask(task: Task): Long =
        taskDao.insertTask(task.toEntity())

    override suspend fun completeTask(taskId: Long) {
        val task = taskDao.getTaskById(taskId) ?: return
        if (task.isCompleted) return
        taskDao.setCompleted(taskId, true)
        val today = LocalDate.now().format(dateFormatter)
        val stats = userStatsDao.getUserStatsOnce()
        if (stats == null) {
            userStatsDao.insertOrUpdate(UserStatsEntity(totalXP = XP_PER_TASK, lastDailyReset = today))
        } else {
            userStatsDao.addXP(XP_PER_TASK)
        }
    }

    override suspend fun uncompleteTask(taskId: Long) {
        val task = taskDao.getTaskById(taskId) ?: return
        if (!task.isCompleted) return
        taskDao.setCompleted(taskId, false)
        userStatsDao.addXP(-XP_PER_TASK)
    }

    override suspend fun deleteTask(taskId: Long) {
        taskDao.getTaskById(taskId)?.let { taskDao.deleteTask(it) }
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private fun HabitEntity.toDomain() = Habit(
        id = id, name = name, icon = icon, category = category,
        streakCount = streakCount, totalDays = totalDays,
        completedToday = completedToday, lastCompletedDate = lastCompletedDate,
        reminderEnabled = reminderEnabled, reminderHour = reminderHour,
        reminderMinute = reminderMinute
    )

    private fun Habit.toEntity() = HabitEntity(
        id = id, name = name, icon = icon, category = category,
        streakCount = streakCount, totalDays = totalDays,
        completedToday = completedToday, lastCompletedDate = lastCompletedDate,
        reminderEnabled = reminderEnabled, reminderHour = reminderHour,
        reminderMinute = reminderMinute
    )

    private fun TaskEntity.toDomain() = Task(
        id = id, name = name, category = category,
        isCompleted = isCompleted, createdAt = createdAt
    )

    private fun Task.toEntity() = TaskEntity(
        id = id, name = name, category = category,
        isCompleted = isCompleted, createdAt = createdAt
    )

    private fun UserStatsEntity.toDomain() = UserStats(
        totalXP = totalXP, userName = userName, userAvatar = userAvatar
    )
}
