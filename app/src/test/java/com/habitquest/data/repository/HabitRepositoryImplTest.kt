package com.habitquest.data.repository

import com.habitquest.data.local.dao.HabitDao
import com.habitquest.data.local.dao.TaskDao
import com.habitquest.data.local.dao.UserStatsDao
import com.habitquest.data.local.entity.HabitEntity
import com.habitquest.data.local.entity.TaskEntity
import com.habitquest.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class HabitRepositoryImplTest {
    @Test
    fun completeHabit_addsXpOnlyOnce() = runBlocking {
        val habitDao = FakeHabitDao(
            HabitEntity(
                id = 1,
                name = "Meditate",
                icon = "*",
                category = "salud_mental"
            )
        )
        val statsDao = FakeUserStatsDao(UserStatsEntity(totalXP = 0))
        val repository = HabitRepositoryImpl(habitDao, statsDao, FakeTaskDao())

        val firstGain = repository.completeHabit(1)
        val secondGain = repository.completeHabit(1)

        assertEquals(30, firstGain)
        assertEquals(0, secondGain)
        assertEquals(30, statsDao.getUserStatsOnce()?.totalXP)
    }

    @Test
    fun uncompleteHabit_subtractsXpWithoutGoingNegative() = runBlocking {
        val habitDao = FakeHabitDao(
            HabitEntity(
                id = 1,
                name = "Workout",
                icon = "*",
                category = "productividad",
                streakCount = 1,
                totalDays = 1,
                completedToday = true
            )
        )
        val statsDao = FakeUserStatsDao(UserStatsEntity(totalXP = 10))
        val repository = HabitRepositoryImpl(habitDao, statsDao, FakeTaskDao())

        repository.uncompleteHabit(1)

        assertEquals(0, statsDao.getUserStatsOnce()?.totalXP)
        assertEquals(false, habitDao.getHabitById(1)?.completedToday)
    }
}

private class FakeHabitDao(initialHabit: HabitEntity) : HabitDao {
    private val habits = linkedMapOf(initialHabit.id to initialHabit)
    private val habitsFlow = MutableStateFlow(habits.values.toList())

    override fun getAllHabits(): Flow<List<HabitEntity>> = habitsFlow

    override suspend fun getHabitById(id: Long): HabitEntity? = habits[id]

    override suspend fun insertHabit(habit: HabitEntity): Long {
        val id = if (habit.id == 0L) ((habits.keys.maxOrNull() ?: 0L) + 1L) else habit.id
        habits[id] = habit.copy(id = id)
        publish()
        return id
    }

    override suspend fun insertHabits(habits: List<HabitEntity>) {
        habits.forEach { insertHabit(it) }
    }

    override suspend fun updateHabit(habit: HabitEntity) {
        habits[habit.id] = habit
        publish()
    }

    override suspend fun deleteHabit(habit: HabitEntity) {
        habits.remove(habit.id)
        publish()
    }

    override suspend fun resetDailyCompletions() {
        habits.replaceAll { _, habit -> habit.copy(completedToday = false) }
        publish()
    }

    override suspend fun getHabitCount(): Int = habits.size

    private fun publish() {
        habitsFlow.value = habits.values.toList()
    }
}

private class FakeUserStatsDao(initialStats: UserStatsEntity?) : UserStatsDao {
    private val statsFlow = MutableStateFlow(initialStats)

    override fun getUserStats(): Flow<UserStatsEntity?> = statsFlow

    override suspend fun getUserStatsOnce(): UserStatsEntity? = statsFlow.value

    override suspend fun insertOrUpdate(stats: UserStatsEntity) {
        statsFlow.value = stats
    }

    override suspend fun addXP(amount: Int) {
        val current = statsFlow.value ?: return
        statsFlow.value = current.copy(totalXP = (current.totalXP + amount).coerceAtLeast(0))
    }

    override suspend fun updateUserName(name: String) {
        val current = statsFlow.value ?: return
        statsFlow.value = current.copy(userName = name)
    }

    override suspend fun updateUserAvatar(avatar: String) {
        val current = statsFlow.value ?: return
        statsFlow.value = current.copy(userAvatar = avatar)
    }
}

private class FakeTaskDao : TaskDao {
    private val tasks = linkedMapOf<Long, TaskEntity>()
    private val tasksFlow = MutableStateFlow(emptyList<TaskEntity>())

    override fun getAllTasks(): Flow<List<TaskEntity>> = tasksFlow

    override suspend fun insertTask(task: TaskEntity): Long {
        val id = if (task.id == 0L) ((tasks.keys.maxOrNull() ?: 0L) + 1L) else task.id
        tasks[id] = task.copy(id = id)
        publish()
        return id
    }

    override suspend fun updateTask(task: TaskEntity) {
        tasks[task.id] = task
        publish()
    }

    override suspend fun deleteTask(task: TaskEntity) {
        tasks.remove(task.id)
        publish()
    }

    override suspend fun setCompleted(taskId: Long, completed: Boolean) {
        tasks[taskId]?.let { tasks[taskId] = it.copy(isCompleted = completed) }
        publish()
    }

    override suspend fun getTaskById(taskId: Long): TaskEntity? = tasks[taskId]

    private fun publish() {
        tasksFlow.value = tasks.values.toList()
    }
}
