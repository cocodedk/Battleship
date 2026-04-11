package com.cocode.battleship.domain.scoring

import com.cocode.battleship.domain.model.ShipType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BadgeTest {

    private fun baseStats(
        outcome: GameOutcome = GameOutcome.WIN,
        totalShots: Int = 50,
        hits: Int = 30,
        misses: Int = 20,
        accuracy: Float = 0.6f,
        survivingPlayerShips: Int = 3,
        totalPlayerShipHp: Int = 10,
        shipsSunkByPlayer: Int = 5,
        longestHitStreak: Int = 0,
        longestMissStreak: Int = 0,
        firstShotHit: Boolean = false,
        firstEnemyShipSunkType: ShipType? = null,
        playerShipEndStates: Map<ShipType, ShipEndState> = emptyMap()
    ) = GameStats(
        outcome, totalShots, hits, misses, accuracy,
        survivingPlayerShips, totalPlayerShipHp, shipsSunkByPlayer,
        longestHitStreak, longestMissStreak, firstShotHit,
        firstEnemyShipSunkType, playerShipEndStates
    )

    // FIRST_BLOOD
    @Test fun `FIRST_BLOOD matches when firstShotHit is true`() =
        assertTrue(Badge.FIRST_BLOOD.matches(baseStats(firstShotHit = true)))

    @Test fun `FIRST_BLOOD does not match when firstShotHit is false`() =
        assertFalse(Badge.FIRST_BLOOD.matches(baseStats(firstShotHit = false)))

    // SHARPSHOOTER
    @Test fun `SHARPSHOOTER matches when accuracy 0_60 and totalShots 10`() =
        assertTrue(Badge.SHARPSHOOTER.matches(baseStats(accuracy = 0.60f, totalShots = 10)))

    @Test fun `SHARPSHOOTER does not match when accuracy below 0_60`() =
        assertFalse(Badge.SHARPSHOOTER.matches(baseStats(accuracy = 0.59f, totalShots = 10)))

    @Test fun `SHARPSHOOTER does not match when totalShots below 10`() =
        assertFalse(Badge.SHARPSHOOTER.matches(baseStats(accuracy = 0.60f, totalShots = 9)))

    // DEAD_EYE
    @Test fun `DEAD_EYE matches when accuracy 0_80 and totalShots 10`() =
        assertTrue(Badge.DEAD_EYE.matches(baseStats(accuracy = 0.80f, totalShots = 10)))

    @Test fun `DEAD_EYE does not match when accuracy below 0_80`() =
        assertFalse(Badge.DEAD_EYE.matches(baseStats(accuracy = 0.79f, totalShots = 10)))

    // HOT_STREAK
    @Test fun `HOT_STREAK matches when longestHitStreak is 5`() =
        assertTrue(Badge.HOT_STREAK.matches(baseStats(longestHitStreak = 5)))

    @Test fun `HOT_STREAK does not match when longestHitStreak is 4`() =
        assertFalse(Badge.HOT_STREAK.matches(baseStats(longestHitStreak = 4)))

    // UNSTOPPABLE
    @Test fun `UNSTOPPABLE matches when longestHitStreak is 8`() =
        assertTrue(Badge.UNSTOPPABLE.matches(baseStats(longestHitStreak = 8)))

    @Test fun `UNSTOPPABLE does not match when longestHitStreak is 7`() =
        assertFalse(Badge.UNSTOPPABLE.matches(baseStats(longestHitStreak = 7)))

    // FLAWLESS_VICTORY
    @Test fun `FLAWLESS_VICTORY matches when WIN and 5 surviving ships`() =
        assertTrue(Badge.FLAWLESS_VICTORY.matches(baseStats(outcome = GameOutcome.WIN, survivingPlayerShips = 5)))

    @Test fun `FLAWLESS_VICTORY does not match when LOSS and 5 surviving ships`() =
        assertFalse(Badge.FLAWLESS_VICTORY.matches(baseStats(outcome = GameOutcome.LOSS, survivingPlayerShips = 5)))

    // PERFECT_GUNNER
    @Test fun `PERFECT_GUNNER matches when WIN and misses 0`() =
        assertTrue(Badge.PERFECT_GUNNER.matches(baseStats(outcome = GameOutcome.WIN, misses = 0)))

    @Test fun `PERFECT_GUNNER does not match when LOSS and misses 0`() =
        assertFalse(Badge.PERFECT_GUNNER.matches(baseStats(outcome = GameOutcome.LOSS, misses = 0)))

    @Test fun `PERFECT_GUNNER does not match when WIN and misses 1`() =
        assertFalse(Badge.PERFECT_GUNNER.matches(baseStats(outcome = GameOutcome.WIN, misses = 1)))

    // LEVIATHAN_SLAYER
    @Test fun `LEVIATHAN_SLAYER matches when firstEnemyShipSunkType is CARRIER`() =
        assertTrue(Badge.LEVIATHAN_SLAYER.matches(baseStats(firstEnemyShipSunkType = ShipType.CARRIER)))

    @Test fun `LEVIATHAN_SLAYER does not match when firstEnemyShipSunkType is BATTLESHIP`() =
        assertFalse(Badge.LEVIATHAN_SLAYER.matches(baseStats(firstEnemyShipSunkType = ShipType.BATTLESHIP)))

    @Test fun `LEVIATHAN_SLAYER does not match when firstEnemyShipSunkType is null`() =
        assertFalse(Badge.LEVIATHAN_SLAYER.matches(baseStats(firstEnemyShipSunkType = null)))

    // SILENT_SERVICE
    @Test fun `SILENT_SERVICE matches when SUBMARINE is UNTOUCHED`() =
        assertTrue(Badge.SILENT_SERVICE.matches(baseStats(playerShipEndStates = mapOf(ShipType.SUBMARINE to ShipEndState.UNTOUCHED))))

    @Test fun `SILENT_SERVICE does not match when SUBMARINE is DAMAGED`() =
        assertFalse(Badge.SILENT_SERVICE.matches(baseStats(playerShipEndStates = mapOf(ShipType.SUBMARINE to ShipEndState.DAMAGED))))

    @Test fun `SILENT_SERVICE does not match when SUBMARINE key is missing`() =
        assertFalse(Badge.SILENT_SERVICE.matches(baseStats(playerShipEndStates = emptyMap())))

    // LAST_STAND
    @Test fun `LAST_STAND matches when WIN and 1 surviving ship`() =
        assertTrue(Badge.LAST_STAND.matches(baseStats(outcome = GameOutcome.WIN, survivingPlayerShips = 1)))

    @Test fun `LAST_STAND does not match when WIN and 2 surviving ships`() =
        assertFalse(Badge.LAST_STAND.matches(baseStats(outcome = GameOutcome.WIN, survivingPlayerShips = 2)))

    // DESTROYER_LIVES
    @Test fun `DESTROYER_LIVES matches when DESTROYER is UNTOUCHED`() =
        assertTrue(Badge.DESTROYER_LIVES.matches(baseStats(playerShipEndStates = mapOf(ShipType.DESTROYER to ShipEndState.UNTOUCHED))))

    @Test fun `DESTROYER_LIVES does not match when DESTROYER is SUNK`() =
        assertFalse(Badge.DESTROYER_LIVES.matches(baseStats(playerShipEndStates = mapOf(ShipType.DESTROYER to ShipEndState.SUNK))))

    // SWIM_FOR_IT
    @Test fun `SWIM_FOR_IT matches when LOSS and hits 0`() =
        assertTrue(Badge.SWIM_FOR_IT.matches(baseStats(outcome = GameOutcome.LOSS, hits = 0)))

    @Test fun `SWIM_FOR_IT does not match when WIN and hits 0`() =
        assertFalse(Badge.SWIM_FOR_IT.matches(baseStats(outcome = GameOutcome.WIN, hits = 0)))

    // FOG_OF_WAR
    @Test fun `FOG_OF_WAR matches when longestMissStreak is 10`() =
        assertTrue(Badge.FOG_OF_WAR.matches(baseStats(longestMissStreak = 10)))

    @Test fun `FOG_OF_WAR does not match when longestMissStreak is 9`() =
        assertFalse(Badge.FOG_OF_WAR.matches(baseStats(longestMissStreak = 9)))

    // DEPTH_CHARGE_DIPLOMAT
    @Test fun `DEPTH_CHARGE_DIPLOMAT matches when totalShots is 100`() =
        assertTrue(Badge.DEPTH_CHARGE_DIPLOMAT.matches(baseStats(totalShots = 100)))

    @Test fun `DEPTH_CHARGE_DIPLOMAT does not match when totalShots is 99`() =
        assertFalse(Badge.DEPTH_CHARGE_DIPLOMAT.matches(baseStats(totalShots = 99)))

    // ON_FIRE
    @Test fun `ON_FIRE matches when sessionWinStreak is 3`() =
        assertTrue(Badge.ON_FIRE.matches(baseStats(), sessionWinStreak = 3))

    @Test fun `ON_FIRE does not match when sessionWinStreak is 2`() =
        assertFalse(Badge.ON_FIRE.matches(baseStats(), sessionWinStreak = 2))
}
