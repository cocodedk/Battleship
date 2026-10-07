package com.cocode.battleship.domain.scoring

import com.cocode.battleship.domain.model.ShipType

enum class Rarity { COMMON, RARE, EPIC, LEGENDARY }

enum class Badge(val rarity: Rarity) {
    FIRST_BLOOD(Rarity.RARE),
    SHARPSHOOTER(Rarity.RARE),
    DEAD_EYE(Rarity.EPIC),
    HOT_STREAK(Rarity.RARE),
    UNSTOPPABLE(Rarity.EPIC),
    FLAWLESS_VICTORY(Rarity.EPIC),
    PERFECT_GUNNER(Rarity.LEGENDARY),
    LEVIATHAN_SLAYER(Rarity.RARE),
    SILENT_SERVICE(Rarity.RARE),
    LAST_STAND(Rarity.RARE),
    DESTROYER_LIVES(Rarity.COMMON),
    SWIM_FOR_IT(Rarity.RARE),
    FOG_OF_WAR(Rarity.COMMON),
    DEPTH_CHARGE_DIPLOMAT(Rarity.COMMON),
    ON_FIRE(Rarity.EPIC),
    BLITZ(Rarity.EPIC),
    SEA_WOLF(Rarity.EPIC),
    LUCKY_DOG(Rarity.RARE),
    COLD_OPENER(Rarity.COMMON),
    IRON_HULL(Rarity.RARE),
    CRUISER_LIVES(Rarity.COMMON),
    TORPEDO_ACE(Rarity.RARE),
    BATTLESHIP_HUNTER(Rarity.RARE),
    SMALL_GAME(Rarity.COMMON),
    SPRAY_AND_PRAY(Rarity.COMMON),
    NUCLEAR_OPTION(Rarity.COMMON),
    SCATTERSHOT(Rarity.COMMON),
    TACTICAL_RETREAT(Rarity.RARE),
    PHOENIX(Rarity.COMMON),
    SPITE(Rarity.RARE),
    FLEET_COMMANDER(Rarity.RARE),
    SEA_VETERAN(Rarity.COMMON),
    IRON_ADMIRAL(Rarity.LEGENDARY);

    companion object {
        val byName: Map<String, Badge> = entries.associateBy { it.name }
    }

    fun matches(
        stats: GameStats,
        sessionWinStreak: Int = 0,
        sessionTotalWins: Int = 0,
        sessionGamesPlayed: Int = 0
    ): Boolean = when (this) {
        FIRST_BLOOD -> stats.firstShotHit
        SHARPSHOOTER -> stats.accuracy >= 0.60f && stats.totalShots >= 10
        DEAD_EYE -> stats.accuracy >= 0.80f && stats.totalShots >= 10
        HOT_STREAK -> stats.longestHitStreak >= 5
        UNSTOPPABLE -> stats.longestHitStreak >= 8
        FLAWLESS_VICTORY -> stats.outcome == GameOutcome.WIN && stats.survivingPlayerShips == 5
        PERFECT_GUNNER -> stats.outcome == GameOutcome.WIN && stats.misses == 0
        LEVIATHAN_SLAYER -> stats.firstEnemyShipSunkType == ShipType.CARRIER
        SILENT_SERVICE -> stats.playerShipEndStates[ShipType.SUBMARINE] == ShipEndState.UNTOUCHED
        LAST_STAND -> stats.outcome == GameOutcome.WIN && stats.survivingPlayerShips == 1
        DESTROYER_LIVES -> stats.playerShipEndStates[ShipType.DESTROYER] == ShipEndState.UNTOUCHED
        SWIM_FOR_IT -> stats.outcome == GameOutcome.LOSS && stats.hits == 0
        FOG_OF_WAR -> stats.longestMissStreak >= 10
        DEPTH_CHARGE_DIPLOMAT -> stats.totalShots >= 100
        ON_FIRE -> stats.outcome == GameOutcome.WIN && sessionWinStreak >= 3
        BLITZ -> stats.outcome == GameOutcome.WIN && stats.totalShots <= 25
        SEA_WOLF -> stats.outcome == GameOutcome.WIN && sessionWinStreak >= 5
        LUCKY_DOG -> stats.outcome == GameOutcome.WIN && stats.totalShots >= 20 && stats.accuracy < 0.25f
        COLD_OPENER -> stats.outcome == GameOutcome.WIN && !stats.firstShotHit
        IRON_HULL -> stats.playerShipEndStates[ShipType.BATTLESHIP] == ShipEndState.UNTOUCHED
        CRUISER_LIVES -> stats.playerShipEndStates[ShipType.CRUISER] == ShipEndState.UNTOUCHED
        TORPEDO_ACE -> stats.firstEnemyShipSunkType == ShipType.SUBMARINE
        BATTLESHIP_HUNTER -> stats.firstEnemyShipSunkType == ShipType.BATTLESHIP
        SMALL_GAME -> stats.firstEnemyShipSunkType == ShipType.DESTROYER
        SPRAY_AND_PRAY -> stats.longestMissStreak >= 20
        NUCLEAR_OPTION -> stats.totalShots >= 150
        SCATTERSHOT -> stats.totalShots >= 50 && stats.misses > stats.hits
        TACTICAL_RETREAT -> stats.outcome == GameOutcome.LOSS && stats.shipsSunkByPlayer >= 3
        PHOENIX -> stats.outcome == GameOutcome.WIN && stats.survivingPlayerShips == 2
        SPITE -> stats.outcome == GameOutcome.WIN && stats.totalShots >= 30 && stats.accuracy < 0.30f
        FLEET_COMMANDER -> stats.outcome == GameOutcome.WIN && sessionTotalWins >= 10
        SEA_VETERAN -> sessionGamesPlayed >= 25
        IRON_ADMIRAL -> stats.outcome == GameOutcome.WIN && sessionTotalWins >= 25
    }
}
