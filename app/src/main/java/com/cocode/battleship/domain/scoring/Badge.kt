package com.cocode.battleship.domain.scoring

import com.cocode.battleship.domain.model.ShipType

enum class Rarity { COMMON, RARE, EPIC, LEGENDARY }

enum class Badge(val displayName: String, val rarity: Rarity) {
    FIRST_BLOOD("First Blood", Rarity.RARE),
    SHARPSHOOTER("Sharpshooter", Rarity.RARE),
    DEAD_EYE("Dead-Eye", Rarity.EPIC),
    HOT_STREAK("Hot Streak", Rarity.RARE),
    UNSTOPPABLE("Unstoppable", Rarity.EPIC),
    FLAWLESS_VICTORY("Flawless Victory", Rarity.EPIC),
    PERFECT_GUNNER("Perfect Gunner", Rarity.LEGENDARY),
    LEVIATHAN_SLAYER("Leviathan Slayer", Rarity.RARE),
    SILENT_SERVICE("Silent Service", Rarity.RARE),
    LAST_STAND("Last Stand", Rarity.RARE),
    DESTROYER_LIVES("Destroyer Lives", Rarity.COMMON),
    SWIM_FOR_IT("Swim for It", Rarity.RARE),
    FOG_OF_WAR("Fog of War", Rarity.COMMON),
    DEPTH_CHARGE_DIPLOMAT("Depth Charge Diplomat", Rarity.COMMON),
    ON_FIRE("On Fire", Rarity.EPIC),
    BLITZ("Blitz", Rarity.EPIC),
    SEA_WOLF("Sea Wolf", Rarity.EPIC),
    LUCKY_DOG("Lucky Dog", Rarity.RARE),
    COLD_OPENER("Cold Opener", Rarity.COMMON),
    IRON_HULL("Iron Hull", Rarity.RARE),
    CRUISER_LIVES("Cruiser Lives", Rarity.COMMON),
    TORPEDO_ACE("Torpedo Ace", Rarity.RARE),
    BATTLESHIP_HUNTER("Battleship Hunter", Rarity.RARE),
    SMALL_GAME("Small Game", Rarity.COMMON),
    SPRAY_AND_PRAY("Spray and Pray", Rarity.COMMON),
    NUCLEAR_OPTION("Nuclear Option", Rarity.COMMON),
    SCATTERSHOT("Scattershot", Rarity.COMMON),
    TACTICAL_RETREAT("Tactical Retreat", Rarity.RARE),
    PHOENIX("Phoenix", Rarity.COMMON),
    SPITE("Spite", Rarity.RARE),
    FLEET_COMMANDER("Fleet Commander", Rarity.RARE),
    SEA_VETERAN("Sea Veteran", Rarity.COMMON),
    IRON_ADMIRAL("Iron Admiral", Rarity.LEGENDARY);

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
