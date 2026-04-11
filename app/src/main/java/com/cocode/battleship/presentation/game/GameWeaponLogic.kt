package com.cocode.battleship.presentation.game

import com.cocode.battleship.domain.model.Board
import com.cocode.battleship.domain.model.CellState
import com.cocode.battleship.domain.model.ShipType
import com.cocode.battleship.domain.model.SuperWeapon

/**
 * Builds the player-facing status message after an attack.
 * For weapon fire, describes what the weapon hit/sunk.
 * For single-cell fire, uses the standard hit/miss/sunk message.
 */
fun buildPlayerHitMessage(
    weapon: SuperWeapon?,
    primaryCellState: CellState,
    newlySunkTypes: Set<ShipType>,
    board: Board
): String {
    if (weapon != null) {
        return if (newlySunkTypes.isNotEmpty()) {
            "${weapon.displayName} sunk: ${newlySunkTypes.joinToString { it.displayName }}!"
        } else {
            "${weapon.displayName} fired!"
        }
    }
    return when (primaryCellState) {
        CellState.HIT -> "Hit! Keep going!"
        CellState.SUNK -> "You sunk a ${
            board.ships.find { it.isSunk && newlySunkTypes.contains(it.type) }
                ?.type?.displayName ?: "ship"
        }!"
        else -> "Miss."
    }
}

/**
 * Updates TrackerState for a weapon fire covering multiple cells.
 * Each fired cell counts as one shot. newlySunkTypes is attributed to
 * the last cell in the list (the "primary" target for tracking purposes).
 */
fun updateTrackersForFire(
    current: TrackerState,
    board: Board,
    firedCells: List<Pair<Int, Int>>,
    newlySunkTypes: Set<ShipType>
): TrackerState {
    var t = current
    firedCells.forEachIndexed { idx, (r, c) ->
        val cs = board.getCellState(r, c)
        val sunkTypeForThis = if (idx == firedCells.lastIndex && newlySunkTypes.isNotEmpty())
            newlySunkTypes.first() else null
        t = updateTrackers(t, cs, sunkTypeForThis)
    }
    return t
}
