package com.habitquest.domain.gamification

enum class PetStage(
    val label: String,
    val minLevel: Int,
    val minXP: Int,
    val icon: String,
    val phrase: String
) {
    EGG(
        label = "Semilla",
        minLevel = 1,
        minXP = 0,
        icon = "🌱",
        phrase = "Todo comienza aquí 🌱"
    ),
    BABY(
        label = "Brote",
        minLevel = 2,
        minXP = 300,
        icon = "🌷",
        phrase = "Cada pequeño hábito la hace crecer"
    ),
    EXPLORER(
        label = "Flor",
        minLevel = 3,
        minXP = 800,
        icon = "🌸",
        phrase = "Descubre nuevas rutinas contigo"
    ),
    GUARDIAN(
        label = "Aura",
        minLevel = 4,
        minXP = 1500,
        icon = "✨",
        phrase = "Protege tu constancia diaria"
    ),
    LEGEND(
        label = "Esencia",
        minLevel = 5,
        minXP = 2500,
        icon = "👑",
        phrase = "Tu disciplina inspira calma y progreso"
    )
}

data class PetState(
    val stage: PetStage,
    val nextStage: PetStage?,
    val progressToNext: Float,
    val xpToNext: Int,
    val streakBonusActive: Boolean,
    val totalXP: Int,
    val streak: Int,
    val habitsCompleted: Int,
    val tasksCompleted: Int,
    val companionPower: Int
)

fun resolvePetState(
    totalXP: Int,
    level: Int,
    streak: Int,
    habitsCompleted: Int,
    tasksCompleted: Int
): PetState {
    val safeXP = totalXP.coerceAtLeast(0)
    val safeLevel = level.coerceAtLeast(1)
    val safeStreak = streak.coerceAtLeast(0)
    val safeHabitsCompleted = habitsCompleted.coerceAtLeast(0)
    val safeTasksCompleted = tasksCompleted.coerceAtLeast(0)

    val stage = PetStage.entries.last { 
        safeLevel >= it.minLevel || safeXP >= it.minXP 
    }
    
    val nextStage = PetStage.entries.firstOrNull { 
        it.ordinal > stage.ordinal 
    }

    val progressToNext = nextStage?.let { next ->
        val previousXP = stage.minXP
        val xpRange = (next.minXP - previousXP).coerceAtLeast(1)
        ((safeXP - previousXP).toFloat() / xpRange).coerceIn(0f, 1f)
    } ?: 1f

    return PetState(
        stage = stage,
        nextStage = nextStage,
        progressToNext = progressToNext,
        xpToNext = nextStage?.let { (it.minXP - safeXP).coerceAtLeast(0) } ?: 0,
        streakBonusActive = safeStreak >= 3,
        totalXP = safeXP,
        streak = safeStreak,
        habitsCompleted = safeHabitsCompleted,
        tasksCompleted = safeTasksCompleted,
        companionPower = safeXP + (safeStreak * 10) + (safeHabitsCompleted * 5) + (safeTasksCompleted * 3)
    )
}
