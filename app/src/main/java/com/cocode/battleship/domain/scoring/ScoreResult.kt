package com.cocode.battleship.domain.scoring

data class ScoreResult(
    val score: Int,
    val rank: Rank,
    val earnedBadges: List<Badge>,
    val stats: GameStats
)
