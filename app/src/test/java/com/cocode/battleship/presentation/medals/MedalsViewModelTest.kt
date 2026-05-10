package com.cocode.battleship.presentation.medals

import com.cocode.battleship.domain.scoring.Badge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MedalsViewModelTest {

    private fun makeViewModel(): MedalsViewModel {
        val storage = FakeMedalsStorageForVm()
        return MedalsViewModel(storage)
    }

    @Test fun `selectedItem is null on init`() {
        val vm = makeViewModel()
        assertNull(vm.state.value.selectedItem)
    }

    @Test fun `selectItem sets selectedItem in state`() {
        val vm = makeViewModel()
        val item = vm.state.value.items.first { it.badge == Badge.FIRST_BLOOD }
        vm.selectItem(item)
        assertEquals(item, vm.state.value.selectedItem)
    }

    @Test fun `selectItem with null clears selectedItem`() {
        val vm = makeViewModel()
        val item = vm.state.value.items.first { it.badge == Badge.FIRST_BLOOD }
        vm.selectItem(item)
        vm.selectItem(null)
        assertNull(vm.state.value.selectedItem)
    }
}

private class FakeMedalsStorageForVm : MedalsStorage {
    override fun load(): Map<Badge, Int> = emptyMap()
    override fun increment(badges: List<Badge>) = Unit
}
