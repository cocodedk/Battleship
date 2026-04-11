package com.cocode.battleship.presentation.menu

import android.provider.Settings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.ui.theme.DeepNavy
import com.cocode.battleship.ui.theme.NavySurface
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TextDim
import com.cocode.battleship.ui.theme.TextSecondary

@Composable
fun MenuScreen(onStartGame: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "sonar")
    val sonarPulseSpec = infiniteRepeatable<Float>(tween(2800, easing = LinearEasing), RepeatMode.Restart)
    val pulseScale by infiniteTransition.animateFloat(0.2f, 2.0f, sonarPulseSpec, label = "scale")
    val pulseAlpha by infiniteTransition.animateFloat(0.5f, 0f, sonarPulseSpec, label = "alpha")

    val context = LocalContext.current
    val prefersReducedMotion = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    val effectivePulseAlpha = if (prefersReducedMotion) 0f else pulseAlpha
    val effectivePulseScale = if (prefersReducedMotion) 0f else pulseScale

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(
                colors = listOf(NavySurface, DeepNavy),
                radius = 900f
            )),
        contentAlignment = Alignment.Center
    ) {
        // HUD corner brackets overlay
        HudCorners()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Naval targeting reticle logo
            MenuLogo(
                pulseScale = effectivePulseScale,
                pulseAlpha = effectivePulseAlpha,
                modifier = Modifier.size(180.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.menu_title),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 36.sp, letterSpacing = 2.sp),
                color = SonarCyan,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.menu_system_label),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                letterSpacing = 3.sp,
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = stringResource(R.string.menu_tagline).uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary.copy(alpha = 0.55f),
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp,
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = onStartGame,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SonarCyan,
                    contentColor = DeepNavy,
                )
            ) {
                Text(
                    text = stringResource(R.string.menu_start_game),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp,
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.menu_game_mode),
                style = MaterialTheme.typography.labelSmall,
                color = TextDim,
                letterSpacing = 1.sp,
            )
        }
    }
}

@Composable
private fun HudCorners(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val arm = 28.dp.toPx()
        val margin = 20.dp.toPx()
        val color = SonarCyan.copy(alpha = 0.4f)
        val sw = 2.dp.toPx()
        val cap = StrokeCap.Square

        // Top-left
        drawLine(color, Offset(margin, margin + arm), Offset(margin, margin), sw, cap)
        drawLine(color, Offset(margin, margin), Offset(margin + arm, margin), sw, cap)
        // Top-right
        drawLine(color, Offset(size.width - margin, margin + arm), Offset(size.width - margin, margin), sw, cap)
        drawLine(color, Offset(size.width - margin, margin), Offset(size.width - margin - arm, margin), sw, cap)
        // Bottom-left
        drawLine(color, Offset(margin, size.height - margin - arm), Offset(margin, size.height - margin), sw, cap)
        drawLine(color, Offset(margin, size.height - margin), Offset(margin + arm, size.height - margin), sw, cap)
        // Bottom-right
        drawLine(color, Offset(size.width - margin, size.height - margin - arm), Offset(size.width - margin, size.height - margin), sw, cap)
        drawLine(color, Offset(size.width - margin, size.height - margin), Offset(size.width - margin - arm, size.height - margin), sw, cap)
    }
}
