package com.habitquest.domain.gamification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PetEvolutionPolicyTest {

    @Test
    fun getStage_usesXpThresholds() {
        assertEquals(PetStage.EGG, stage(xp = 0))
        assertEquals(PetStage.BABY, stage(xp = 300))
        assertEquals(PetStage.EXPLORER, stage(xp = 800))
        assertEquals(PetStage.GUARDIAN, stage(xp = 1_500))
        assertEquals(PetStage.LEGEND, stage(xp = 2_500))
    }

    @Test
    fun getStage_usesExistingActivityCombination() {
        val stage = stage(
            xp = 200,
            level = 1,
            streak = 7,
            completedHabits = 24,
            completedTasks = 15
        )

        assertEquals(PetStage.GUARDIAN, stage)
    }

    @Test
    fun getNextStageProgress_usesBestAvailableProgressSignal() {
        val progress = PetEvolutionPolicy.getNextStageProgress(
            xp = 450,
            level = 2,
            streak = 1,
            completedHabits = 3,
            completedTasks = 2
        )

        assertEquals(PetStage.BABY, progress.currentStage)
        assertEquals(PetStage.EXPLORER, progress.nextStage)
        assertEquals(350, progress.xpToNext)
        assertEquals(0.3f, progress.progress, 0.0001f)
    }

    @Test
    fun getNextStageProgress_maxStage_isComplete() {
        val progress = PetEvolutionPolicy.getNextStageProgress(
            xp = 2_500,
            level = 5,
            streak = 14,
            completedHabits = 50,
            completedTasks = 30
        )

        assertEquals(PetStage.LEGEND, progress.currentStage)
        assertEquals(null, progress.nextStage)
        assertEquals(1f, progress.progress, 0.0001f)
        assertEquals(0, progress.xpToNext)
        assertEquals(
            "Etapa máxima alcanzada",
            PetEvolutionPolicy.getNextStageRequirementText(2_500, 5, 14, 50, 30)
        )
    }

    @Test
    fun getStage_zeroAndNegativeData_staysAtEgg() {
        val progress = PetEvolutionPolicy.getNextStageProgress(
            xp = -10,
            level = -1,
            streak = -3,
            completedHabits = -4,
            completedTasks = -5
        )

        assertEquals(PetStage.EGG, progress.currentStage)
        assertTrue(progress.progress >= 0f)
    }

    @Test
    fun getEmotion_derivesStateWithoutPersistence() {
        assertEquals(
            PetEmotion.TIRED,
            PetEvolutionPolicy.getEmotion(PetStage.EGG, level = 1, streak = 0, completedHabits = 0, completedTasks = 0, progressToNext = 0f)
        )
        assertEquals(
            PetEmotion.HAPPY,
            PetEvolutionPolicy.getEmotion(PetStage.BABY, level = 2, streak = 3, completedHabits = 4, completedTasks = 2, progressToNext = 0.4f)
        )
        assertEquals(
            PetEmotion.PROUD,
            PetEvolutionPolicy.getEmotion(PetStage.GUARDIAN, level = 4, streak = 1, completedHabits = 24, completedTasks = 15, progressToNext = 0.5f)
        )
    }

    private fun stage(
        xp: Int,
        level: Int = 1,
        streak: Int = 0,
        completedHabits: Int = 0,
        completedTasks: Int = 0
    ) = PetEvolutionPolicy.getStage(
        xp = xp,
        level = level,
        streak = streak,
        completedHabits = completedHabits,
        completedTasks = completedTasks
    )
}
