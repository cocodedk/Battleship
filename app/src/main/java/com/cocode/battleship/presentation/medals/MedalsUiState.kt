package com.cocode.battleship.presentation.medals

import com.cocode.battleship.domain.scoring.Badge

data class MedalItem(val badge: Badge, val count: Int) {
    val isEarned: Boolean get() = count > 0
}

data class MedalsUiState(
    val items: List<MedalItem>,
    val selectedItem: MedalItem? = null
) {
    val earnedCount: Int get() = items.count { it.isEarned }
    val totalCount: Int get() = items.size
}
