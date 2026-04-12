package com.cocode.battleship.presentation.game

import com.cocode.battleship.domain.scoring.Badge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SessionStatsTest {

    private lateinit var storage: FakeSessionStatsStorage

    @Before
    fun setUp() {
        storage = FakeSessionStatsStorage()
        SessionStats.initialize(storage)
    }

    @Test
    fun `initialize restores persisted snapshot`() {
        val persisted = SessionStatsSnapshot(
            gamesPlayed = 4,
            totalWins = 3,
            currentWinStreak = 2,
            longestWinStreak = 3,
            bestScore = 1450,
            earnedBadges = setOf(Badge.FIRST_BLOOD, Badge.ON_FIRE)
        )

        SessionStats.initialize(FakeSessionStatsStorage(persisted))

        assertEquals(4, SessionStats.gamesPlayed)
        assertEquals(3, SessionStats.totalWins)
        assertEquals(2, SessionStats.currentWinStreak)
        assertEquals(3, SessionStats.longestWinStreak)
        assertEquals(1450, SessionStats.bestScore)
        assertEquals(setOf(Badge.FIRST_BLOOD, Badge.ON_FIRE), SessionStats.allEarnedBadges)
    }

    @Test
    fun `record updates counters and saves snapshot`() {
        SessionStats.record(
            score = 900,
            isWin = true,
            earnedBadges = listOf(Badge.FIRST_BLOOD, Badge.FIRST_BLOOD, Badge.HOT_STREAK)
        )

        assertEquals(1, SessionStats.gamesPlayed)
        assertEquals(1, SessionStats.totalWins)
        assertEquals(1, SessionStats.currentWinStreak)
        assertEquals(1, SessionStats.longestWinStreak)
        assertEquals(900, SessionStats.bestScore)
        assertEquals(setOf(Badge.FIRST_BLOOD, Badge.HOT_STREAK), SessionStats.allEarnedBadges)

        assertEquals(SessionStats.snapshot(), storage.savedSnapshot)
    }

    @Test
    fun `loss resets current streak and persists updated snapshot`() {
        SessionStats.initialize(
            FakeSessionStatsStorage(
                SessionStatsSnapshot(
                    gamesPlayed = 2,
                    totalWins = 2,
                    currentWinStreak = 2,
                    longestWinStreak = 2,
                    bestScore = 700
                )
            )
        )

        val savingStorage = FakeSessionStatsStorage(SessionStats.snapshot())
        SessionStats.initialize(savingStorage)

        SessionStats.record(score = 250, isWin = false)

        assertEquals(3, SessionStats.gamesPlayed)
        assertEquals(2, SessionStats.totalWins)
        assertEquals(0, SessionStats.currentWinStreak)
        assertEquals(2, SessionStats.longestWinStreak)
        assertEquals(700, SessionStats.bestScore)
        assertEquals(SessionStats.snapshot(), savingStorage.savedSnapshot)
    }

    @Test
    fun `reset clears in memory state and persists empty snapshot`() {
        SessionStats.record(score = 1200, isWin = true, earnedBadges = listOf(Badge.ON_FIRE))

        SessionStats.reset()

        assertEquals(SessionStatsSnapshot(), SessionStats.snapshot())
        assertEquals(SessionStatsSnapshot(), storage.savedSnapshot)
        assertTrue(SessionStats.allEarnedBadges.isEmpty())
    }

    private class FakeSessionStatsStorage(
        private val persistedSnapshot: SessionStatsSnapshot = SessionStatsSnapshot()
    ) : SessionStatsStorage {
        var savedSnapshot: SessionStatsSnapshot? = null

        override fun load(): SessionStatsSnapshot = persistedSnapshot

        override fun save(snapshot: SessionStatsSnapshot) {
            savedSnapshot = snapshot
        }
    }
}
