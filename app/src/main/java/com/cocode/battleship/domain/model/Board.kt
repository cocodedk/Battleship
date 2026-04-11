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

    /**
     * Applies multiple attacks at once (super weapon fire).
     * Cells already in [attacks] are skipped silently.
     * Callers are responsible for ensuring cells are in-bounds.
     */
    fun receiveWeaponAttack(cells: List<Pair<Int, Int>>): Board {
        val newCells = cells.filter { it !in attacks }
        if (newCells.isEmpty()) return this
        val updatedShips = ships.map { ship ->
            newCells.fold(ship) { acc, (r, c) -> acc.receiveHit(r, c) }
        }
        return copy(ships = updatedShips, attacks = attacks + newCells.toSet())
    }

    fun getCellStates(cells: List<Pair<Int, Int>>): List<CellState> =
        cells.map { (r, c) -> getCellState(r, c) }

    fun getShipAt(row: Int, col: Int): Ship? = ships.find { it.occupies(row, col) }
}
