package com.cocode.battleship.presentation.badges

import com.cocode.battleship.domain.scoring.Badge
import com.cocode.battleship.presentation.medals.MedalsStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BadgesViewModelTest {

    private fun vm(counts: Map<Badge, Int> = emptyMap()) =
        BadgesViewModel(FakeMedalsStorage(counts))

    @Test
    fun `initial state contains all 33 badges`() {
        assertEquals(33, vm().state.value.items.size)
    }

    @Test
    fun `badges ordered by Badge enum declaration order`() {
        assertEquals(Badge.entries.toList(), vm().state.value.items.map { it.badge })
    }

    @Test
    fun `unearned badge has count zero`() {
        val item = vm().state.value.items.first { it.badge == Badge.FIRST_BLOOD }
        assertEquals(0, item.count)
    }

    @Test
    fun `unearned badge isEarned is false`() {
        val item = vm().state.value.items.first { it.badge == Badge.FIRST_BLOOD }
        assertFalse(item.isEarned)
    }

    @Test
    fun `earned badge reflects storage count`() {
        val item = vm(mapOf(Badge.FIRST_BLOOD to 5)).state.value.items
            .first { it.badge == Badge.FIRST_BLOOD }
        assertEquals(5, item.count)
    }

    @Test
    fun `earned badge isEarned is true`() {
        val item = vm(mapOf(Badge.FIRST_BLOOD to 5)).state.value.items
            .first { it.badge == Badge.FIRST_BLOOD }
        assertTrue(item.isEarned)
    }

    @Test
    fun `earnedCount reflects non-zero entries only`() {
        assertEquals(2, vm(mapOf(Badge.FIRST_BLOOD to 3, Badge.ON_FIRE to 1)).state.value.earnedCount)
    }

    @Test
    fun `totalCount is always 33`() {
        assertEquals(33, vm(mapOf(Badge.FIRST_BLOOD to 1)).state.value.totalCount)
    }

    @Test
    fun `selectItem sets selectedItem`() {
        val viewModel = vm(mapOf(Badge.ON_FIRE to 2))
        val item = viewModel.state.value.items.first { it.badge == Badge.ON_FIRE }
        viewModel.selectItem(item)
        assertEquals(item, viewModel.state.value.selectedItem)
    }

    @Test
    fun `selectItem null clears selectedItem`() {
        val viewModel = vm(mapOf(Badge.ON_FIRE to 1))
        val item = viewModel.state.value.items.first { it.badge == Badge.ON_FIRE }
        viewModel.selectItem(item)
        viewModel.selectItem(null)
        assertNull(viewModel.state.value.selectedItem)
    }

    @Test
    fun `initial selectedItem is null`() {
        assertNull(vm().state.value.selectedItem)
    }

    private class FakeMedalsStorage(
        private val counts: Map<Badge, Int> = emptyMap()
    ) : MedalsStorage {
        override fun load(): Map<Badge, Int> = counts
        override fun increment(badges: List<Badge>) = Unit
    }
}
