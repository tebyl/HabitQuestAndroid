package com.habitquest.domain.gamification

data class PetReactionSnapshot(
    val totalXP: Int,
    val level: Int,
    val streak: Int,
    val completedHabits: Int,
    val completedTasks: Int,
    val stage: PetStage = PetEvolutionPolicy.getStage(
        xp = totalXP,
        level = level,
        streak = streak,
        completedHabits = completedHabits,
        completedTasks = completedTasks
    )
)

object PetReactionPolicy {
    fun detect(previous: PetReactionSnapshot, current: PetReactionSnapshot): PetReactionState = when {
        current.level > previous.level -> PetReactionState.LEVEL_UP
        current.stage.ordinal > previous.stage.ordinal -> PetReactionState.PROUD
        current.streak > previous.streak -> PetReactionState.STREAK
        current.completedTasks > previous.completedTasks -> PetReactionState.HAPPY
        current.completedHabits > previous.completedHabits -> PetReactionState.HAPPY
        current.totalXP > previous.totalXP -> PetReactionState.HAPPY
        else -> PetReactionState.IDLE
    }

    fun messageFor(reaction: PetReactionState): String? = when (reaction) {
        PetReactionState.IDLE -> null
        PetReactionState.HAPPY -> "¡Buen trabajo!"
        PetReactionState.PROUD -> "¡Tu compañero evolucionó!"
        PetReactionState.STREAK -> "¡Racha mantenida!"
        PetReactionState.LEVEL_UP -> "¡Subiste de nivel!"
    }
}
