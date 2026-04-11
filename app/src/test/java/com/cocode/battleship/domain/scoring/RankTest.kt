package com.cocode.battleship.domain.scoring

import org.junit.Assert.assertEquals
import org.junit.Test

class RankTest {

    @Test
    fun `score 0 yields CADET`() {
        assertEquals(Rank.CADET, Rank.fromScore(0))
    }

    @Test
    fun `score 349 yields CADET`() {
        assertEquals(Rank.CADET, Rank.fromScore(349))
    }

    @Test
    fun `score 350 yields ENSIGN`() {
        assertEquals(Rank.ENSIGN, Rank.fromScore(350))
    }

    @Test
    fun `score 699 yields ENSIGN`() {
        assertEquals(Rank.ENSIGN, Rank.fromScore(699))
    }

    @Test
    fun `score 700 yields LIEUTENANT`() {
        assertEquals(Rank.LIEUTENANT, Rank.fromScore(700))
    }

    @Test
    fun `score 1099 yields LIEUTENANT`() {
        assertEquals(Rank.LIEUTENANT, Rank.fromScore(1099))
    }

    @Test
    fun `score 1100 yields CAPTAIN`() {
        assertEquals(Rank.CAPTAIN, Rank.fromScore(1100))
    }

    @Test
    fun `score 2599 yields VICE_ADMIRAL`() {
        assertEquals(Rank.VICE_ADMIRAL, Rank.fromScore(2599))
    }

    @Test
    fun `score 2600 yields ADMIRAL`() {
        assertEquals(Rank.ADMIRAL, Rank.fromScore(2600))
    }

    @Test
    fun `score 3500 yields FLEET_ADMIRAL`() {
        assertEquals(Rank.FLEET_ADMIRAL, Rank.fromScore(3500))
    }

    @Test
    fun `score 9999 yields FLEET_ADMIRAL`() {
        assertEquals(Rank.FLEET_ADMIRAL, Rank.fromScore(9999))
    }
}
