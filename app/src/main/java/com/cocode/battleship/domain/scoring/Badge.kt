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
        "Win at least 3 games in a row in the same session"
    );

    companion object {
        val byName: Map<String, Badge> = entries.associateBy { it.name }
    }

    fun matches(stats: GameStats, sessionWinStreak: Int = 0): Boolean = when (this) {
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
    }
}
