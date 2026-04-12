package com.cocode.battleship.presentation.menu

import android.provider.Settings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.ui.theme.AmberWarning
import com.cocode.battleship.ui.theme.DeepNavy
import com.cocode.battleship.ui.theme.PhosphorGreen
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TextDim
import com.cocode.battleship.ui.theme.TextSecondary

@Composable
fun MenuScreen(onStartGame: () -> Unit, onViewStats: () -> Unit = {}, onViewMedals: () -> Unit = {}) {
    val context = LocalContext.current
    val prefersReducedMotion = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }

    val infiniteTransition = rememberInfiniteTransition(label = "menu")
    val blinkAlpha by infiniteTransition.animateFloat(
        1f, 0.3f, infiniteRepeatable(tween(700, easing = LinearEasing), RepeatMode.Reverse), label = "blink")
    val pulseScale by infiniteTransition.animateFloat(
        0.2f, 2.0f, infiniteRepeatable(tween(2800, easing = LinearEasing), RepeatMode.Restart), label = "scale")
    val pulseAlpha by infiniteTransition.animateFloat(
        0.5f, 0f, infiniteRepeatable(tween(2800, easing = LinearEasing), RepeatMode.Restart), label = "alpha")
    val radarAngle by infiniteTransition.animateFloat(
        0f, 360f, infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart), label = "radar")
    val entry = rememberMenuEntryState(prefersReducedMotion)
    val effectiveBlinkAlpha = if (prefersReducedMotion) 1f else blinkAlpha
    val effectivePulse = if (prefersReducedMotion) 0f else pulseAlpha
    val effectiveScale = if (prefersReducedMotion) 0f else pulseScale
    val subtitleMod = Modifier.alpha(entry.subtitleAlpha).offset(y = entry.subtitleOffsetY)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MenuBackground(animate = !prefersReducedMotion, radarAngle = if (prefersReducedMotion) -1f else radarAngle)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            MenuLogo(
                pulseScale = effectiveScale,
                pulseAlpha = effectivePulse,
                reveal = entry.reticleReveal,
                radarAngle = if (prefersReducedMotion) -1f else radarAngle,
                modifier = Modifier.size(180.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.menu_title),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 48.sp,
                    letterSpacing = 2.sp,
                    shadow = Shadow(SonarCyan.copy(alpha = 0.5f), blurRadius = 24f)
                ),
                color = SonarCyan,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .scale(entry.titleScale)
                    .alpha(entry.titleAlpha)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.menu_system_label),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                letterSpacing = 3.sp,
                modifier = subtitleMod
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = stringResource(R.string.menu_tagline).uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                color = PhosphorGreen.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp,
                modifier = subtitleMod
            )

            Spacer(Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .alpha(entry.primaryButtonAlpha)
                    .offset(x = entry.primaryButtonOffsetX)
                    .drawWithContent {
                        drawContent()
                        drawOval(
                            color = AmberWarning.copy(alpha = effectiveBlinkAlpha * 0.35f),
                            style = Stroke(width = 10.dp.toPx())
                        )
                    }
            ) {
                Button(
                    onClick = onStartGame,
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SonarCyan,
                        contentColor = DeepNavy
                    )
                ) {
                    Text(
                        text = stringResource(R.string.menu_start_game),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 3.sp,
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onViewStats,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .alpha(entry.secondaryButtonAlpha)
                    .offset(x = entry.secondaryButtonOffsetX),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, SonarCyan.copy(alpha = 0.45f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SonarCyan),
            ) {
                Text(
                    text = stringResource(R.string.menu_view_stats),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onViewMedals,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .alpha(entry.secondaryButtonAlpha)
                    .offset(x = entry.secondaryButtonOffsetX),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, SonarCyan.copy(alpha = 0.35f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SonarCyan.copy(alpha = 0.8f)),
            ) {
                Text(
                    text = stringResource(R.string.menu_view_medals),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.menu_game_mode),
                style = MaterialTheme.typography.labelSmall,
                color = TextDim,
                letterSpacing = 1.sp,
            )

            MenuFooter()
        }

        if (entry.staticFlashAlpha > 0f)
            Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = entry.staticFlashAlpha)))
    }
}
