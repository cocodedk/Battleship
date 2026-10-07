package com.cocode.battleship.presentation.game

import com.cocode.battleship.domain.model.Board
import com.cocode.battleship.domain.model.CellState
import com.cocode.battleship.domain.model.ShipType
import com.cocode.battleship.domain.model.SuperWeapon
import com.cocode.battleship.domain.model.resolveWeaponCells
import com.cocode.battleship.domain.scoring.GameOutcome
import com.cocode.battleship.domain.scoring.GameStats
import com.cocode.battleship.domain.scoring.ScoreResult

/** The ship types that sank on the shot that turned [before] into [after]. */
fun newlySunk(before: Board, after: Board): Set<ShipType> =
    sunkTypes(after) - sunkTypes(before)

private fun sunkTypes(board: Board): Set<ShipType> =
    board.ships.filter { it.isSunk }.map { it.type }.toSet()

/**
 * The weapons the player holds after a shot: the weapon just [used] is spent, and each newly
 * sunk ship unlocks its weapon, unless the player already had it or has just spent it.
 */
fun weaponsAfterAttack(
    available: List<SuperWeapon>,
    used: SuperWeapon?,
    newlySunk: Set<ShipType>
): List<SuperWeapon> {
    val alreadyGranted = (available + listOfNotNull(used)).map { it.unlockShip }.toSet()
    return available.filter { it != used } +
        newlySunk.filter { it !in alreadyGranted }.map { SuperWeapon.forShipType(it) }
}

/** The effect of firing [weapon] at ([row], [col]); [triggerId] tells one firing from the next. */
fun weaponEffectFor(weapon: SuperWeapon, triggerId: Int, row: Int, col: Int) = SuperWeaponEffect(
    triggerId = triggerId,
    weapon = weapon,
    targetRow = row,
    targetCol = col,
    cells = resolveWeaponCells(weapon, row, col).toSet()
)

/** What the status line says after the AI fired at ([row], [col]) on the player's [board]. */
fun aiAttackMessage(cellState: CellState, board: Board, row: Int, col: Int): GameMessage =
    when (cellState) {
        CellState.HIT -> GameMessage.AiHit
        CellState.SUNK -> GameMessage.AiSunk(
            board.ships.find { it.isSunk && it.occupies(row, col) }?.type
        )
        else -> GameMessage.AiMissed
    }

/** Scores a finished game and records it in the session stats. */
fun recordFinishedGame(stats: GameStats): ScoreResult {
    val won = stats.outcome == GameOutcome.WIN
    val bump = if (won) 1 else 0
    val result = computeScoreResult(
        stats,
        sessionWinStreak = SessionStats.currentWinStreak + bump,
        sessionTotalWins = SessionStats.totalWins + bump,
        sessionGamesPlayed = SessionStats.gamesPlayed + 1
    )
    SessionStats.record(
        result.score,
        isWin = won,
        earnedBadges = result.earnedBadges,
        totalShots = result.stats.totalShots,
        hits = result.stats.hits
    )
    return result
}
