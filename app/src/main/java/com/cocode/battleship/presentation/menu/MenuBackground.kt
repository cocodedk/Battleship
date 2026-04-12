package com.cocode.battleship.presentation.menu

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.cocode.battleship.ui.theme.AmberWarning
import com.cocode.battleship.ui.theme.DeepNavy
import com.cocode.battleship.ui.theme.NavySurface
import com.cocode.battleship.ui.theme.PhosphorGreen
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TorpedoRed
import kotlin.math.cos
import kotlin.math.sin

private val PARTICLE_COLORS = listOf(SonarCyan, PhosphorGreen, AmberWarning, TorpedoRed)
private const val PARTICLE_COUNT = 25

@Composable
fun MenuBackground(animate: Boolean, radarAngle: Float, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")

    val particleTime by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Restart),
        label = "particles"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Gradient background
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(NavySurface, DeepNavy),
                center = center,
                radius = size.maxDimension * 0.7f
            )
        )

        if (!animate) return@Canvas

        // Radar sweep — PhosphorGreen with 3-line trail
        val sweepRadius = size.maxDimension
        val sw = 1.5.dp.toPx()
        listOf(0f to 1.0f, -8f to 0.4f, -18f to 0.1f).forEach { (offset, alpha) ->
            val rad = Math.toRadians((radarAngle + offset).toDouble())
            drawLine(
                color = PhosphorGreen.copy(alpha = (alpha * 0.6f)),
                start = center,
                end = Offset(
                    center.x + sweepRadius * cos(rad).toFloat(),
                    center.y + sweepRadius * sin(rad).toFloat()
                ),
                strokeWidth = sw, cap = StrokeCap.Round
            )
        }

        // Particle field — 25 dots drifting upward
        for (i in 0 until PARTICLE_COUNT) {
            val col = i.toFloat() / PARTICLE_COUNT
            val speed = 0.3f + (i % 5) * 0.1f
            val x = col * size.width
            val y = (size.height - (i * size.height / PARTICLE_COUNT +
                    particleTime * speed * 0.001f * size.height)) % size.height
            drawCircle(
                color = PARTICLE_COLORS[i % 4].copy(alpha = 0.08f + (i % 3) * 0.04f),
                radius = (1f + (i % 3) * 0.5f).dp.toPx(),
                center = Offset(x, (y + size.height) % size.height)
            )
        }

        // HUD corner brackets — cyan top, green bottom
        val arm = 28.dp.toPx(); val margin = 20.dp.toPx(); val capSq = StrokeCap.Square
        val cyan = SonarCyan.copy(alpha = 0.75f); val green = PhosphorGreen.copy(alpha = 0.75f)
        // Top-left (cyan)
        drawLine(cyan, Offset(margin, margin + arm), Offset(margin, margin), sw, capSq)
        drawLine(cyan, Offset(margin, margin), Offset(margin + arm, margin), sw, capSq)
        // Top-right (cyan)
        drawLine(cyan, Offset(size.width - margin, margin + arm), Offset(size.width - margin, margin), sw, capSq)
        drawLine(cyan, Offset(size.width - margin, margin), Offset(size.width - margin - arm, margin), sw, capSq)
        // Bottom-left (green)
        drawLine(green, Offset(margin, size.height - margin - arm), Offset(margin, size.height - margin), sw, capSq)
        drawLine(green, Offset(margin, size.height - margin), Offset(margin + arm, size.height - margin), sw, capSq)
        // Bottom-right (green)
        drawLine(green, Offset(size.width - margin, size.height - margin - arm), Offset(size.width - margin, size.height - margin), sw, capSq)
        drawLine(green, Offset(size.width - margin, size.height - margin), Offset(size.width - margin - arm, size.height - margin), sw, capSq)
    }
}
