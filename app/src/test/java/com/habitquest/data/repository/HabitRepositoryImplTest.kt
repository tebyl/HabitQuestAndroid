package com.habitquest.data.repository

import com.habitquest.data.local.dao.HabitDao
import com.habitquest.data.local.dao.TaskDao
import com.habitquest.data.local.dao.UserStatsDao
import com.habitquest.data.local.entity.HabitEntity
import com.habitquest.data.local.entity.TaskEntity
import com.habitquest.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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

    @Test
    fun completeTask_assignsCompletedAt() = runBlocking {
        val taskDao = FakeTaskDao()
        val taskId = taskDao.insertTask(
            TaskEntity(
                name = "Send report",
                category = "productividad"
            )
        )
        val statsDao = FakeUserStatsDao(UserStatsEntity(totalXP = 0))
        val repository = HabitRepositoryImpl(
            FakeHabitDao(HabitEntity(id = 1, name = "Habit", icon = "*", category = "vida_diaria")),
            statsDao,
            taskDao
        )

        val before = System.currentTimeMillis()
        repository.completeTask(taskId)

        val task = taskDao.getTaskById(taskId)
        assertEquals(true, task?.isCompleted)
        assertNotNull(task?.completedAt)
        assertTrue((task?.completedAt ?: 0L) >= before)
        assertEquals(30, statsDao.getUserStatsOnce()?.totalXP)
    }

    @Test
    fun uncompleteTask_clearsCompletedAt() = runBlocking {
        val taskDao = FakeTaskDao()
        val taskId = taskDao.insertTask(
            TaskEntity(
                name = "Send report",
                category = "productividad",
                isCompleted = true,
                completedAt = 1_777_000_000_000L
            )
        )
        val statsDao = FakeUserStatsDao(UserStatsEntity(totalXP = 30))
        val repository = HabitRepositoryImpl(
            FakeHabitDao(HabitEntity(id = 1, name = "Habit", icon = "*", category = "vida_diaria")),
            statsDao,
            taskDao
        )

        repository.uncompleteTask(taskId)

        val task = taskDao.getTaskById(taskId)
        assertEquals(false, task?.isCompleted)
        assertNull(task?.completedAt)
        assertEquals(0, statsDao.getUserStatsOnce()?.totalXP)
    }

    @Test
    fun updateHabit_changesNameAndCategoryWithoutResettingProgress() = runBlocking {
        val habitDao = FakeHabitDao(
            HabitEntity(
                id = 1,
                name = "Old",
                icon = "*",
                category = "vida_diaria",
                streakCount = 4,
                totalDays = 9,
                completedToday = true
            )
        )
        val repository = HabitRepositoryImpl(habitDao, FakeUserStatsDao(UserStatsEntity()), FakeTaskDao())

        repository.updateHabit(
            com.habitquest.domain.model.Habit(
                id = 1,
                name = "New",
                icon = "*",
                category = "salud",
                frequency = "weekly",
                streakCount = 4,
                totalDays = 9,
                completedToday = true
            )
        )

        val habit = repository.getHabits().first().single()
        assertEquals("New", habit.name)
        assertEquals("salud", habit.category)
        assertEquals("weekly", habit.frequency)
        assertEquals(4, habit.streakCount)
        assertEquals(9, habit.totalDays)
        assertEquals(true, habit.completedToday)
    }

    @Test
    fun deleteHabit_removesHabit() = runBlocking {
        val habitDao = FakeHabitDao(HabitEntity(id = 1, name = "Delete me", icon = "*", category = "vida_diaria"))
        val repository = HabitRepositoryImpl(habitDao, FakeUserStatsDao(UserStatsEntity()), FakeTaskDao())

        repository.deleteHabit(1)

        assertEquals(emptyList<com.habitquest.domain.model.Habit>(), repository.getHabits().first())
    }

    @Test
    fun updateTask_changesNameCategoryAndDate() = runBlocking {
        val taskDao = FakeTaskDao()
        val taskId = taskDao.insertTask(TaskEntity(name = "Old", category = "vida_diaria", scheduledDate = "2026-04-30"))
        val repository = HabitRepositoryImpl(
            FakeHabitDao(HabitEntity(id = 1, name = "Habit", icon = "*", category = "vida_diaria")),
            FakeUserStatsDao(UserStatsEntity()),
            taskDao
        )

        repository.updateTask(
            com.habitquest.domain.model.Task(
                id = taskId,
                name = "New",
                category = "salud",
                scheduledDate = "2026-05-10"
            )
        )

        val task = repository.getTasks().first().single()
        assertEquals("New", task.name)
        assertEquals("salud", task.category)
        assertEquals("2026-05-10", task.scheduledDate)
    }

    @Test
    fun deleteTask_removesTask() = runBlocking {
        val taskDao = FakeTaskDao()
        val taskId = taskDao.insertTask(TaskEntity(name = "Delete me", category = "vida_diaria"))
        val repository = HabitRepositoryImpl(
            FakeHabitDao(HabitEntity(id = 1, name = "Habit", icon = "*", category = "vida_diaria")),
            FakeUserStatsDao(UserStatsEntity()),
            taskDao
        )

        repository.deleteTask(taskId)

        assertEquals(emptyList<com.habitquest.domain.model.Task>(), repository.getTasks().first())
    }

    @Test
    fun addTask_doesNotCreateHabit() = runBlocking {
        val habitDao = FakeHabitDao()
        val taskDao = FakeTaskDao()
        val repository = HabitRepositoryImpl(habitDao, FakeUserStatsDao(UserStatsEntity()), taskDao)

        repository.addTask(
            com.habitquest.domain.model.Task(
                name = "Agenda",
                category = "mama_colegio",
                scheduledDate = "2026-04-30"
            )
        )

        assertEquals(0, repository.getHabits().first().size)
        assertEquals(1, repository.getTasks().first().size)
    }

    @Test
    fun addHabit_doesNotCreateTask() = runBlocking {
        val habitDao = FakeHabitDao()
        val taskDao = FakeTaskDao()
        val repository = HabitRepositoryImpl(habitDao, FakeUserStatsDao(UserStatsEntity()), taskDao)

        repository.addHabit(
            com.habitquest.domain.model.Habit(
                name = "Leer",
                icon = "",
                category = "desarrollo"
            )
        )

        assertEquals(1, repository.getHabits().first().size)
        assertEquals(0, repository.getTasks().first().size)
    }
}

private class FakeHabitDao(initialHabit: HabitEntity? = null) : HabitDao {
    private val habits = linkedMapOf<Long, HabitEntity>().apply {
        initialHabit?.let { put(it.id, it) }
    }
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

    override suspend fun setCompletion(taskId: Long, completed: Boolean, completedAt: Long?) {
        tasks[taskId]?.let {
            tasks[taskId] = it.copy(
                isCompleted = completed,
                completedAt = completedAt
            )
        }
        publish()
    }

    override suspend fun getTaskById(taskId: Long): TaskEntity? = tasks[taskId]

    private fun publish() {
        tasksFlow.value = tasks.values.toList()
    }
}
