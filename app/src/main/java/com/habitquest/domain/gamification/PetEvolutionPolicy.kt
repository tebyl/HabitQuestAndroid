package com.habitquest.domain.gamification

enum class PetEmotion {
    IDLE,
    HAPPY,
    PROUD,
    TIRED
}

data class PetStageThreshold(
    val stage: PetStage,
    val minXP: Int,
    val minLevel: Int,
    val minStreak: Int,
    val minCompletedHabits: Int,
    val minCompletedTasks: Int
)

data class PetStageProgress(
    val currentStage: PetStage,
    val nextStage: PetStage?,
    val progress: Float,
    val xpToNext: Int
)

object PetEvolutionPolicy {
    val thresholds = listOf(
        PetStageThreshold(PetStage.EGG, minXP = 0, minLevel = 1, minStreak = 0, minCompletedHabits = 0, minCompletedTasks = 0),
        PetStageThreshold(PetStage.BABY, minXP = 300, minLevel = 2, minStreak = 1, minCompletedHabits = 3, minCompletedTasks = 2),
        PetStageThreshold(PetStage.EXPLORER, minXP = 800, minLevel = 3, minStreak = 3, minCompletedHabits = 10, minCompletedTasks = 6),
        PetStageThreshold(PetStage.GUARDIAN, minXP = 1_500, minLevel = 4, minStreak = 7, minCompletedHabits = 24, minCompletedTasks = 15),
        PetStageThreshold(PetStage.LEGEND, minXP = 2_500, minLevel = 5, minStreak = 14, minCompletedHabits = 50, minCompletedTasks = 30)
    )

    fun getStage(
        xp: Int,
        level: Int,
        streak: Int,
        completedHabits: Int,
        completedTasks: Int
    ): PetStage {
        val safe = sanitize(xp, level, streak, completedHabits, completedTasks)
        return thresholds.last { threshold ->
            safe.xp >= threshold.minXP ||
                safe.level >= threshold.minLevel ||
                safe.streak >= threshold.minStreak && safe.completedHabits >= threshold.minCompletedHabits ||
                safe.completedTasks >= threshold.minCompletedTasks && safe.completedHabits >= threshold.minCompletedHabits / 2
        }.stage
    }

    fun getNextStageProgress(
        xp: Int,
        level: Int,
        streak: Int,
        completedHabits: Int,
        completedTasks: Int
    ): PetStageProgress {
        val safe = sanitize(xp, level, streak, completedHabits, completedTasks)
        val stage = getStage(safe.xp, safe.level, safe.streak, safe.completedHabits, safe.completedTasks)
        val nextStage = PetStage.entries.firstOrNull { it.ordinal > stage.ordinal }
        val progress = nextStage?.let { next ->
            val currentThreshold = thresholdFor(stage)
            val nextThreshold = thresholdFor(next)
            val xpProgress = ratio(safe.xp - currentThreshold.minXP, nextThreshold.minXP - currentThreshold.minXP)
            val habitProgress = ratio(safe.completedHabits - currentThreshold.minCompletedHabits, nextThreshold.minCompletedHabits - currentThreshold.minCompletedHabits)
            val taskProgress = ratio(safe.completedTasks - currentThreshold.minCompletedTasks, nextThreshold.minCompletedTasks - currentThreshold.minCompletedTasks)
            val streakProgress = ratio(safe.streak - currentThreshold.minStreak, nextThreshold.minStreak - currentThreshold.minStreak)
            maxOf(xpProgress, habitProgress, taskProgress, streakProgress).coerceIn(0f, 1f)
        } ?: 1f

        return PetStageProgress(
            currentStage = stage,
            nextStage = nextStage,
            progress = progress,
            xpToNext = nextStage?.let { (thresholdFor(it).minXP - safe.xp).coerceAtLeast(0) } ?: 0
        )
    }

    fun getNextStageRequirementText(
        xp: Int,
        level: Int,
        streak: Int,
        completedHabits: Int,
        completedTasks: Int
    ): String {
        val progress = getNextStageProgress(xp, level, streak, completedHabits, completedTasks)
        return progress.nextStage?.let {
            if (progress.xpToNext > 0) {
                "Te faltan ${progress.xpToNext} XP para evolucionar"
            } else {
                "Sigue completando hábitos y tareas para evolucionar"
            }
        } ?: "Etapa máxima alcanzada"
    }

    fun getEmotion(
        stage: PetStage,
        level: Int,
        streak: Int,
        completedHabits: Int,
        completedTasks: Int,
        progressToNext: Float
    ): PetEmotion {
        val safeLevel = level.coerceAtLeast(1)
        val safeStreak = streak.coerceAtLeast(0)
        val safeCompleted = completedHabits.coerceAtLeast(0) + completedTasks.coerceAtLeast(0)
        return when {
            stage.ordinal >= PetStage.GUARDIAN.ordinal || safeLevel >= 4 || progressToNext <= 0.15f && stage != PetStage.EGG -> PetEmotion.PROUD
            safeStreak >= 3 || completedTasks >= 5 -> PetEmotion.HAPPY
            safeStreak == 0 && safeCompleted < 3 -> PetEmotion.TIRED
            else -> PetEmotion.IDLE
        }
    }

    fun getStageMessage(stage: PetStage, emotion: PetEmotion): String = when (emotion) {
        PetEmotion.TIRED -> "Hoy necesita un pequeño impulso."
        PetEmotion.HAPPY -> "Tu constancia le está dando energía."
        PetEmotion.PROUD -> "Está brillando por tu progreso."
        PetEmotion.IDLE -> when (stage) {
            PetStage.EGG -> "Tu compañero está despertando."
            PetStage.BABY -> "Está aprendiendo contigo."
            PetStage.EXPLORER -> "Ya explora nuevas rutinas."
            PetStage.GUARDIAN -> "Protege tu constancia diaria."
            PetStage.LEGEND -> "Tu disciplina ya es legendaria."
        }
    }

    private fun thresholdFor(stage: PetStage): PetStageThreshold =
        thresholds.first { it.stage == stage }

    private fun ratio(value: Int, range: Int): Float =
        if (range <= 0) 1f else value.toFloat() / range.toFloat()

    private fun sanitize(
        xp: Int,
        level: Int,
        streak: Int,
        completedHabits: Int,
        completedTasks: Int
    ) = SafePetInputs(
        xp = xp.coerceAtLeast(0),
        level = level.coerceAtLeast(1),
        streak = streak.coerceAtLeast(0),
        completedHabits = completedHabits.coerceAtLeast(0),
        completedTasks = completedTasks.coerceAtLeast(0)
    )

    private data class SafePetInputs(
        val xp: Int,
        val level: Int,
        val streak: Int,
        val completedHabits: Int,
        val completedTasks: Int
    )
}
