package com.cocode.battleship.domain.model

const val GRID_SIZE = 10

data class Board(
    val ships: List<Ship> = emptyList(),
    val attacks: Set<Pair<Int, Int>> = emptySet()
) {
    fun isValidPlacement(ship: Ship): Boolean {
        if (ship.positions.any { (r, c) -> r < 0 || r >= GRID_SIZE || c < 0 || c >= GRID_SIZE }) return false
        val occupied = ships.flatMap { it.positions }.toSet()
        return ship.positions.none { it in occupied }
    }

    fun placeShip(ship: Ship): Board = copy(ships = ships + ship)

    fun receiveAttack(row: Int, col: Int): Board {
        val pos = Pair(row, col)
        if (pos in attacks) return this
        val updatedShips = ships.map { it.receiveHit(row, col) }
        return copy(ships = updatedShips, attacks = attacks + pos)
    }

    fun allShipsSunk(): Boolean = ships.isNotEmpty() && ships.all { it.isSunk }

    fun hasBeenAttacked(row: Int, col: Int): Boolean = Pair(row, col) in attacks

    fun getCellState(row: Int, col: Int): CellState {
        val pos = Pair(row, col)
        if (pos !in attacks) return CellState.EMPTY
        val ship = ships.find { pos in it.positions }
        return when {
            ship == null -> CellState.MISS
            ship.isSunk -> CellState.SUNK
            else -> CellState.HIT
        }
    }

    fun getShipAt(row: Int, col: Int): Ship? = ships.find { it.occupies(row, col) }
}
