package com.cocode.battleship.presentation.game

import android.content.Context
import com.cocode.battleship.domain.scoring.Badge

class SharedPreferencesSessionStatsStorage(context: Context) : SessionStatsStorage {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun load(): SessionStatsSnapshot = SessionStatsSnapshot(
        gamesPlayed = prefs.getInt(KEY_GAMES_PLAYED, 0),
        totalWins = prefs.getInt(KEY_TOTAL_WINS, 0),
        currentWinStreak = prefs.getInt(KEY_CURRENT_WIN_STREAK, 0),
        longestWinStreak = prefs.getInt(KEY_LONGEST_WIN_STREAK, 0),
        bestScore = prefs.getInt(KEY_BEST_SCORE, 0),
        earnedBadges = prefs.getStringSet(KEY_EARNED_BADGES, emptySet())
            ?.mapNotNull { badgeName -> Badge.entries.find { it.name == badgeName } }
            ?.toSet()
            ?: emptySet(),
        totalShotsLifetime = prefs.getInt(KEY_TOTAL_SHOTS_LIFETIME, 0),
        totalHitsLifetime = prefs.getInt(KEY_TOTAL_HITS_LIFETIME, 0)
    )

    override fun save(snapshot: SessionStatsSnapshot) {
        prefs.edit()
            .putInt(KEY_GAMES_PLAYED, snapshot.gamesPlayed)
            .putInt(KEY_TOTAL_WINS, snapshot.totalWins)
            .putInt(KEY_CURRENT_WIN_STREAK, snapshot.currentWinStreak)
            .putInt(KEY_LONGEST_WIN_STREAK, snapshot.longestWinStreak)
            .putInt(KEY_BEST_SCORE, snapshot.bestScore)
            .putStringSet(KEY_EARNED_BADGES, snapshot.earnedBadges.mapTo(mutableSetOf()) { it.name })
            .putInt(KEY_TOTAL_SHOTS_LIFETIME, snapshot.totalShotsLifetime)
            .putInt(KEY_TOTAL_HITS_LIFETIME, snapshot.totalHitsLifetime)
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "career_stats"
        const val KEY_GAMES_PLAYED = "games_played"
        const val KEY_TOTAL_WINS = "total_wins"
        const val KEY_CURRENT_WIN_STREAK = "current_win_streak"
        const val KEY_LONGEST_WIN_STREAK = "longest_win_streak"
        const val KEY_BEST_SCORE = "best_score"
        const val KEY_EARNED_BADGES = "earned_badges"
        const val KEY_TOTAL_SHOTS_LIFETIME = "total_shots_lifetime"
        const val KEY_TOTAL_HITS_LIFETIME = "total_hits_lifetime"
    }
}
