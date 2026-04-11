package com.cocode.battleship.presentation.game

import com.cocode.battleship.domain.scoring.Badge

object SessionStats {
    var gamesPlayed: Int = 0
        private set
    var totalWins: Int = 0
        private set
    var currentWinStreak: Int = 0
        private set
    var longestWinStreak: Int = 0
        private set
    var bestScore: Int = 0
        private set
    private val _allEarnedBadges: MutableSet<Badge> = mutableSetOf()
    val allEarnedBadges: Set<Badge> get() = _allEarnedBadges

    fun record(score: Int, isWin: Boolean, earnedBadges: List<Badge> = emptyList()) {
        gamesPlayed++
        if (isWin) {
            totalWins++
            currentWinStreak++
            if (currentWinStreak > longestWinStreak) longestWinStreak = currentWinStreak
        } else {
            currentWinStreak = 0
        }
        if (score > bestScore) bestScore = score
        _allEarnedBadges.addAll(earnedBadges)
    }

    fun reset() {
        gamesPlayed = 0
        totalWins = 0
        currentWinStreak = 0
        longestWinStreak = 0
        bestScore = 0
        _allEarnedBadges.clear()
    }
}
