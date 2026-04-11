package com.cocode.battleship.domain.scoring

import com.cocode.battleship.domain.model.ShipType

enum class GameOutcome { WIN, LOSS }

enum class ShipEndState { UNTOUCHED, DAMAGED, SUNK }

data class GameStats(
    val outcome: GameOutcome,
    val totalShots: Int,
    val hits: Int,
    val misses: Int,
    val accuracy: Float,                            // hits / totalShots, or 0f if totalShots == 0
    val survivingPlayerShips: Int,
    val totalPlayerShipHp: Int,                     // sum of (size - hitCount) for surviving player ships
    val shipsSunkByPlayer: Int,
    val longestHitStreak: Int,
    val longestMissStreak: Int,
    val firstShotHit: Boolean,
    val firstEnemyShipSunkType: ShipType?,
    val playerShipEndStates: Map<ShipType, ShipEndState>
)
