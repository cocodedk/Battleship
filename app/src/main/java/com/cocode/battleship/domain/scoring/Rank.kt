package com.cocode.battleship.domain.scoring

enum class Rank(val minScore: Int) {
    FLEET_ADMIRAL(3500),
    ADMIRAL(2600),
    VICE_ADMIRAL(2000),
    COMMODORE(1500),
    CAPTAIN(1100),
    LIEUTENANT(700),
    ENSIGN(350),
    CADET(0);

    companion object {
        fun fromScore(score: Int): Rank =
            entries.sortedByDescending { it.minScore }.firstOrNull { score >= it.minScore } ?: CADET
    }
}
