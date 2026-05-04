package com.habitquest.domain.gamification

import org.junit.Assert.*
import org.junit.Test

class AchievementPolicyTest {

    @Test
    fun `user without progress has no unlocked achievements`() {
        val snapshot = PetReactionSnapshot(totalXP = 0, level = 1, streak = 0, completedHabits = 0, completedTasks = 0)
        val all = AchievementPolicy.evaluate(snapshot)
        assertTrue(all.none { it.unlocked })
    }

    @Test
    fun `first habit completed unlocks primer paso`() {
        val prev = PetReactionSnapshot(0,1,0,0,0)
        val curr = PetReactionSnapshot(0,1,0,1,0)
        val newly = AchievementPolicy.newlyUnlocked(prev, curr)
        assertTrue(newly.any { it.id == 1 })
    }

    @Test
    fun `seven day streak unlocks ritmo de 7 dias`() {
        val prev = PetReactionSnapshot(0,1,6,0,0)
        val curr = PetReactionSnapshot(0,1,7,0,0)
        val newly = AchievementPolicy.newlyUnlocked(prev, curr)
        assertTrue(newly.any { it.id == 2 })
    }

    @Test
    fun `ten tasks unlock rutina organizada`() {
        val prev = PetReactionSnapshot(0,1,0,0,9)
        val curr = PetReactionSnapshot(0,1,0,0,10)
        val newly = AchievementPolicy.newlyUnlocked(prev, curr)
        assertTrue(newly.any { it.id == 3 })
    }

    @Test
    fun `level five unlocks nueva version`() {
        val prev = PetReactionSnapshot(0,4,0,0,0)
        val curr = PetReactionSnapshot(0,5,0,0,0)
        val newly = AchievementPolicy.newlyUnlocked(prev, curr)
        assertTrue(newly.any { it.id == 4 })
    }

    @Test
    fun `one thousand xp unlocks energia acumulada`() {
        val prev = PetReactionSnapshot(999,1,0,0,0)
        val curr = PetReactionSnapshot(1000,1,0,0,0)
        val newly = AchievementPolicy.newlyUnlocked(prev, curr)
        assertTrue(newly.any { it.id == 5 })
    }
}
