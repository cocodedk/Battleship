package com.cocode.battleship.presentation.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.ui.theme.AmberWarning
import com.cocode.battleship.ui.theme.DeepNavy
import com.cocode.battleship.ui.theme.SonarCyan

@Composable
internal fun MenuButtons(
    entry: MenuEntryState,
    onViewStats: () -> Unit,
    onViewMedals: () -> Unit,
    onViewBadges: () -> Unit,
    onViewAbout: () -> Unit,
) {
    MenuSecondaryButton(stringResource(R.string.menu_view_stats), onViewStats, entry, borderAlpha = 0.45f, contentAlpha = 1f)
    Spacer(Modifier.height(8.dp))
    MenuSecondaryButton(stringResource(R.string.menu_view_medals), onViewMedals, entry)
    Spacer(Modifier.height(8.dp))
    MenuSecondaryButton(stringResource(R.string.menu_badges), onViewBadges, entry)
    Spacer(Modifier.height(8.dp))
    MenuSecondaryButton(stringResource(R.string.menu_about), onViewAbout, entry)
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

/** The main START MISSION button, ringed by the blinking amber oval. */
@Composable
internal fun MenuStartButton(entry: MenuEntryState, blinkAlpha: Float, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .alpha(entry.primaryButtonAlpha)
            .offset(x = entry.primaryButtonOffsetX)
            .drawWithContent {
                drawContent()
                drawOval(
                    color = AmberWarning.copy(alpha = blinkAlpha * 0.35f),
                    style = Stroke(width = 10.dp.toPx())
                )
            }
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SonarCyan, contentColor = DeepNavy)
        ) {
            Text(
                text = stringResource(R.string.menu_start_game),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp,
            )
        }
    }
}
