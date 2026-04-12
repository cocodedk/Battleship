package com.cocode.battleship.presentation.medals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cocode.battleship.domain.scoring.Badge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MedalsViewModel(storage: MedalsStorage) : ViewModel() {

    private val _state = MutableStateFlow(buildState(storage.load()))
    val state: StateFlow<MedalsUiState> = _state.asStateFlow()

    companion object {
        fun factory(storage: MedalsStorage): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    MedalsViewModel(storage) as T
            }
    }
}

private fun buildState(counts: Map<Badge, Int>): MedalsUiState {
    val items = Badge.entries.map { badge ->
        MedalItem(badge = badge, count = counts[badge] ?: 0)
    }
    return MedalsUiState(items = items, earnedCount = items.count { it.isEarned })
}
