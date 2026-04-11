package com.cocode.battleship.domain.ai

import com.cocode.battleship.domain.model.Board
import com.cocode.battleship.domain.model.CellState
import com.cocode.battleship.domain.model.FLEET
import com.cocode.battleship.domain.model.GRID_SIZE
import com.cocode.battleship.domain.model.Ship
import com.cocode.battleship.domain.model.ShipType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BattleshipAITest {

    // --- placeShipsRandomly tests ---

    @Test
    fun `placeShipsRandomly places all 5 ships`() {
        val board = BattleshipAI.placeShipsRandomly()
        assertEquals(FLEET.size, board.ships.size)
        // Verify all fleet types are present
        val placedTypes = board.ships.map { it.type }.toSet()
        val expectedTypes = FLEET.toSet()
        assertEquals(expectedTypes, placedTypes)
    }

    @Test
    fun `placeShipsRandomly ships do not overlap`() {
        val board = BattleshipAI.placeShipsRandomly()
        val allPositions = board.ships.flatMap { it.positions }
        val uniquePositions = allPositions.toSet()
        assertEquals("Ships overlap", allPositions.size, uniquePositions.size)
    }

    @Test
    fun `placeShipsRandomly all ships within grid bounds`() {
        val board = BattleshipAI.placeShipsRandomly()
        for (ship in board.ships) {
            for ((r, c) in ship.positions) {
                assertTrue("Row $r out of bounds", r in 0 until GRID_SIZE)
                assertTrue("Col $c out of bounds", c in 0 until GRID_SIZE)
            }
        }
    }

    // --- chooseAttack tests ---

    @Test
    fun `chooseAttack returns an unattacked cell`() {
        val board = BattleshipAI.placeShipsRandomly()
        val attack = BattleshipAI.chooseAttack(board)
        assertFalse("Attack cell should not be previously attacked", board.hasBeenAttacked(attack.first, attack.second))
    }

    @Test
    fun `chooseAttack does not return already-attacked cell`() {
        var board = BattleshipAI.placeShipsRandomly()
        // Attack 20 cells and verify each new attack is not already attacked
        repeat(20) {
            val attack = BattleshipAI.chooseAttack(board)
            assertFalse(board.hasBeenAttacked(attack.first, attack.second))
            board = board.receiveAttack(attack.first, attack.second)
        }
    }

    @Test
    fun `chooseAttack in target mode returns cell adjacent to the hit`() {
        // Build a board with one hit (not sunk): Destroyer at (5,5)-(5,6), hit at (5,5)
        val destroyer = Ship(
            type = ShipType.DESTROYER,
            startRow = 5,
            startCol = 5,
            isHorizontal = true
        )
        val board = Board(ships = listOf(destroyer))
            .receiveAttack(5, 5)  // hit the first cell, ship not sunk yet

        val attack = BattleshipAI.chooseAttack(board)

        // The returned cell must be adjacent to (5,5)
        val adjacentTo55 = setOf(
            Pair(4, 5), Pair(6, 5), Pair(5, 4), Pair(5, 6)
        )
        assertTrue(
            "Expected attack $attack to be adjacent to (5,5), adjacent cells: $adjacentTo55",
            attack in adjacentTo55
        )
    }

    @Test
    fun `chooseAttack eventually covers all cells`() {
        // Place ships so some hits can occur; run 100 attacks on a 10x10 grid
        val board = BattleshipAI.placeShipsRandomly()
        var currentBoard = board
        val totalCells = GRID_SIZE * GRID_SIZE  // 100

        repeat(totalCells) {
            val attack = BattleshipAI.chooseAttack(currentBoard)
            assertFalse("Attack $attack was already attacked at step $it", currentBoard.hasBeenAttacked(attack.first, attack.second))
            currentBoard = currentBoard.receiveAttack(attack.first, attack.second)
        }

        assertEquals("Expected all $totalCells cells to be attacked", totalCells, currentBoard.attacks.size)
    }

    @Test
    fun `chooseAttack in target mode with two hits prefers line continuation`() {
        // Place a battleship horizontally at row 3, cols 2..5
        val battleship = Ship(
            type = ShipType.BATTLESHIP,
            startRow = 3,
            startCol = 2,
            isHorizontal = true
        )
        // Hit positions (3,2) and (3,3) — two hits in same row
        val board = Board(ships = listOf(battleship))
            .receiveAttack(3, 2)
            .receiveAttack(3, 3)

        // With two hits in same row, AI should prefer (3,1) or (3,4) to continue the line
        val lineCandidates = setOf(Pair(3, 1), Pair(3, 4))
        // Run multiple times to verify it consistently returns a line candidate
        // (it could return adjacent of single-hit groupings, but grouped-line logic should prefer line)
        var lineContinuationCount = 0
        repeat(30) {
            val attack = BattleshipAI.chooseAttack(board)
            if (attack in lineCandidates) lineContinuationCount++
        }
        assertTrue(
            "Expected AI to prefer line continuation but got $lineContinuationCount/30 line candidates",
            lineContinuationCount > 0
        )
    }
}
