package com.cocode.battleship.domain.scoring

import com.cocode.battleship.domain.model.ShipType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BadgeNewTest {

    private fun baseStats(
        outcome: GameOutcome = GameOutcome.WIN,
        totalShots: Int = 50,
        hits: Int = 30,
        misses: Int = 20,
        survivingPlayerShips: Int = 3,
        totalPlayerShipHp: Int = 10,
        shipsSunkByPlayer: Int = 5,
        longestHitStreak: Int = 0,
        longestMissStreak: Int = 0,
        firstShotHit: Boolean = false,
        firstEnemyShipSunkType: ShipType? = null,
        playerShipEndStates: Map<ShipType, ShipEndState> = emptyMap()
    ) = GameStats(
        outcome, totalShots, hits, misses,
        survivingPlayerShips, totalPlayerShipHp, shipsSunkByPlayer,
        longestHitStreak, longestMissStreak, firstShotHit,
        firstEnemyShipSunkType, playerShipEndStates
    )

    // BLITZ
    @Test fun `BLITZ matches when WIN and totalShots is 25`() =
        assertTrue(Badge.BLITZ.matches(baseStats(outcome = GameOutcome.WIN, totalShots = 25)))

    @Test fun `BLITZ does not match when WIN and totalShots is 26`() =
        assertFalse(Badge.BLITZ.matches(baseStats(outcome = GameOutcome.WIN, totalShots = 26)))

    @Test fun `BLITZ does not match when LOSS and totalShots is 20`() =
        assertFalse(Badge.BLITZ.matches(baseStats(outcome = GameOutcome.LOSS, totalShots = 20)))

    // SEA_WOLF
    @Test fun `SEA_WOLF matches when WIN and sessionWinStreak is 5`() =
        assertTrue(Badge.SEA_WOLF.matches(baseStats(outcome = GameOutcome.WIN), sessionWinStreak = 5))

    @Test fun `SEA_WOLF does not match when sessionWinStreak is 4`() =
        assertFalse(Badge.SEA_WOLF.matches(baseStats(outcome = GameOutcome.WIN), sessionWinStreak = 4))

    // LUCKY_DOG
    @Test fun `LUCKY_DOG matches when WIN accuracy below 25 percent with 20 shots`() =
        assertTrue(Badge.LUCKY_DOG.matches(baseStats(outcome = GameOutcome.WIN, totalShots = 20, hits = 4, misses = 16)))

    @Test fun `LUCKY_DOG does not match when accuracy is exactly 25 percent`() =
        assertFalse(Badge.LUCKY_DOG.matches(baseStats(outcome = GameOutcome.WIN, totalShots = 20, hits = 5, misses = 15)))

    @Test fun `LUCKY_DOG does not match when totalShots below 20`() =
        assertFalse(Badge.LUCKY_DOG.matches(baseStats(outcome = GameOutcome.WIN, totalShots = 19, hits = 4, misses = 15)))

    // COLD_OPENER
    @Test fun `COLD_OPENER matches when WIN and first shot was a miss`() =
        assertTrue(Badge.COLD_OPENER.matches(baseStats(outcome = GameOutcome.WIN, firstShotHit = false)))

    @Test fun `COLD_OPENER does not match when WIN and first shot was a hit`() =
        assertFalse(Badge.COLD_OPENER.matches(baseStats(outcome = GameOutcome.WIN, firstShotHit = true)))

    @Test fun `COLD_OPENER does not match when LOSS even if first shot missed`() =
        assertFalse(Badge.COLD_OPENER.matches(baseStats(outcome = GameOutcome.LOSS, firstShotHit = false)))

    // IRON_HULL
    @Test fun `IRON_HULL matches when BATTLESHIP is UNTOUCHED`() =
        assertTrue(Badge.IRON_HULL.matches(baseStats(playerShipEndStates = mapOf(ShipType.BATTLESHIP to ShipEndState.UNTOUCHED))))

    @Test fun `IRON_HULL does not match when BATTLESHIP is DAMAGED`() =
        assertFalse(Badge.IRON_HULL.matches(baseStats(playerShipEndStates = mapOf(ShipType.BATTLESHIP to ShipEndState.DAMAGED))))

    @Test fun `IRON_HULL does not match when BATTLESHIP key is missing`() =
        assertFalse(Badge.IRON_HULL.matches(baseStats(playerShipEndStates = emptyMap())))

    // CRUISER_LIVES
    @Test fun `CRUISER_LIVES matches when CRUISER is UNTOUCHED`() =
        assertTrue(Badge.CRUISER_LIVES.matches(baseStats(playerShipEndStates = mapOf(ShipType.CRUISER to ShipEndState.UNTOUCHED))))

    @Test fun `CRUISER_LIVES does not match when CRUISER is SUNK`() =
        assertFalse(Badge.CRUISER_LIVES.matches(baseStats(playerShipEndStates = mapOf(ShipType.CRUISER to ShipEndState.SUNK))))

    @Test fun `CRUISER_LIVES does not match when CRUISER key is missing`() =
        assertFalse(Badge.CRUISER_LIVES.matches(baseStats(playerShipEndStates = emptyMap())))

    // TORPEDO_ACE
    @Test fun `TORPEDO_ACE matches when firstEnemyShipSunkType is SUBMARINE`() =
        assertTrue(Badge.TORPEDO_ACE.matches(baseStats(firstEnemyShipSunkType = ShipType.SUBMARINE)))

    @Test fun `TORPEDO_ACE does not match when firstEnemyShipSunkType is CARRIER`() =
        assertFalse(Badge.TORPEDO_ACE.matches(baseStats(firstEnemyShipSunkType = ShipType.CARRIER)))

    @Test fun `TORPEDO_ACE does not match when firstEnemyShipSunkType is null`() =
        assertFalse(Badge.TORPEDO_ACE.matches(baseStats(firstEnemyShipSunkType = null)))

    // BATTLESHIP_HUNTER
    @Test fun `BATTLESHIP_HUNTER matches when firstEnemyShipSunkType is BATTLESHIP`() =
        assertTrue(Badge.BATTLESHIP_HUNTER.matches(baseStats(firstEnemyShipSunkType = ShipType.BATTLESHIP)))

    @Test fun `BATTLESHIP_HUNTER does not match when firstEnemyShipSunkType is CARRIER`() =
        assertFalse(Badge.BATTLESHIP_HUNTER.matches(baseStats(firstEnemyShipSunkType = ShipType.CARRIER)))

    @Test fun `BATTLESHIP_HUNTER does not match when firstEnemyShipSunkType is null`() =
        assertFalse(Badge.BATTLESHIP_HUNTER.matches(baseStats(firstEnemyShipSunkType = null)))

    // SMALL_GAME
    @Test fun `SMALL_GAME matches when firstEnemyShipSunkType is DESTROYER`() =
        assertTrue(Badge.SMALL_GAME.matches(baseStats(firstEnemyShipSunkType = ShipType.DESTROYER)))

    @Test fun `SMALL_GAME does not match when firstEnemyShipSunkType is CARRIER`() =
        assertFalse(Badge.SMALL_GAME.matches(baseStats(firstEnemyShipSunkType = ShipType.CARRIER)))

    @Test fun `SMALL_GAME does not match when firstEnemyShipSunkType is null`() =
        assertFalse(Badge.SMALL_GAME.matches(baseStats(firstEnemyShipSunkType = null)))

    // SPRAY_AND_PRAY
    @Test fun `SPRAY_AND_PRAY matches when longestMissStreak is 20`() =
        assertTrue(Badge.SPRAY_AND_PRAY.matches(baseStats(longestMissStreak = 20)))

    @Test fun `SPRAY_AND_PRAY does not match when longestMissStreak is 19`() =
        assertFalse(Badge.SPRAY_AND_PRAY.matches(baseStats(longestMissStreak = 19)))

    // NUCLEAR_OPTION
    @Test fun `NUCLEAR_OPTION matches when totalShots is 150`() =
        assertTrue(Badge.NUCLEAR_OPTION.matches(baseStats(totalShots = 150)))

    @Test fun `NUCLEAR_OPTION does not match when totalShots is 149`() =
        assertFalse(Badge.NUCLEAR_OPTION.matches(baseStats(totalShots = 149)))

    // SCATTERSHOT
    @Test fun `SCATTERSHOT matches when 50 shots and misses exceed hits`() =
        assertTrue(Badge.SCATTERSHOT.matches(baseStats(totalShots = 50, hits = 20, misses = 30)))

    @Test fun `SCATTERSHOT does not match when shots below 50`() =
        assertFalse(Badge.SCATTERSHOT.matches(baseStats(totalShots = 49, hits = 20, misses = 29)))

    @Test fun `SCATTERSHOT does not match when misses equal hits`() =
        assertFalse(Badge.SCATTERSHOT.matches(baseStats(totalShots = 50, hits = 25, misses = 25)))

    // TACTICAL_RETREAT
    @Test fun `TACTICAL_RETREAT matches when LOSS and shipsSunkByPlayer is 3`() =
        assertTrue(Badge.TACTICAL_RETREAT.matches(baseStats(outcome = GameOutcome.LOSS, shipsSunkByPlayer = 3)))

    @Test fun `TACTICAL_RETREAT does not match when LOSS and shipsSunkByPlayer is 2`() =
        assertFalse(Badge.TACTICAL_RETREAT.matches(baseStats(outcome = GameOutcome.LOSS, shipsSunkByPlayer = 2)))

    @Test fun `TACTICAL_RETREAT does not match when WIN even with 3 ships sunk`() =
        assertFalse(Badge.TACTICAL_RETREAT.matches(baseStats(outcome = GameOutcome.WIN, shipsSunkByPlayer = 3)))

    // PHOENIX
    @Test fun `PHOENIX matches when WIN and survivingPlayerShips is 2`() =
        assertTrue(Badge.PHOENIX.matches(baseStats(outcome = GameOutcome.WIN, survivingPlayerShips = 2)))

    @Test fun `PHOENIX does not match when WIN and survivingPlayerShips is 1`() =
        assertFalse(Badge.PHOENIX.matches(baseStats(outcome = GameOutcome.WIN, survivingPlayerShips = 1)))

    @Test fun `PHOENIX does not match when LOSS`() =
        assertFalse(Badge.PHOENIX.matches(baseStats(outcome = GameOutcome.LOSS, survivingPlayerShips = 2)))

    // SPITE
    @Test fun `SPITE matches when WIN accuracy below 30 percent and 30 shots`() =
        assertTrue(Badge.SPITE.matches(baseStats(outcome = GameOutcome.WIN, totalShots = 30, hits = 8, misses = 22)))

    @Test fun `SPITE does not match when accuracy is exactly 30 percent`() =
        assertFalse(Badge.SPITE.matches(baseStats(outcome = GameOutcome.WIN, totalShots = 30, hits = 9, misses = 21)))

    @Test fun `SPITE does not match when totalShots below 30`() =
        assertFalse(Badge.SPITE.matches(baseStats(outcome = GameOutcome.WIN, totalShots = 29, hits = 7, misses = 22)))

    // FLEET_COMMANDER
    @Test fun `FLEET_COMMANDER matches when WIN and sessionTotalWins is exactly 10`() =
        assertTrue(Badge.FLEET_COMMANDER.matches(baseStats(outcome = GameOutcome.WIN), sessionTotalWins = 10))

    @Test fun `FLEET_COMMANDER matches when WIN and sessionTotalWins is 11`() =
        assertTrue(Badge.FLEET_COMMANDER.matches(baseStats(outcome = GameOutcome.WIN), sessionTotalWins = 11))

    @Test fun `FLEET_COMMANDER does not match when LOSS and sessionTotalWins is 10`() =
        assertFalse(Badge.FLEET_COMMANDER.matches(baseStats(outcome = GameOutcome.LOSS), sessionTotalWins = 10))

    // SEA_VETERAN
    @Test fun `SEA_VETERAN matches when sessionGamesPlayed is exactly 25`() =
        assertTrue(Badge.SEA_VETERAN.matches(baseStats(), sessionGamesPlayed = 25))

    @Test fun `SEA_VETERAN does not match when sessionGamesPlayed is 24`() =
        assertFalse(Badge.SEA_VETERAN.matches(baseStats(), sessionGamesPlayed = 24))

    @Test fun `SEA_VETERAN matches when sessionGamesPlayed is 26`() =
        assertTrue(Badge.SEA_VETERAN.matches(baseStats(), sessionGamesPlayed = 26))

    // IRON_ADMIRAL
    @Test fun `IRON_ADMIRAL matches when WIN and sessionTotalWins is exactly 25`() =
        assertTrue(Badge.IRON_ADMIRAL.matches(baseStats(outcome = GameOutcome.WIN), sessionTotalWins = 25))

    @Test fun `IRON_ADMIRAL does not match when sessionTotalWins is 24`() =
        assertFalse(Badge.IRON_ADMIRAL.matches(baseStats(outcome = GameOutcome.WIN), sessionTotalWins = 24))

    @Test fun `IRON_ADMIRAL does not match when LOSS`() =
        assertFalse(Badge.IRON_ADMIRAL.matches(baseStats(outcome = GameOutcome.LOSS), sessionTotalWins = 25))
}
