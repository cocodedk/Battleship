package com.cocode.battleship.domain.model

import org.junit.Assert.*
import org.junit.Test

class SuperWeaponTest {

    // Helper
    private fun cells(vararg pairs: Pair<Int,Int>) = setOf(*pairs)

    @Test fun `carpetBomb center returns 9 cells`() {
        val result = resolveWeaponCells(SuperWeapon.CARPET_BOMB, 5, 5).toSet()
        assertEquals(9, result.size)
        assertTrue(result.containsAll(listOf(4 to 4, 4 to 5, 4 to 6, 5 to 4, 5 to 5, 5 to 6, 6 to 4, 6 to 5, 6 to 6)))
    }

    @Test fun `carpetBomb topLeft corner clamps to 4 cells`() {
        val result = resolveWeaponCells(SuperWeapon.CARPET_BOMB, 0, 0).toSet()
        assertEquals(cells(0 to 0, 0 to 1, 1 to 0, 1 to 1), result)
    }

    @Test fun `carpetBomb bottomRight corner clamps to 4 cells`() {
        val result = resolveWeaponCells(SuperWeapon.CARPET_BOMB, 9, 9).toSet()
        assertEquals(cells(8 to 8, 8 to 9, 9 to 8, 9 to 9), result)
    }

    @Test fun `battleshipBarrage center returns 9 unique cells`() {
        val result = resolveWeaponCells(SuperWeapon.BATTLESHIP_BARRAGE, 5, 5)
        assertEquals(9, result.size)
        assertEquals(9, result.distinct().size)
        val expected = cells(3 to 5, 4 to 5, 5 to 3, 5 to 4, 5 to 5, 5 to 6, 5 to 7, 6 to 5, 7 to 5)
        assertEquals(expected, result.toSet())
    }

    @Test fun `battleshipBarrage leftEdge clamps horizontal arm`() {
        // fire at (5,0): cols -2 and -1 are out of bounds
        val result = resolveWeaponCells(SuperWeapon.BATTLESHIP_BARRAGE, 5, 0).toSet()
        assertFalse(result.contains(5 to -2))
        assertFalse(result.contains(5 to -1))
        assertTrue(result.contains(5 to 0))
        assertTrue(result.contains(5 to 1))
        assertTrue(result.contains(5 to 2))
        assertEquals(7, result.size)
    }

    @Test fun `sonarSweep center returns 5 horizontal cells`() {
        val result = resolveWeaponCells(SuperWeapon.SONAR_SWEEP, 3, 5).toSet()
        assertEquals(cells(3 to 3, 3 to 4, 3 to 5, 3 to 6, 3 to 7), result)
    }

    @Test fun `sonarSweep nearLeftEdge clamps correctly`() {
        // fire at (3,1): col -1 drops out
        val result = resolveWeaponCells(SuperWeapon.SONAR_SWEEP, 3, 1).toSet()
        assertEquals(cells(3 to 0, 3 to 1, 3 to 2, 3 to 3), result)
    }

    @Test fun `torpedoSpread center returns 5 vertical cells`() {
        val result = resolveWeaponCells(SuperWeapon.TORPEDO_SPREAD, 5, 5).toSet()
        assertEquals(cells(3 to 5, 4 to 5, 5 to 5, 6 to 5, 7 to 5), result)
    }

    @Test fun `torpedoSpread nearBottomEdge clamps correctly`() {
        // fire at (8,5): rows 10 and 11 drop out
        val result = resolveWeaponCells(SuperWeapon.TORPEDO_SPREAD, 8, 5).toSet()
        assertEquals(cells(6 to 5, 7 to 5, 8 to 5, 9 to 5), result)
    }

    @Test fun `precisionStrike center returns 5 X cells`() {
        val result = resolveWeaponCells(SuperWeapon.PRECISION_STRIKE, 5, 5).toSet()
        assertEquals(cells(4 to 4, 4 to 6, 5 to 5, 6 to 4, 6 to 6), result)
    }

    @Test fun `precisionStrike topLeftCorner clamps to 2 cells`() {
        val result = resolveWeaponCells(SuperWeapon.PRECISION_STRIKE, 0, 0).toSet()
        assertEquals(cells(0 to 0, 1 to 1), result)
    }

    @Test fun `forShipType returns correct weapon for each type`() {
        assertEquals(SuperWeapon.CARPET_BOMB,         SuperWeapon.forShipType(ShipType.CARRIER))
        assertEquals(SuperWeapon.BATTLESHIP_BARRAGE,  SuperWeapon.forShipType(ShipType.BATTLESHIP))
        assertEquals(SuperWeapon.SONAR_SWEEP,         SuperWeapon.forShipType(ShipType.CRUISER))
        assertEquals(SuperWeapon.TORPEDO_SPREAD,      SuperWeapon.forShipType(ShipType.SUBMARINE))
        assertEquals(SuperWeapon.PRECISION_STRIKE,    SuperWeapon.forShipType(ShipType.DESTROYER))
    }

    @Test fun `all offsets contain center 0 0`() {
        SuperWeapon.entries.forEach { weapon ->
            assertTrue("${weapon.name} missing (0,0)", weapon.offsets.contains(0 to 0))
        }
    }

    @Test fun `all offset lists have no duplicates`() {
        SuperWeapon.entries.forEach { weapon ->
            val offsets = weapon.offsets
            assertEquals("${weapon.name} has duplicate offsets", offsets.size, offsets.distinct().size)
        }
    }

    @Test fun `all weapons have unique unlock ship`() {
        val ships = SuperWeapon.entries.map { it.unlockShip }
        assertEquals(5, ships.distinct().size)
    }
}
