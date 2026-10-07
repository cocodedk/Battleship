package com.cocode.battleship.presentation.game

import com.cocode.battleship.domain.model.Board
import com.cocode.battleship.domain.model.CellState
import com.cocode.battleship.domain.model.Ship
import com.cocode.battleship.domain.model.ShipType
import com.cocode.battleship.domain.model.SuperWeapon
import com.cocode.battleship.domain.scoring.GameOutcome
import com.cocode.battleship.domain.scoring.GameStats
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GameAttackOutcomeTest {
    private val destroyer = Ship(ShipType.DESTROYER, 0, 0, true)

    @Test
    fun `a shot that completes a ship reports that ship as newly sunk`() {
        val before = Board().placeShip(destroyer).receiveAttack(0, 0)
        val after = before.receiveAttack(0, 1)
        assertEquals(setOf(ShipType.DESTROYER), newlySunk(before, after))
    }

    @Test
    fun `a shot that sinks nothing reports no newly sunk ships`() {
        val before = Board().placeShip(destroyer)
        assertEquals(emptySet<ShipType>(), newlySunk(before, before.receiveAttack(0, 0)))
    }

    @Test
    fun `an already sunk ship is not newly sunk again`() {
        val sunk = Board().placeShip(destroyer).receiveAttack(0, 0).receiveAttack(0, 1)
        assertEquals(emptySet<ShipType>(), newlySunk(sunk, sunk.receiveAttack(5, 5)))
    }

    @Test
    fun `sinking a ship unlocks its weapon`() {
        assertEquals(
            listOf(SuperWeapon.PRECISION_STRIKE),
            weaponsAfterAttack(emptyList(), null, setOf(ShipType.DESTROYER))
        )
    }

    @Test
    fun `the weapon just fired is spent`() {
        assertEquals(
            emptyList<SuperWeapon>(),
            weaponsAfterAttack(listOf(SuperWeapon.SONAR_SWEEP), SuperWeapon.SONAR_SWEEP, emptySet())
        )
    }

    @Test
    fun `a weapon the player already holds is not granted twice`() {
        assertEquals(
            listOf(SuperWeapon.PRECISION_STRIKE),
            weaponsAfterAttack(listOf(SuperWeapon.PRECISION_STRIKE), null, setOf(ShipType.DESTROYER))
        )
    }

    @Test
    fun `a weapon fired in the same shot that sinks its ship is not granted again`() {
        assertEquals(
            emptyList<SuperWeapon>(),
            weaponsAfterAttack(listOf(SuperWeapon.PRECISION_STRIKE), SuperWeapon.PRECISION_STRIKE, setOf(ShipType.DESTROYER))
        )
    }

    @Test
    fun `the weapon effect covers the cells the weapon reaches`() {
        val effect = weaponEffectFor(SuperWeapon.SONAR_SWEEP, triggerId = 7, row = 4, col = 4)
        assertEquals(7, effect.triggerId)
        assertEquals(setOf(4 to 2, 4 to 3, 4 to 4, 4 to 5, 4 to 6), effect.cells)
    }

    @Test
    fun `an AI hit is reported as a hit`() {
        assertEquals(GameMessage.AiHit, aiAttackMessage(CellState.HIT, Board(), 0, 0))
    }

    @Test
    fun `an AI miss is reported as a miss`() {
        assertEquals(GameMessage.AiMissed, aiAttackMessage(CellState.MISS, Board(), 0, 0))
    }

    @Test
    fun `an AI sinking names the ship at the fired cell`() {
        val board = Board().placeShip(destroyer).receiveAttack(0, 0).receiveAttack(0, 1)
        assertEquals(GameMessage.AiSunk(ShipType.DESTROYER), aiAttackMessage(CellState.SUNK, board, 0, 1))
    }

    @Test
    fun `an AI sinking with no sunk ship at the cell names no ship`() {
        assertEquals(GameMessage.AiSunk(null), aiAttackMessage(CellState.SUNK, Board(), 3, 3))
    }

    @Test
    fun `a recorded win adds a game, a win and a streak step`() {
        startSession(gamesPlayed = 2, totalWins = 1, streak = 1)
        val result = recordFinishedGame(stats(GameOutcome.WIN))
        assertEquals(3, SessionStats.gamesPlayed)
        assertEquals(2, SessionStats.totalWins)
        assertEquals(2, SessionStats.currentWinStreak)
        assertEquals(result.score, SessionStats.bestScore)
    }

    @Test
    fun `a recorded loss adds a game and ends the streak`() {
        startSession(gamesPlayed = 2, totalWins = 1, streak = 1)
        recordFinishedGame(stats(GameOutcome.LOSS))
        assertEquals(3, SessionStats.gamesPlayed)
        assertEquals(1, SessionStats.totalWins)
        assertEquals(0, SessionStats.currentWinStreak)
    }

    @Before
    fun resetSession() = startSession()

    private fun startSession(gamesPlayed: Int = 0, totalWins: Int = 0, streak: Int = 0) {
        val snapshot = SessionStatsSnapshot(
            gamesPlayed = gamesPlayed, totalWins = totalWins,
            currentWinStreak = streak, longestWinStreak = streak
        )
        SessionStats.initialize(object : SessionStatsStorage {
            override fun load() = snapshot
            override fun save(snapshot: SessionStatsSnapshot) = Unit
        })
    }

    private fun stats(outcome: GameOutcome) = GameStats(
        outcome = outcome, totalShots = 30, hits = 20, misses = 10,
        survivingPlayerShips = 2, totalPlayerShipHp = 5, shipsSunkByPlayer = 5,
        longestHitStreak = 3, longestMissStreak = 2, firstShotHit = false,
        firstEnemyShipSunkType = null, playerShipEndStates = emptyMap()
    )
}
