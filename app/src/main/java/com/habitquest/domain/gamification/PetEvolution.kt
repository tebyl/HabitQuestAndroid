package com.habitquest.domain.gamification

enum class PetStage(
    val label: String,
    val minLevel: Int,
    val minXP: Int,
    val icon: String,
    val phrase: String
) {
    EGG("Egg", 1, 0, "\uD83E\uDD5A", "Tu aventura esta por comenzar"),
    BABY("Baby", 2, 300, "\uD83C\uDF31", "Cada pequeno habito la hace crecer"),
    EXPLORER("Explorer", 3, 800, "\uD83E\uDDED", "Ya explora nuevas rutinas contigo"),
    GUARDIAN("Guardian", 4, 1500, "\uD83D\uDEE1\uFE0F", "Protege tu constancia diaria"),
    LEGEND("Legend", 5, 2500, "\uD83D\uDC09", "Tu disciplina alcanzo nivel legendario")
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

    val stage = PetStage.entries.last { safeLevel >= it.minLevel || safeXP >= it.minXP }
    val nextStage = PetStage.entries.firstOrNull { it.ordinal > stage.ordinal }
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
