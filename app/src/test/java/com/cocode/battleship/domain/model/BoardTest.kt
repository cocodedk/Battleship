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
}
