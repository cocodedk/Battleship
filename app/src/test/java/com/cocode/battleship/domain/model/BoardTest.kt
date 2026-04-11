package com.cocode.battleship.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BoardTest {

    @Test
    fun validPlacementOnEmptyBoard() {
        val board = Board()
        val ship = Ship(ShipType.DESTROYER, startRow = 0, startCol = 0, isHorizontal = true)
        assertTrue(board.isValidPlacement(ship))
    }

    @Test
    fun invalidPlacementOutOfBounds() {
        val board = Board()
        // DESTROYER size 2, horizontal at col 9: positions (9,9) and (9,10) — col 10 is out of bounds
        val ship = Ship(ShipType.DESTROYER, startRow = 9, startCol = 9, isHorizontal = true)
        assertFalse(board.isValidPlacement(ship))
    }

    @Test
    fun invalidPlacementOverlap() {
        val ship1 = Ship(ShipType.DESTROYER, startRow = 0, startCol = 0, isHorizontal = true)
        val board = Board().placeShip(ship1)
        // Second ship overlaps at (0,0)
        val ship2 = Ship(ShipType.CRUISER, startRow = 0, startCol = 0, isHorizontal = false)
        assertFalse(board.isValidPlacement(ship2))
    }

    @Test
    fun receiveAttackReturnsHitWhenShipPresent() {
        val ship = Ship(ShipType.DESTROYER, startRow = 3, startCol = 3, isHorizontal = true)
        val board = Board().placeShip(ship).receiveAttack(3, 3)
        assertEquals(CellState.HIT, board.getCellState(3, 3))
    }

    @Test
    fun receiveAttackReturnsMissWhenNoShip() {
        val ship = Ship(ShipType.DESTROYER, startRow = 3, startCol = 3, isHorizontal = true)
        val board = Board().placeShip(ship).receiveAttack(5, 5)
        assertEquals(CellState.MISS, board.getCellState(5, 5))
    }

    @Test
    fun allShipsSunkReturnsTrueWhenAllShipsSunk() {
        val ship = Ship(ShipType.DESTROYER, startRow = 0, startCol = 0, isHorizontal = true)
        // DESTROYER has size 2, occupies (0,0) and (0,1)
        val board = Board()
            .placeShip(ship)
            .receiveAttack(0, 0)
            .receiveAttack(0, 1)
        assertTrue(board.allShipsSunk())
    }

    @Test
    fun hasBeenAttackedReflectsAttackHistory() {
        val board = Board().receiveAttack(4, 7)
        assertTrue(board.hasBeenAttacked(4, 7))
        assertFalse(board.hasBeenAttacked(0, 0))
    }

    // --- receiveWeaponAttack tests ---

    @Test
    fun `receiveWeaponAttack appliesAllHitsAtOnce`() {
        // CARRIER size 5, horizontal at row 0, cols 0-4
        val ship = Ship(ShipType.CARRIER, startRow = 0, startCol = 0, isHorizontal = true)
        val board = Board().placeShip(ship)
        // Carpet-bomb-like 3x3 at (0,2): covers cols 1,2,3 on row 0 plus row above (nothing) and below
        val cells = listOf(0 to 1, 0 to 2, 0 to 3, 0 to 4, 0 to 0)  // covers all 5 ship cells
        val result = board.receiveWeaponAttack(cells)
        assertTrue(result.allShipsSunk())
    }

    @Test
    fun `receiveWeaponAttack skipsAlreadyAttackedCells`() {
        val ship = Ship(ShipType.DESTROYER, startRow = 0, startCol = 0, isHorizontal = true)
        val board = Board().placeShip(ship).receiveAttack(0, 0)
        // Weapon hits (0,0) again (should skip) and (0,1) (new hit)
        val result = board.receiveWeaponAttack(listOf(0 to 0, 0 to 1))
        // (0,0) was already there, (0,1) is new — total attacks = 2
        assertEquals(2, result.attacks.size)
        assertTrue(result.allShipsSunk())
    }

    @Test
    fun `receiveWeaponAttack emptyCellsList returnsSameBoard`() {
        val board = Board().receiveAttack(5, 5)
        val result = board.receiveWeaponAttack(emptyList())
        assertEquals(board, result)
    }

    @Test
    fun `receiveWeaponAttack allCellsAlreadyAttacked returnsSameBoard`() {
        val board = Board().receiveAttack(3, 3).receiveAttack(3, 4)
        val result = board.receiveWeaponAttack(listOf(3 to 3, 3 to 4))
        assertEquals(board, result)
    }

    @Test
    fun `receiveWeaponAttack partialHit sinksOnlyCoveredShipPortion`() {
        val ship = Ship(ShipType.DESTROYER, startRow = 2, startCol = 2, isHorizontal = true)
        // DESTROYER at (2,2) and (2,3)
        val board = Board().placeShip(ship)
        // Only hit (2,2) — ship should be HIT but not SUNK
        val result = board.receiveWeaponAttack(listOf(2 to 2))
        assertEquals(CellState.HIT, result.getCellState(2, 2))
        assertFalse(result.allShipsSunk())
    }
}
