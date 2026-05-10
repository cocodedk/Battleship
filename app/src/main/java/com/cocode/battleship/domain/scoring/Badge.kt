package com.cocode.battleship.domain.scoring

import com.cocode.battleship.domain.model.ShipType

enum class Rarity { COMMON, RARE, EPIC, LEGENDARY }

enum class Badge(
    val displayName: String,
    val rarity: Rarity,
    val icon: String,
    val unlockHint: String
) {
    FIRST_BLOOD(
        "First Blood", Rarity.RARE, "🎯",
        "Hit the enemy on your very first shot"
    ),
    SHARPSHOOTER(
        "Sharpshooter", Rarity.RARE, "🏹",
        "Finish with 60% accuracy or better (minimum 10 shots)"
    ),
    DEAD_EYE(
        "Dead-Eye", Rarity.EPIC, "🎯🎯",
        "Finish with 80% accuracy or better (minimum 10 shots)"
    ),
    HOT_STREAK(
        "Hot Streak", Rarity.RARE, "🔥",
        "Land at least 5 hits in a row without missing"
    ),
    UNSTOPPABLE(
        "Unstoppable", Rarity.EPIC, "⚡",
        "Land at least 8 hits in a row without missing"
    ),
    FLAWLESS_VICTORY(
        "Flawless Victory", Rarity.EPIC, "👑",
        "Win with all 5 of your ships still afloat"
    ),
    PERFECT_GUNNER(
        "Perfect Gunner", Rarity.LEGENDARY, "💎",
        "Win without missing a single shot"
    ),
    LEVIATHAN_SLAYER(
        "Leviathan Slayer", Rarity.RARE, "🐋",
        "Sink the enemy Carrier as your first kill"
    ),
    SILENT_SERVICE(
        "Silent Service", Rarity.RARE, "🤫",
        "Keep your Submarine untouched for the entire game"
    ),
    LAST_STAND(
        "Last Stand", Rarity.RARE, "🛡️",
        "Win with only 1 ship surviving"
    ),
    DESTROYER_LIVES(
        "Destroyer Lives", Rarity.COMMON, "🚤",
        "Keep your Destroyer untouched for the entire game"
    ),
    SWIM_FOR_IT(
        "Swim for It", Rarity.RARE, "🏊",
        "Lose without landing a single hit on the enemy"
    ),
    FOG_OF_WAR(
        "Fog of War", Rarity.COMMON, "🌫️",
        "Miss at least 10 shots in a row"
    ),
    DEPTH_CHARGE_DIPLOMAT(
        "Depth Charge Diplomat", Rarity.COMMON, "💣",
        "Fire 100 or more shots in a single game"
    ),
    ON_FIRE(
        "On Fire", Rarity.EPIC, "🔥🔥",
        "Win at least 3 games in a row"
    ),
    BLITZ(
        "Blitz", Rarity.EPIC, "⚡⚡",
        "Win in 25 shots or fewer"
    ),
    SEA_WOLF(
        "Sea Wolf", Rarity.EPIC, "🐺",
        "Win 5 games in a row"
    ),
    LUCKY_DOG(
        "Lucky Dog", Rarity.RARE, "🎲",
        "Win with accuracy below 25% (minimum 20 shots)"
    ),
    COLD_OPENER(
        "Cold Opener", Rarity.COMMON, "❄️",
        "Miss your first shot but still win"
    ),
    IRON_HULL(
        "Iron Hull", Rarity.RARE, "🛡️🛡️",
        "Keep your Battleship untouched for the entire game"
    ),
    CRUISER_LIVES(
        "Cruiser Lives", Rarity.COMMON, "🚢",
        "Keep your Cruiser untouched for the entire game"
    ),
    TORPEDO_ACE(
        "Torpedo Ace", Rarity.RARE, "🌊",
        "Sink the enemy Submarine as your first kill"
    ),
    BATTLESHIP_HUNTER(
        "Battleship Hunter", Rarity.RARE, "⚓",
        "Sink the enemy Battleship as your first kill"
    ),
    SMALL_GAME(
        "Small Game", Rarity.COMMON, "🎣",
        "Sink the enemy Destroyer as your first kill"
    ),
    SPRAY_AND_PRAY(
        "Spray and Pray", Rarity.COMMON, "💨",
        "Miss at least 20 shots in a row"
    ),
    NUCLEAR_OPTION(
        "Nuclear Option", Rarity.COMMON, "☢️",
        "Fire 150 or more shots in a single game"
    ),
    SCATTERSHOT(
        "Scattershot", Rarity.COMMON, "🌀",
        "Fire 50 or more shots and miss more than you hit"
    ),
    TACTICAL_RETREAT(
        "Tactical Retreat", Rarity.RARE, "🏳️",
        "Lose after sinking at least 3 enemy ships"
    ),
    PHOENIX(
        "Phoenix", Rarity.COMMON, "🌅",
        "Win with exactly 2 ships surviving"
    ),
    SPITE(
        "Spite", Rarity.RARE, "😤",
        "Win with accuracy below 30% (minimum 30 shots)"
    ),
    FLEET_COMMANDER(
        "Fleet Commander", Rarity.RARE, "🎖️",
        "Reach 10 lifetime wins"
    ),
    SEA_VETERAN(
        "Sea Veteran", Rarity.COMMON, "⚓⚓",
        "Play your 25th game"
    ),
    IRON_ADMIRAL(
        "Iron Admiral", Rarity.LEGENDARY, "👑⚓",
        "Reach 25 lifetime wins"
    );

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
