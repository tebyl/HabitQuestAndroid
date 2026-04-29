package com.habitquest.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class LevelsTest {
    @Test
    fun currentLevel_matchesXpThresholds() {
        assertEquals(1, Levels.getCurrentLevel(0).level)
        assertEquals(1, Levels.getCurrentLevel(199).level)
        assertEquals(2, Levels.getCurrentLevel(200).level)
        assertEquals(3, Levels.getCurrentLevel(500).level)
        assertEquals(4, Levels.getCurrentLevel(1000).level)
        assertEquals(5, Levels.getCurrentLevel(2000).level)
    }
}
