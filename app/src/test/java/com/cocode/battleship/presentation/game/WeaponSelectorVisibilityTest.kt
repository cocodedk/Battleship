package com.cocode.battleship.presentation.game

import com.cocode.battleship.domain.model.SuperWeapon
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WeaponSelectorVisibilityTest {

    @Test
    fun `selector is visible when weapons are available regardless of turn`() {
        assertTrue(weaponSelectorVisible(listOf(SuperWeapon.SONAR_SWEEP)))
    }

    @Test
    fun `selector is hidden when no weapons are available`() {
        assertFalse(weaponSelectorVisible(emptyList()))
    }
}
