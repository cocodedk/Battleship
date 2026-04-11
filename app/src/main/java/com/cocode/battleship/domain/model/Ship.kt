package com.cocode.battleship.domain.model

data class Ship(
    val type: ShipType,
    val startRow: Int,
    val startCol: Int,
    val isHorizontal: Boolean,
    val hitPositions: Set<Pair<Int, Int>> = emptySet()
) {
    val positions: List<Pair<Int, Int>> = buildList {
        repeat(type.size) { i ->
            add(if (isHorizontal) Pair(startRow, startCol + i) else Pair(startRow + i, startCol))
        }
    }

    val isSunk: Boolean get() = positions.all { it in hitPositions }

    fun receiveHit(row: Int, col: Int): Ship {
        val pos = Pair(row, col)
        return if (pos in positions) copy(hitPositions = hitPositions + pos) else this
    }

    fun occupies(row: Int, col: Int): Boolean = Pair(row, col) in positions
}
