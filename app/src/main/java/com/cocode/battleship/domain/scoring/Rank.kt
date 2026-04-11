package com.cocode.battleship.domain.scoring

enum class Rank(val minScore: Int, val displayName: String) {
    FLEET_ADMIRAL(3500, "Fleet Admiral"),
    ADMIRAL(2600, "Admiral"),
    VICE_ADMIRAL(2000, "Vice Admiral"),
    COMMODORE(1500, "Commodore"),
    CAPTAIN(1100, "Captain"),
    LIEUTENANT(700, "Lieutenant"),
    ENSIGN(350, "Ensign"),
    CADET(0, "Cadet");

    companion object {
        fun fromScore(score: Int): Rank =
            entries.sortedByDescending { it.minScore }.first { score >= it.minScore }
    }
}
