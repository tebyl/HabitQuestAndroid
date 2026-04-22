package com.habitquest.domain.model

data class Achievement(
    val id: Int,
    val name: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean
)

object Achievements {
    fun evaluate(habits: List<Habit>): List<Achievement> {
        val maxStreak = habits.maxOfOrNull { it.streakCount } ?: 0
        val totalDaysAll = habits.sumOf { it.totalDays }
        val activeHabits = habits.count { it.streakCount > 0 }

        return listOf(
            Achievement(
                id = 1,
                name = "Primera Racha",
                description = "5 días seguidos",
                icon = "🔥",
                isUnlocked = maxStreak >= 5
            ),
            Achievement(
                id = 2,
                name = "Semana Épica",
                description = "7 días en un hábito",
                icon = "⚡",
                isUnlocked = habits.any { it.streakCount >= 7 }
            ),
            Achievement(
                id = 3,
                name = "Maestro",
                description = "30 días totales",
                icon = "🏆",
                isUnlocked = totalDaysAll >= 30
            ),
            Achievement(
                id = 4,
                name = "Constante",
                description = "3 hábitos activos",
                icon = "💎",
                isUnlocked = activeHabits >= 3
            ),
        )
    }
}
