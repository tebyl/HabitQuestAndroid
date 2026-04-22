package com.habitquest.domain.model

import androidx.compose.ui.graphics.Color

data class Level(
    val level: Int,
    val name: String,
    val xpRequired: Int,
    val color: Color
)

object Levels {
    val all = listOf(
        Level(1, "Novice",    0,    Color(0xFF6B7280)),
        Level(2, "Aprendiz",  200,  Color(0xFF10B981)),
        Level(3, "Guerrero",  500,  Color(0xFF3B82F6)),
        Level(4, "Héroe",     1000, Color(0xFF8B5CF6)),
        Level(5, "Leyenda",   2000, Color(0xFFF59E0B)),
    )

    fun getCurrentLevel(xp: Int): Level {
        var current = all.first()
        for (level in all) {
            if (xp >= level.xpRequired) current = level
        }
        return current
    }

    fun getXPToNext(xp: Int): Int {
        val current = getCurrentLevel(xp)
        val next = all.find { it.level == current.level + 1 } ?: return 9999
        return next.xpRequired - current.xpRequired
    }

    fun getXPInCurrentLevel(xp: Int): Int {
        val current = getCurrentLevel(xp)
        return xp - current.xpRequired
    }
}
