package com.cocode.battleship.presentation.medals

import com.cocode.battleship.domain.scoring.Badge

interface MedalsStorage {
    /** Returns a map of badge to total times earned (absent = 0). */
    fun load(): Map<Badge, Int>
    /** Increments the count for each badge in the list. */
    fun increment(badges: List<Badge>)
}
