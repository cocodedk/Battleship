package com.cocode.battleship.domain.model

/**
 * Resolves the absolute grid cells a weapon targets when fired at (row, col).
 * Cells outside [0, GRID_SIZE) are discarded. Duplicate cells are deduped.
 * Does NOT filter already-attacked cells — that is Board's responsibility.
 */
fun resolveWeaponCells(
    weapon: SuperWeapon,
    row: Int,
    col: Int
): List<Pair<Int, Int>> =
    weapon.offsets
        .map { (dr, dc) -> (row + dr) to (col + dc) }
        .filter { (r, c) -> r in 0 until GRID_SIZE && c in 0 until GRID_SIZE }
        .distinct()
