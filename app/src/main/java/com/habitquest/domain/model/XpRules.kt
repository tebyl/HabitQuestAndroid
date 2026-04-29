package com.habitquest.domain.model

object XpRules {
    fun applyDelta(currentXP: Int, delta: Int): Int =
        (currentXP + delta).coerceAtLeast(0)
}
