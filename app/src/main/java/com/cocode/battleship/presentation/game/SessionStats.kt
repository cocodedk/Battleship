package com.cocode.battleship.presentation.game

import com.cocode.battleship.domain.scoring.Badge
import com.cocode.battleship.presentation.medals.MedalsStorage

data class SessionStatsSnapshot(
    val gamesPlayed: Int = 0,
    val totalWins: Int = 0,
    val currentWinStreak: Int = 0,
    val longestWinStreak: Int = 0,
    val bestScore: Int = 0,
    val earnedBadges: Set<Badge> = emptySet()
)

interface SessionStatsStorage {
    fun load(): SessionStatsSnapshot
    fun save(snapshot: SessionStatsSnapshot)
}

object SessionStats {
    private var storage: SessionStatsStorage? = null
    private var medalsStorage: MedalsStorage? = null

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

    fun initialize(storage: SessionStatsStorage, medalsStorage: MedalsStorage? = null) {
        this.storage = storage
        this.medalsStorage = medalsStorage
        restore(storage.load())
    }

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
        if (earnedBadges.isNotEmpty()) medalsStorage?.increment(earnedBadges)
        persist()
    }

    fun reset() {
        restore(SessionStatsSnapshot())
        persist()
    }

    internal fun snapshot(): SessionStatsSnapshot = SessionStatsSnapshot(
        gamesPlayed = gamesPlayed,
        totalWins = totalWins,
        currentWinStreak = currentWinStreak,
        longestWinStreak = longestWinStreak,
        bestScore = bestScore,
        earnedBadges = _allEarnedBadges.toSet()
    )

    private fun restore(snapshot: SessionStatsSnapshot) {
        gamesPlayed = snapshot.gamesPlayed
        totalWins = snapshot.totalWins
        currentWinStreak = snapshot.currentWinStreak
        longestWinStreak = snapshot.longestWinStreak
        bestScore = snapshot.bestScore
        _allEarnedBadges.clear()
        _allEarnedBadges.addAll(snapshot.earnedBadges)
    }

    private fun persist() {
        storage?.save(snapshot())
    }
}
