package com.cocode.battleship.presentation.game

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

    fun record(score: Int, isWin: Boolean) {
        gamesPlayed++
        if (isWin) {
            totalWins++
            currentWinStreak++
            if (currentWinStreak > longestWinStreak) longestWinStreak = currentWinStreak
        } else {
            currentWinStreak = 0
        }
        if (score > bestScore) bestScore = score
    }

    fun reset() {
        gamesPlayed = 0
        totalWins = 0
        currentWinStreak = 0
        longestWinStreak = 0
        bestScore = 0
    }
}
