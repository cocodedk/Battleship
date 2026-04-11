package com.cocode.battleship.domain.ai

import com.cocode.battleship.domain.model.Board
import com.cocode.battleship.domain.model.CellState
import com.cocode.battleship.domain.model.FLEET
import com.cocode.battleship.domain.model.GRID_SIZE
import com.cocode.battleship.domain.model.Ship
import kotlin.random.Random

object BattleshipAI {

    fun chooseAttack(playerBoard: Board): Pair<Int, Int> {
        // Collect all HIT (but not sunk) cells
        val hitCells = playerBoard.attacks.filter { (r, c) ->
            playerBoard.getCellState(r, c) == CellState.HIT
        }

        if (hitCells.isNotEmpty()) {
            // Target mode: try to find line continuations first
            val lineCandidates = findLineCandidates(hitCells, playerBoard)
            if (lineCandidates.isNotEmpty()) {
                return lineCandidates.random()
            }

            // Fall back to any adjacent unattacked cell
            val adjacentCandidates = hitCells
                .flatMap { (r, c) -> getAdjacentCells(r, c) }
                .distinct()
                .filter { (r, c) -> !playerBoard.hasBeenAttacked(r, c) }

            if (adjacentCandidates.isNotEmpty()) {
                return adjacentCandidates.random()
            }
        }

        // Hunt mode: checkerboard parity (row + col) % 2 == 0 first
        val allUnattacked = (0 until GRID_SIZE).flatMap { r ->
            (0 until GRID_SIZE).map { c -> Pair(r, c) }
        }.filter { (r, c) -> !playerBoard.hasBeenAttacked(r, c) }

        val parityFirst = allUnattacked.filter { (r, c) -> (r + c) % 2 == 0 }
        return if (parityFirst.isNotEmpty()) {
            parityFirst.random()
        } else {
            require(allUnattacked.isNotEmpty()) { "No unattacked cells remain" }
            allUnattacked.random()
        }
    }

    private fun findLineCandidates(
        hitCells: List<Pair<Int, Int>>,
        board: Board
    ): List<Pair<Int, Int>> {
        val candidates = mutableListOf<Pair<Int, Int>>()

        // Check for horizontal alignment: two or more hits in the same row
        val rowGroups = hitCells.groupBy { it.first }
        for ((row, cells) in rowGroups) {
            if (cells.size >= 2) {
                val cols = cells.map { it.second }.sorted()
                val minCol = cols.first()
                val maxCol = cols.last()
                // Extend left
                val leftCell = Pair(row, minCol - 1)
                if (minCol - 1 >= 0 && !board.hasBeenAttacked(leftCell.first, leftCell.second)) {
                    candidates.add(leftCell)
                }
                // Extend right
                val rightCell = Pair(row, maxCol + 1)
                if (maxCol + 1 < GRID_SIZE && !board.hasBeenAttacked(rightCell.first, rightCell.second)) {
                    candidates.add(rightCell)
                }
            }
        }

        // Check for vertical alignment: two or more hits in the same column
        val colGroups = hitCells.groupBy { it.second }
        for ((col, cells) in colGroups) {
            if (cells.size >= 2) {
                val rows = cells.map { it.first }.sorted()
                val minRow = rows.first()
                val maxRow = rows.last()
                // Extend up
                val upCell = Pair(minRow - 1, col)
                if (minRow - 1 >= 0 && !board.hasBeenAttacked(upCell.first, upCell.second)) {
                    candidates.add(upCell)
                }
                // Extend down
                val downCell = Pair(maxRow + 1, col)
                if (maxRow + 1 < GRID_SIZE && !board.hasBeenAttacked(downCell.first, downCell.second)) {
                    candidates.add(downCell)
                }
            }
        }

        return candidates.distinct()
    }

    private fun getAdjacentCells(row: Int, col: Int): List<Pair<Int, Int>> {
        return listOf(
            Pair(row - 1, col),
            Pair(row + 1, col),
            Pair(row, col - 1),
            Pair(row, col + 1)
        ).filter { (r, c) -> r in 0 until GRID_SIZE && c in 0 until GRID_SIZE }
    }

    fun placeShipsRandomly(): Board {
        var board = Board()
        for (shipType in FLEET) {
            var placed = false
            while (!placed) {
                val row = Random.nextInt(GRID_SIZE)
                val col = Random.nextInt(GRID_SIZE)
                val isHorizontal = Random.nextBoolean()
                val ship = Ship(
                    type = shipType,
                    startRow = row,
                    startCol = col,
                    isHorizontal = isHorizontal
                )
                if (board.isValidPlacement(ship)) {
                    board = board.placeShip(ship)
                    placed = true
                }
            }
        }
        return board
    }
}
