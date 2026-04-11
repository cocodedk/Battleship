package com.cocode.battleship.domain.scoring

import kotlin.math.max
import kotlin.math.roundToInt

object ScoreCalculator {

    fun calculate(stats: GameStats): Int {
        val components = listOf(
            base(stats),
            victoryBonus(stats),
            accuracyBonus(stats),
            speedBonus(stats),
            streakBonus(stats),
            sinkBonus(stats),
            flourishBonus(stats),
            penalties(stats)
        )
        return max(0, components.sum())
    }

    private fun base(stats: GameStats): Int =
        if (stats.outcome == GameOutcome.WIN) 1000 else 200

    private fun victoryBonus(stats: GameStats): Int {
        if (stats.outcome != GameOutcome.WIN) return 0
        return 300 * stats.survivingPlayerShips + 5 * stats.totalPlayerShipHp
    }

    private fun accuracyBonus(stats: GameStats): Int {
        var bonus = (stats.accuracy * 500f).roundToInt()
        if (stats.accuracy >= 0.50f) bonus += 250
        val perfectGame = stats.outcome == GameOutcome.WIN && stats.misses == 0
        if (perfectGame) bonus += 500
        return bonus
    }

    private fun speedBonus(stats: GameStats): Int {
        if (stats.outcome != GameOutcome.WIN) return 0
        return max(0, (50 - stats.totalShots) * 25)
    }

    private fun streakBonus(stats: GameStats): Int {
        var bonus = stats.longestHitStreak * 40
        if (stats.longestHitStreak >= 5) bonus += 200
        if (stats.longestHitStreak >= 8) bonus += 500
        return bonus
    }

    private fun sinkBonus(stats: GameStats): Int = 100 * stats.shipsSunkByPlayer

    private fun flourishBonus(stats: GameStats): Int = if (stats.firstShotHit) 150 else 0

    private fun penalties(stats: GameStats): Int {
        var penalty = 0
        penalty -= 10 * max(0, stats.totalShots - 80)
        if (stats.outcome == GameOutcome.LOSS && stats.hits == 0) penalty -= 50
        return penalty
    }
}
