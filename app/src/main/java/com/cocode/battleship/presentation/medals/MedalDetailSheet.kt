package com.cocode.battleship.presentation.medals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.cocode.battleship.R
import com.cocode.battleship.domain.scoring.Rarity
import com.cocode.battleship.ui.theme.AmberWarning
import com.cocode.battleship.ui.theme.NavyCard
import com.cocode.battleship.ui.theme.TextPrimary
import com.cocode.battleship.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedalDetailSheet(item: MedalItem, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val rarityColor = rarityColor(item.badge.rarity)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NavyCard,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            MedalCanvas(
                badge = item.badge,
                count = item.count,
                modifier = Modifier.size(120.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = item.badge.displayName.uppercase(Locale.ROOT),
                style = MaterialTheme.typography.titleMedium,
                color = rarityColor,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = rarityLabel(item.badge.rarity),
                style = MaterialTheme.typography.labelSmall,
                color = rarityColor.copy(alpha = 0.6f),
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.medal_detail_how_to_earn),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(item.badge.unlockHintResId()),
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            if (item.isEarned) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.medal_detail_earned_count, item.count),
                    style = MaterialTheme.typography.labelMedium,
                    color = AmberWarning,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun rarityLabel(rarity: Rarity): String = when (rarity) {
    Rarity.COMMON    -> stringResource(R.string.medal_detail_rarity_common)
    Rarity.RARE      -> stringResource(R.string.medal_detail_rarity_rare)
    Rarity.EPIC      -> stringResource(R.string.medal_detail_rarity_epic)
    Rarity.LEGENDARY -> stringResource(R.string.medal_detail_rarity_legendary)
}
