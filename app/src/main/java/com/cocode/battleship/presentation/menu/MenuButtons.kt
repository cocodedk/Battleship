package com.cocode.battleship.presentation.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.ui.theme.SonarCyan

@Composable
internal fun MenuButtons(
    entry: MenuEntryState,
    onViewStats: () -> Unit,
    onViewMedals: () -> Unit,
    onViewBadges: () -> Unit,
) {
    MenuSecondaryButton(stringResource(R.string.menu_view_stats), onViewStats, entry, borderAlpha = 0.45f, contentAlpha = 1f)
    Spacer(Modifier.height(8.dp))
    MenuSecondaryButton(stringResource(R.string.menu_view_medals), onViewMedals, entry)
    Spacer(Modifier.height(8.dp))
    MenuSecondaryButton(stringResource(R.string.menu_badges), onViewBadges, entry)
}

@Composable
private fun MenuSecondaryButton(
    text: String,
    onClick: () -> Unit,
    entry: MenuEntryState,
    borderAlpha: Float = 0.35f,
    contentAlpha: Float = 0.8f,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .alpha(entry.secondaryButtonAlpha)
            .offset(x = entry.secondaryButtonOffsetX),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, SonarCyan.copy(alpha = borderAlpha)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = SonarCyan.copy(alpha = contentAlpha)),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
        )
    }
}
