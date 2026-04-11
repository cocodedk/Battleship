package com.cocode.battleship.presentation.menu

import android.provider.Settings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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

private const val SONAR_GLYPH = "◈"

@Composable
fun MenuScreen(onStartGame: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "sonar")
    val sonarPulseSpec = infiniteRepeatable<Float>(tween(2800, easing = LinearEasing), RepeatMode.Restart)
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 2.0f,
        animationSpec = sonarPulseSpec,
        label = "scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 0f,
        animationSpec = sonarPulseSpec,
        label = "alpha"
    )

    val context = LocalContext.current
    val prefersReducedMotion = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    val effectivePulseAlpha = if (prefersReducedMotion) 0f else pulseAlpha

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepNavy, NavySurface, DeepNavy))),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(320.dp)
                .scale(pulseScale)
                .border(1.5.dp, SonarCyan.copy(alpha = effectivePulseAlpha * 0.4f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .scale(pulseScale * 0.65f)
                .border(1.dp, SonarCyan.copy(alpha = effectivePulseAlpha * 0.7f), CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text(text = SONAR_GLYPH, fontSize = 28.sp, color = SonarCyan)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.menu_title),
                style = MaterialTheme.typography.displayLarge,
                color = SonarCyan,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.menu_system_label),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                letterSpacing = 3.sp,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.menu_tagline).uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp,
            )

            Spacer(modifier = Modifier.height(52.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.menu_game_mode),
                style = MaterialTheme.typography.labelSmall,
                color = TextDim,
                letterSpacing = 1.sp,
            )
        }
    }
}
