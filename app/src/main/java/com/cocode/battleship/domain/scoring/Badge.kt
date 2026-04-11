package com.cocode.battleship.domain.scoring

import com.cocode.battleship.domain.model.ShipType

enum class Rarity { COMMON, RARE, EPIC, LEGENDARY }

enum class Badge(
    val displayName: String,
    val rarity: Rarity,
    val icon: String
) {
    FIRST_BLOOD("First Blood", Rarity.RARE, "🎯"),
    SHARPSHOOTER("Sharpshooter", Rarity.RARE, "🏹"),
    DEAD_EYE("Dead-Eye", Rarity.EPIC, "🎯🎯"),
    HOT_STREAK("Hot Streak", Rarity.RARE, "🔥"),
    UNSTOPPABLE("Unstoppable", Rarity.EPIC, "⚡"),
    FLAWLESS_VICTORY("Flawless Victory", Rarity.EPIC, "👑"),
    PERFECT_GUNNER("Perfect Gunner", Rarity.LEGENDARY, "💎"),
    LEVIATHAN_SLAYER("Leviathan Slayer", Rarity.RARE, "🐋"),
    SILENT_SERVICE("Silent Service", Rarity.RARE, "🤫"),
    LAST_STAND("Last Stand", Rarity.RARE, "🛡️"),
    DESTROYER_LIVES("Destroyer Lives", Rarity.COMMON, "🚤"),
    SWIM_FOR_IT("Swim for It", Rarity.RARE, "🏊"),
    FOG_OF_WAR("Fog of War", Rarity.COMMON, "🌫️"),
    DEPTH_CHARGE_DIPLOMAT("Depth Charge Diplomat", Rarity.COMMON, "💣"),
    ON_FIRE("On Fire", Rarity.EPIC, "🔥🔥");

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
        ON_FIRE -> sessionWinStreak >= 3
    }
}
