package com.cocode.battleship.presentation.badges

import com.cocode.battleship.domain.scoring.Badge

data class BadgeItem(val badge: Badge, val count: Int) {
    val isEarned: Boolean get() = count > 0
}

data class BadgesUiState(
    val items: List<BadgeItem>,
    val selectedItem: BadgeItem? = null,
) {
    val earnedCount: Int get() = items.count { it.isEarned }
    val totalCount: Int get() = items.size
}
