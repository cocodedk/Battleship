package com.cocode.battleship.presentation.game

import com.cocode.battleship.domain.model.Board
import com.cocode.battleship.domain.model.CellState
import com.cocode.battleship.domain.model.ShipType
import com.cocode.battleship.domain.scoring.Badge
import com.cocode.battleship.domain.scoring.GameOutcome
import com.cocode.battleship.domain.scoring.GameStats
import com.cocode.battleship.domain.scoring.Rank
import com.cocode.battleship.domain.scoring.ScoreCalculator
import com.cocode.battleship.domain.scoring.ScoreResult
import com.cocode.battleship.domain.scoring.ShipEndState

data class TrackerState(
    val totalShots: Int = 0,
    val hits: Int = 0,
    val misses: Int = 0,
    val currentHitStreak: Int = 0,
    val longestHitStreak: Int = 0,
    val currentMissStreak: Int = 0,
    val longestMissStreak: Int = 0,
    val firstShotHit: Boolean = false,
    val firstEnemyShipSunkType: ShipType? = null
)

fun updateTrackers(
    current: TrackerState,
    cellState: CellState,
    newlySunkType: ShipType?
): TrackerState {
    val isHit = cellState == CellState.HIT || cellState == CellState.SUNK
    val isMiss = cellState == CellState.MISS
    val isFirstShot = current.totalShots == 0

    val newHitStreak = if (isHit) current.currentHitStreak + 1 else 0
    val newMissStreak = if (isMiss) current.currentMissStreak + 1 else 0

    return current.copy(
        totalShots = current.totalShots + 1,
        hits = if (isHit) current.hits + 1 else current.hits,
        misses = if (isMiss) current.misses + 1 else current.misses,
        currentHitStreak = newHitStreak,
        longestHitStreak = maxOf(current.longestHitStreak, newHitStreak),
        currentMissStreak = newMissStreak,
        longestMissStreak = maxOf(current.longestMissStreak, newMissStreak),
        firstShotHit = if (isFirstShot && isHit) true else current.firstShotHit,
        firstEnemyShipSunkType = if (current.firstEnemyShipSunkType == null && newlySunkType != null)
            newlySunkType else current.firstEnemyShipSunkType
    )
}

fun buildGameStats(
    trackers: TrackerState,
    playerBoard: Board,
    aiBoard: Board,
    outcome: GameOutcome
): GameStats {
    val surviving = playerBoard.ships.count { !it.isSunk }
    val totalHp = playerBoard.ships.filter { !it.isSunk }
        .sumOf { it.type.size - it.hitPositions.size }
    val sunkByPlayer = aiBoard.ships.count { it.isSunk }
    val endStates = playerBoard.ships.associate { ship ->
        ship.type to when {
            ship.isSunk -> ShipEndState.SUNK
            ship.hitPositions.isNotEmpty() -> ShipEndState.DAMAGED
            else -> ShipEndState.UNTOUCHED
        }
    }
    return GameStats(
        outcome = outcome,
        totalShots = trackers.totalShots,
        hits = trackers.hits,
        misses = trackers.misses,
        survivingPlayerShips = surviving,
        totalPlayerShipHp = totalHp,
        shipsSunkByPlayer = sunkByPlayer,
        longestHitStreak = trackers.longestHitStreak,
        longestMissStreak = trackers.longestMissStreak,
        firstShotHit = trackers.firstShotHit,
        firstEnemyShipSunkType = trackers.firstEnemyShipSunkType,
        playerShipEndStates = endStates
    )
}

fun computeScoreResult(
    stats: GameStats,
    sessionWinStreak: Int
): ScoreResult {
    val score = ScoreCalculator.calculate(stats)
    val rank = Rank.fromScore(score)
    val badges = Badge.entries.filter { it.matches(stats, sessionWinStreak) }
    return ScoreResult(score, rank, badges, stats)
}
