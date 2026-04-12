package com.cocode.battleship.presentation.medals

import com.cocode.battleship.domain.scoring.Badge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class MedalsStorageTest {

    private lateinit var storage: FakeMedalsStorage

    @Before
    fun setUp() {
        storage = FakeMedalsStorage()
    }

    @Test
    fun `load returns empty map when nothing incremented`() {
        assertEquals(emptyMap<Badge, Int>(), storage.load())
    }

    @Test
    fun `increment once sets count to 1`() {
        storage.increment(listOf(Badge.FIRST_BLOOD))
        assertEquals(1, storage.load()[Badge.FIRST_BLOOD])
    }

    @Test
    fun `increment twice accumulates to 2`() {
        storage.increment(listOf(Badge.FIRST_BLOOD))
        storage.increment(listOf(Badge.FIRST_BLOOD))
        assertEquals(2, storage.load()[Badge.FIRST_BLOOD])
    }

    @Test
    fun `increment multiple badges in one call counts each`() {
        storage.increment(listOf(Badge.FIRST_BLOOD, Badge.FIRST_BLOOD, Badge.ON_FIRE))
        assertEquals(2, storage.load()[Badge.FIRST_BLOOD])
        assertEquals(1, storage.load()[Badge.ON_FIRE])
    }

    @Test
    fun `unearned badge is absent from load result`() {
        storage.increment(listOf(Badge.FIRST_BLOOD))
        assertNull(storage.load()[Badge.SHARPSHOOTER])
    }

    @Test
    fun `empty increment list has no effect`() {
        storage.increment(emptyList())
        assertEquals(emptyMap<Badge, Int>(), storage.load())
    }
}

private class FakeMedalsStorage : MedalsStorage {
    private val counts: MutableMap<Badge, Int> = mutableMapOf()
    override fun load(): Map<Badge, Int> = counts.toMap()
    override fun increment(badges: List<Badge>) {
        badges.forEach { counts[it] = (counts[it] ?: 0) + 1 }
    }
}
