package com.habitquest.domain.gamification

enum class Rarity { COMMON, RARE, EPIC }

data class AchievementSnapshot(
    val id: Int,
    val title: String,
    val description: String,
    val icon: String,
    val unlocked: Boolean,
    val progress: Int,
    val target: Int,
    val rarity: Rarity
)

object AchievementPolicy {
    // Return status for all achievements for a given snapshot
    fun evaluate(snapshot: PetReactionSnapshot): List<AchievementSnapshot> {
        return listOf(
            AchievementSnapshot(
                id = 1,
                title = "Primer paso",
                description = "Completa tu primer hábito",
                icon = "🌱",
                unlocked = snapshot.completedHabits >= 1,
                progress = snapshot.completedHabits.coerceAtLeast(0),
                target = 1,
                rarity = Rarity.COMMON
            ),
            AchievementSnapshot(
                id = 2,
                title = "Ritmo de 7 días",
                description = "Mantén tu constancia una semana",
                icon = "💗",
                unlocked = snapshot.streak >= 7,
                progress = snapshot.streak.coerceAtLeast(0),
                target = 7,
                rarity = Rarity.RARE
            ),
            AchievementSnapshot(
                id = 3,
                title = "Rutina organizada",
                description = "Completa 10 tareas",
                icon = "✅",
                unlocked = snapshot.completedTasks >= 10,
                progress = snapshot.completedTasks.coerceAtLeast(0),
                target = 10,
                rarity = Rarity.COMMON
            ),
            AchievementSnapshot(
                id = 4,
                title = "Nueva versión",
                description = "Alcanza una nueva etapa",
                icon = "👑",
                unlocked = snapshot.level >= 5,
                progress = snapshot.level.coerceAtLeast(0),
                target = 5,
                rarity = Rarity.RARE
            ),
            AchievementSnapshot(
                id = 5,
                title = "Energía acumulada",
                description = "Suma 1000 puntos de progreso",
                icon = "✨",
                unlocked = snapshot.totalXP >= 1000,
                progress = snapshot.totalXP.coerceAtLeast(0),
                target = 1000,
                rarity = Rarity.EPIC
            )
        )
    }

    // Return IDs unlocked in curr but not in prev
    fun newlyUnlocked(prev: PetReactionSnapshot, curr: PetReactionSnapshot): List<AchievementSnapshot> {
        val before = evaluate(prev).associateBy { it.id }
        return evaluate(curr).filter { currAch ->
            val was = before[currAch.id]
            !(was?.unlocked ?: false) && currAch.unlocked
        }
    }
}
