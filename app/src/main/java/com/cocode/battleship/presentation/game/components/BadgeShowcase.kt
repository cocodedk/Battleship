package com.cocode.battleship.presentation.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.domain.scoring.Badge
import com.cocode.battleship.domain.scoring.Rarity
import com.cocode.battleship.ui.theme.AmberWarning
import com.cocode.battleship.ui.theme.BronzeGold
import com.cocode.battleship.ui.theme.NavyCard
import com.cocode.battleship.ui.theme.NavyBorder
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TextDim
import com.cocode.battleship.ui.theme.TextSecondary

@Composable
fun BadgeShowcase(badges: List<Badge>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyCard, RoundedCornerShape(4.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = stringResource(R.string.game_over_badges_title),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (badges.isEmpty()) {
            Text(
                text = stringResource(R.string.game_over_badges_none),
                style = MaterialTheme.typography.bodySmall,
                color = TextDim,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                badges.forEach { badge ->
                    BadgeChip(badge = badge)
                }
            }
        }
    }
}

@Composable
private fun BadgeChip(badge: Badge) {
    val borderColor = rarityColor(badge.rarity)
    Column(
        modifier = Modifier
            .size(72.dp)
            .background(borderColor.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
            .border(1.dp, borderColor.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = badge.icon,
            fontSize = 26.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = badge.displayName,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
            color = borderColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 9.sp,
        )
    }
}

private fun rarityColor(rarity: Rarity): Color = when (rarity) {
    Rarity.COMMON -> TextSecondary
    Rarity.RARE -> SonarCyan
    Rarity.EPIC -> AmberWarning
    Rarity.LEGENDARY -> BronzeGold
}
