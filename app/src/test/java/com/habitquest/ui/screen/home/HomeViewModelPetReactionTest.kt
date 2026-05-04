package com.habitquest.ui.screen.home

import com.habitquest.data.repository.HabitRepository
import com.habitquest.domain.gamification.PetReactionState
import com.habitquest.domain.model.Habit
import com.habitquest.domain.model.Task
import com.habitquest.domain.model.UserStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelPetReactionTest {

    @get:Rule
    val mainDispatcherRule: TestWatcher = MainDispatcherRule()

    @Test
    fun completeTask_emitsHappyAndReturnsToIdleAfterTimeout() = runTest {
        val repository = FakeHabitRepository(
            stats = UserStats(totalXP = 0),
            tasks = listOf(Task(id = 1, name = "Task", category = "general"))
        )
        val viewModel = HomeViewModel(repository)
        advanceUntilIdle()

        viewModel.completeTask(1)
        runCurrent()

        assertEquals(PetReactionState.HAPPY, viewModel.uiState.value.petReactionState)

        advanceTimeBy(2_500L)
        runCurrent()

        assertEquals(PetReactionState.IDLE, viewModel.uiState.value.petReactionState)
    }

    @Test
    fun completeTask_levelIncrease_emitsLevelUp() = runTest {
        val repository = FakeHabitRepository(
            stats = UserStats(totalXP = 190),
            tasks = listOf(Task(id = 1, name = "Task", category = "general"))
        )
        val viewModel = HomeViewModel(repository)
        advanceUntilIdle()

        viewModel.completeTask(1)
        runCurrent()

        assertEquals(PetReactionState.LEVEL_UP, viewModel.uiState.value.petReactionState)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private class MainDispatcherRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

private class FakeHabitRepository(
    habits: List<Habit> = emptyList(),
    stats: UserStats = UserStats(),
    tasks: List<Task> = emptyList()
) : HabitRepository {
    private val habitsFlow = MutableStateFlow(habits)
    private val statsFlow = MutableStateFlow(stats)
    private val tasksFlow = MutableStateFlow(tasks)

    override fun getHabits(): Flow<List<Habit>> = habitsFlow
    override fun getUserStats(): Flow<UserStats> = statsFlow
    override fun getTasks(): Flow<List<Task>> = tasksFlow
    override suspend fun resetDailyHabitsIfNeeded() = Unit

    override suspend fun completeTask(taskId: Long) {
        tasksFlow.value = tasksFlow.value.map {
            if (it.id == taskId) it.copy(isCompleted = true) else it
        }
        statsFlow.value = statsFlow.value.copy(totalXP = statsFlow.value.totalXP + 50)
    }

    override suspend fun completeHabit(habitId: Long): Int = 50
    override suspend fun addHabit(habit: Habit): Long = 0L
    override suspend fun updateHabit(habit: Habit) = Unit
    override suspend fun deleteHabit(habitId: Long) = Unit
    override suspend fun updateUserName(name: String) = Unit
    override suspend fun updateUserAvatar(avatar: String) = Unit
    override suspend fun addTask(task: Task): Long = 0L
    override suspend fun updateTask(task: Task) = Unit
    override suspend fun deleteTask(taskId: Long) = Unit
    override suspend fun uncompleteHabit(habitId: Long) = Unit
    override suspend fun uncompleteTask(taskId: Long) = Unit
}
