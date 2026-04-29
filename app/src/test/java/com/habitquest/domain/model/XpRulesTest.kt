package com.habitquest.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class XpRulesTest {
    @Test
    fun applyDelta_neverReturnsNegativeXp() {
        assertEquals(0, XpRules.applyDelta(currentXP = 10, delta = -40))
        assertEquals(0, XpRules.applyDelta(currentXP = 0, delta = -1))
        assertEquals(35, XpRules.applyDelta(currentXP = 10, delta = 25))
    }
}
