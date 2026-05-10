package com.cocode.battleship.presentation.badges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cocode.battleship.domain.scoring.Badge
import com.cocode.battleship.presentation.medals.MedalsStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BadgesViewModel(storage: MedalsStorage) : ViewModel() {

    private val _state = MutableStateFlow(buildState(storage.load()))
    val state: StateFlow<BadgesUiState> = _state.asStateFlow()

    fun selectItem(item: BadgeItem?) {
        _state.update { it.copy(selectedItem = item) }
    }

    companion object {
        fun factory(storage: MedalsStorage): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(BadgesViewModel::class.java)) {
                        "Unknown ViewModel class: ${modelClass.name}"
                    }
                    return BadgesViewModel(storage) as T
                }
            }
    }
}

private fun buildState(counts: Map<Badge, Int>): BadgesUiState {
    val items = Badge.entries.map { badge -> BadgeItem(badge = badge, count = counts[badge] ?: 0) }
    return BadgesUiState(items = items)
}
