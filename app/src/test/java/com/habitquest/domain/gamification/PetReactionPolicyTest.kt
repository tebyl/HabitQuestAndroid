package com.habitquest.domain.gamification

import org.junit.Assert.assertEquals
import org.junit.Test

class PetReactionPolicyTest {

    @Test
    fun detect_completedTask_emitsHappy() {
        val previous = snapshot(completedTasks = 0)
        val current = snapshot(completedTasks = 1, xp = 50)

        assertEquals(PetReactionState.HAPPY, PetReactionPolicy.detect(previous, current))
    }

    @Test
    fun detect_levelIncrease_emitsLevelUp() {
        val previous = snapshot(xp = 290, level = 1)
        val current = snapshot(xp = 310, level = 2)

        assertEquals(PetReactionState.LEVEL_UP, PetReactionPolicy.detect(previous, current))
    }

    @Test
    fun detect_stageIncrease_emitsProud() {
        val previous = snapshot(xp = 250, level = 1)
        val current = snapshot(xp = 310, level = 1)

        assertEquals(PetReactionState.PROUD, PetReactionPolicy.detect(previous, current))
    }

    @Test
    fun detect_streakIncrease_emitsStreak() {
        val previous = snapshot(streak = 2, completedHabits = 8)
        val current = snapshot(streak = 3, completedHabits = 8)

        assertEquals(PetReactionState.STREAK, PetReactionPolicy.detect(previous, current))
    }

    private fun snapshot(
        xp: Int = 0,
        level: Int = 1,
        streak: Int = 0,
        completedHabits: Int = 0,
        completedTasks: Int = 0
    ) = PetReactionSnapshot(
        totalXP = xp,
        level = level,
        streak = streak,
        completedHabits = completedHabits,
        completedTasks = completedTasks
    )
}
