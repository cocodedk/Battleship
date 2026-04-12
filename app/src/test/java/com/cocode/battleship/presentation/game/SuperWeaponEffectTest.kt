package com.cocode.battleship.presentation.game

import com.cocode.battleship.domain.model.SuperWeapon
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SuperWeaponEffectTest {
    @Test
    fun `effectCellAt returns null for cells outside weapon footprint`() {
        val effect = SuperWeaponEffect(
            triggerId = 7,
            weapon = SuperWeapon.CARPET_BOMB,
            targetRow = 5,
            targetCol = 5,
            cells = setOf(5 to 5, 5 to 6)
        )

        assertNull(effect.effectCellAt(0, 0))
    }

    @Test
    fun `sonar sweep effect delays scale with horizontal distance`() {
        val near = weaponEffectDelay(SuperWeapon.SONAR_SWEEP, 4, 4, 4, 5)
        val far = weaponEffectDelay(SuperWeapon.SONAR_SWEEP, 4, 4, 4, 6)

        assertEquals(60, near)
        assertEquals(120, far)
    }

    @Test
    fun `precision strike center triggers before diagonal cells`() {
        val center = weaponEffectDelay(SuperWeapon.PRECISION_STRIKE, 4, 4, 4, 4)
        val diagonal = weaponEffectDelay(SuperWeapon.PRECISION_STRIKE, 4, 4, 3, 3)

        assertEquals(25, center)
        assertEquals(90, diagonal)
    }
}
