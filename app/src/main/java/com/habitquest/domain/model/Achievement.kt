package com.habitquest.domain.model

import com.habitquest.domain.gamification.AchievementPolicy
import com.habitquest.domain.gamification.PetReactionSnapshot

data class Achievement(
    val id: Int,
    val name: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean
)

object Achievements {
    // Compatibility wrapper: derive a minimal snapshot from habits and delegate
    fun evaluate(habits: List<Habit>): List<Achievement> {
        val maxStreak = habits.maxOfOrNull { it.streakCount } ?: 0
        val totalDaysAll = habits.sumOf { it.totalDays }
        val activeHabits = habits.count { it.streakCount > 0 }

        val snapshot = PetReactionSnapshot(
            totalXP = totalDaysAll, // best-effort mapping for legacy usage
            level = 1,
            streak = maxStreak,
            completedHabits = totalDaysAll,
            completedTasks = 0
        )

        return AchievementPolicy.evaluate(snapshot).map {
            Achievement(
                id = it.id,
                name = it.title,
                description = it.description,
                icon = it.icon,
                isUnlocked = it.unlocked
            )
        }
    }
}
