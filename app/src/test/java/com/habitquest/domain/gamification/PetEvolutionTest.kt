package com.habitquest.domain.gamification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PetEvolutionTest {
    @Test
    fun resolvePetState_usesLevelAndXpThresholds() {
        assertEquals(PetStage.EGG, pet(totalXP = 0, level = 1).stage)
        assertEquals(PetStage.BABY, pet(totalXP = 300, level = 1).stage)
        assertEquals(PetStage.BABY, pet(totalXP = 0, level = 2).stage)
        assertEquals(PetStage.EXPLORER, pet(totalXP = 800, level = 1).stage)
        assertEquals(PetStage.EXPLORER, pet(totalXP = 0, level = 3).stage)
        assertEquals(PetStage.GUARDIAN, pet(totalXP = 1500, level = 1).stage)
        assertEquals(PetStage.GUARDIAN, pet(totalXP = 0, level = 4).stage)
        assertEquals(PetStage.LEGEND, pet(totalXP = 2500, level = 1).stage)
        assertEquals(PetStage.LEGEND, pet(totalXP = 0, level = 5).stage)
    }

    @Test
    fun resolvePetState_calculatesNextProgressAndXp() {
        val state = pet(totalXP = 450, level = 2)

        assertEquals(PetStage.BABY, state.stage)
        assertEquals(PetStage.EXPLORER, state.nextStage)
        assertEquals(350, state.xpToNext)
        assertEquals(0.3f, state.progressToNext, 0.0001f)
    }

    @Test
    fun resolvePetState_marksStreakBonusAtThreeDays() {
        assertFalse(pet(streak = 2).streakBonusActive)
        assertTrue(pet(streak = 3).streakBonusActive)
    }

    @Test
    fun resolvePetState_clampsNegativeInputs() {
        val state = pet(
            totalXP = -10,
            level = -1,
            streak = -3,
            habitsCompleted = -5,
            tasksCompleted = -8
        )

        assertEquals(PetStage.EGG, state.stage)
        assertEquals(0, state.streak)
        assertEquals(0, state.habitsCompleted)
        assertEquals(0, state.tasksCompleted)
    }

    private fun pet(
        totalXP: Int = 0,
        level: Int = 1,
        streak: Int = 0,
        habitsCompleted: Int = 0,
        tasksCompleted: Int = 0
    ) = resolvePetState(
        totalXP = totalXP,
        level = level,
        streak = streak,
        habitsCompleted = habitsCompleted,
        tasksCompleted = tasksCompleted
    )
}
