package com.habitquest

import com.habitquest.domain.gamification.PetStage
import com.habitquest.ui.component.petImageResFor
import org.junit.Assert.assertEquals
import org.junit.Test

class PetImageResTest {

    @Test
    fun petImageResForReturnsExpectedDrawableForEachStage() {
        assertEquals(R.drawable.pet_seed, petImageResFor(PetStage.EGG))
        assertEquals(R.drawable.pet_baby, petImageResFor(PetStage.BABY))
        assertEquals(R.drawable.pet_explorer, petImageResFor(PetStage.EXPLORER))
        assertEquals(R.drawable.pet_guardian, petImageResFor(PetStage.GUARDIAN))
        assertEquals(R.drawable.pet_essence, petImageResFor(PetStage.LEGEND))
    }
}
