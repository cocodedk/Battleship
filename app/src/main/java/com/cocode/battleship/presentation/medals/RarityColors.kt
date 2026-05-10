package com.cocode.battleship.presentation.medals

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.cocode.battleship.R
import com.cocode.battleship.domain.scoring.Rarity

internal val RarityCommon    = Color(0xFFB87333)
internal val RarityRare      = Color(0xFF7EB8D4)
internal val RarityEpic      = Color(0xFFFFD700)
internal val RarityLegendary = Color(0xFF00D4FF)

internal fun rarityColor(rarity: Rarity): Color = when (rarity) {
    Rarity.COMMON    -> RarityCommon
    Rarity.RARE      -> RarityRare
    Rarity.EPIC      -> RarityEpic
    Rarity.LEGENDARY -> RarityLegendary
}

@Composable
internal fun rarityLabel(rarity: Rarity): String = when (rarity) {
    Rarity.COMMON    -> stringResource(R.string.medal_detail_rarity_common)
    Rarity.RARE      -> stringResource(R.string.medal_detail_rarity_rare)
    Rarity.EPIC      -> stringResource(R.string.medal_detail_rarity_epic)
    Rarity.LEGENDARY -> stringResource(R.string.medal_detail_rarity_legendary)
}
