package com.cocode.battleship.presentation.game

import com.cocode.battleship.domain.model.SuperWeapon
import kotlin.math.abs

data class SuperWeaponEffect(
    val triggerId: Int,
    val weapon: SuperWeapon,
    val targetRow: Int,
    val targetCol: Int,
    val cells: Set<Pair<Int, Int>>
) {
    fun effectCellAt(row: Int, col: Int): WeaponEffectCell? {
        if ((row to col) !in cells) return null
        return WeaponEffectCell(
            triggerId = triggerId,
            weapon = weapon,
            delayMs = weaponEffectDelay(weapon, targetRow, targetCol, row, col)
        )
    }
}

data class WeaponEffectCell(
    val triggerId: Int,
    val weapon: SuperWeapon,
    val delayMs: Int
)

internal fun weaponEffectDelay(
    weapon: SuperWeapon,
    targetRow: Int,
    targetCol: Int,
    row: Int,
    col: Int
): Int = when (weapon) {
    SuperWeapon.CARPET_BOMB -> (abs(row - targetRow) + abs(col - targetCol)) * 55
    SuperWeapon.BATTLESHIP_BARRAGE -> if (row == targetRow) {
        abs(col - targetCol) * 45
    } else {
        40 + abs(row - targetRow) * 55
    }
    SuperWeapon.SONAR_SWEEP -> abs(col - targetCol) * 60
    SuperWeapon.TORPEDO_SPREAD -> abs(row - targetRow) * 60
    SuperWeapon.PRECISION_STRIKE -> if (row == targetRow && col == targetCol) 25 else 90
}
