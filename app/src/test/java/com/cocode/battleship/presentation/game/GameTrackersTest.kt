package com.cocode.battleship.presentation.game

import com.cocode.battleship.domain.model.CellState
import com.cocode.battleship.domain.model.ShipType
import com.cocode.battleship.domain.scoring.Badge
import com.cocode.battleship.domain.scoring.GameOutcome
import com.cocode.battleship.domain.scoring.GameStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class GameTrackersTest {

    private fun blank() = TrackerState()

    @Test
    fun `hit increments currentHitStreak and resets currentMissStreak to 0`() {
        val state = blank().copy(currentMissStreak = 3)
        val result = updateTrackers(state, CellState.HIT, null)
        assertEquals(1, result.currentHitStreak)
        assertEquals(0, result.currentMissStreak)
    }

    @Test
    fun `miss increments currentMissStreak and resets currentHitStreak to 0`() {
        val state = blank().copy(currentHitStreak = 4)
        val result = updateTrackers(state, CellState.MISS, null)
        assertEquals(1, result.currentMissStreak)
        assertEquals(0, result.currentHitStreak)
    }

    @Test
    fun `sunk counts as a hit for streak purposes`() {
        val state = blank().copy(currentMissStreak = 2)
        val result = updateTrackers(state, CellState.SUNK, ShipType.DESTROYER)
        assertEquals(1, result.currentHitStreak)
        assertEquals(0, result.currentMissStreak)
    }

    @Test
    fun `longestHitStreak updates when currentHitStreak exceeds it`() {
        val state = blank().copy(currentHitStreak = 4, longestHitStreak = 4)
        val result = updateTrackers(state, CellState.HIT, null)
        assertEquals(5, result.longestHitStreak)
    }

    @Test
    fun `longestHitStreak does NOT update when currentHitStreak is equal or less`() {
        val state = blank().copy(currentHitStreak = 2, longestHitStreak = 5)
        val result = updateTrackers(state, CellState.HIT, null)
        assertEquals(5, result.longestHitStreak)
    }

    @Test
    fun `longestMissStreak updates when currentMissStreak exceeds it`() {
        val state = blank().copy(currentMissStreak = 3, longestMissStreak = 3)
        val result = updateTrackers(state, CellState.MISS, null)
        assertEquals(4, result.longestMissStreak)
    }

    @Test
    fun `firstShotHit is true when first shot is HIT`() {
        val result = updateTrackers(blank(), CellState.HIT, null)
        assertTrue(result.firstShotHit)
    }

    @Test
    fun `firstShotHit is false when first shot is MISS`() {
        val result = updateTrackers(blank(), CellState.MISS, null)
        assertFalse(result.firstShotHit)
    }

    @Test
    fun `firstShotHit stays true after subsequent misses`() {
        val afterFirstHit = updateTrackers(blank(), CellState.HIT, null)
        val result = updateTrackers(afterFirstHit, CellState.MISS, null)
        assertTrue(result.firstShotHit)
    }

    @Test
    fun `firstShotHit stays false after subsequent hits`() {
        val afterFirstMiss = updateTrackers(blank(), CellState.MISS, null)
        val result = updateTrackers(afterFirstMiss, CellState.HIT, null)
        assertFalse(result.firstShotHit)
    }

    @Test
    fun `firstEnemyShipSunkType is set when ship is sunk`() {
        val result = updateTrackers(blank(), CellState.SUNK, ShipType.CARRIER)
        assertEquals(ShipType.CARRIER, result.firstEnemyShipSunkType)
    }

    @Test
    fun `firstEnemyShipSunkType is NOT overwritten by second sunk`() {
        val afterFirst = updateTrackers(blank(), CellState.SUNK, ShipType.CARRIER)
        val result = updateTrackers(afterFirst, CellState.SUNK, ShipType.DESTROYER)
        assertEquals(ShipType.CARRIER, result.firstEnemyShipSunkType)
    }

    @Test
    fun `firstEnemyShipSunkType stays null when no ship is sunk`() {
        val result = updateTrackers(blank(), CellState.HIT, null)
        assertNull(result.firstEnemyShipSunkType)
    }

    @Test
    fun `totalShots increments on every call`() {
        val after1 = updateTrackers(blank(), CellState.HIT, null)
        val after2 = updateTrackers(after1, CellState.MISS, null)
        val after3 = updateTrackers(after2, CellState.SUNK, ShipType.BATTLESHIP)
        assertEquals(3, after3.totalShots)
    }

    @Test
    fun `hits increments on HIT or SUNK cellState`() {
        val afterHit = updateTrackers(blank(), CellState.HIT, null)
        val afterSunk = updateTrackers(afterHit, CellState.SUNK, ShipType.SUBMARINE)
        assertEquals(2, afterSunk.hits)
    }

    @Test
    fun `misses increments on MISS cellState`() {
        val after1 = updateTrackers(blank(), CellState.MISS, null)
        val after2 = updateTrackers(after1, CellState.MISS, null)
        assertEquals(2, after2.misses)
    }

    private fun scoreStats(outcome: GameOutcome = GameOutcome.WIN) = GameStats(
        outcome = outcome,
        totalShots = 50, hits = 30, misses = 20,
        survivingPlayerShips = 3, totalPlayerShipHp = 10, shipsSunkByPlayer = 5,
        longestHitStreak = 0, longestMissStreak = 0,
        firstShotHit = false, firstEnemyShipSunkType = null, playerShipEndStates = emptyMap()
    )

    @Test fun `computeScoreResult includes FLEET_COMMANDER when sessionTotalWins is 10`() {
        val result = computeScoreResult(scoreStats(), sessionWinStreak = 0, sessionTotalWins = 10, sessionGamesPlayed = 10)
        assertTrue(Badge.FLEET_COMMANDER in result.earnedBadges)
    }

    @Test fun `computeScoreResult does not include FLEET_COMMANDER when sessionTotalWins is 11`() {
        val result = computeScoreResult(scoreStats(), sessionWinStreak = 0, sessionTotalWins = 11, sessionGamesPlayed = 11)
        assertFalse(Badge.FLEET_COMMANDER in result.earnedBadges)
    }

    @Test fun `computeScoreResult includes IRON_ADMIRAL when sessionTotalWins is 25`() {
        val result = computeScoreResult(scoreStats(), sessionWinStreak = 0, sessionTotalWins = 25, sessionGamesPlayed = 25)
        assertTrue(Badge.IRON_ADMIRAL in result.earnedBadges)
    }

    @Test fun `computeScoreResult includes SEA_VETERAN when sessionGamesPlayed is 25`() {
        val result = computeScoreResult(scoreStats(), sessionWinStreak = 0, sessionTotalWins = 0, sessionGamesPlayed = 25)
        assertTrue(Badge.SEA_VETERAN in result.earnedBadges)
    }

    @Test fun `computeScoreResult with default session params does not award progression badges`() {
        val result = computeScoreResult(scoreStats(), sessionWinStreak = 0)
        assertFalse(Badge.FLEET_COMMANDER in result.earnedBadges)
        assertFalse(Badge.IRON_ADMIRAL in result.earnedBadges)
        assertFalse(Badge.SEA_VETERAN in result.earnedBadges)
    }
}
