package com.cocode.battleship.presentation.menu

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.cocode.battleship.ui.theme.SonarCyan

private fun revealAlpha(reveal: Float, threshold: Float): Float =
    ((reveal - threshold) / (1f - threshold)).coerceIn(0f, 1f)

@Composable
fun MenuLogo(
    pulseScale: Float,
    pulseAlpha: Float,
    reveal: Float = 1f,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val outerR = size.minDimension / 2f * 0.82f
        val innerR = outerR * 0.34f
        val midR = outerR * 0.65f
        val stroke15 = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        val stroke10 = Stroke(width = 1.dp.toPx())
        val stroke08 = Stroke(width = 0.8.dp.toPx())
        val center = Offset(cx, cy)

        // Sonar pulse ring (animated)
        if (pulseAlpha > 0f) {
            drawCircle(
                color = SonarCyan.copy(alpha = pulseAlpha * 0.35f * revealAlpha(reveal, 0.6f)),
                radius = outerR * pulseScale.coerceAtLeast(0.1f),
                center = center, style = stroke15
            )
        }

        // Inner ring
        val innerAlpha = revealAlpha(reveal, 0.1f)
        if (innerAlpha > 0f)
            drawCircle(SonarCyan.copy(alpha = 0.35f * innerAlpha), innerR, center, style = stroke08)

        // Mid ring
        val midAlpha = revealAlpha(reveal, 0.35f)
        if (midAlpha > 0f)
            drawCircle(SonarCyan.copy(alpha = 0.50f * midAlpha), midR, center, style = stroke10)

        // Outer ring
        val outerAlpha = revealAlpha(reveal, 0.6f)
        if (outerAlpha > 0f)
            drawCircle(SonarCyan.copy(alpha = 0.85f * outerAlpha), outerR, center, style = stroke15)

        // Crosshairs + tick marks
        val crosshairAlpha = revealAlpha(reveal, 0.75f)
        if (crosshairAlpha > 0f) {
            val gap = innerR + 3.dp.toPx()
            val lineColor = SonarCyan.copy(alpha = 0.70f * crosshairAlpha)
            val tickColor = SonarCyan.copy(alpha = 0.9f * crosshairAlpha)
            val tickLen = 5.dp.toPx()
            drawLine(lineColor, Offset(cx - outerR, cy), Offset(cx - gap, cy), stroke15.width, StrokeCap.Round)
            drawLine(lineColor, Offset(cx + gap, cy),   Offset(cx + outerR, cy), stroke15.width, StrokeCap.Round)
            drawLine(lineColor, Offset(cx, cy - outerR), Offset(cx, cy - gap), stroke15.width, StrokeCap.Round)
            drawLine(lineColor, Offset(cx, cy + gap),   Offset(cx, cy + outerR), stroke15.width, StrokeCap.Round)
            drawLine(tickColor, Offset(cx, cy - outerR - tickLen), Offset(cx, cy - outerR + tickLen / 2), stroke15.width)
            drawLine(tickColor, Offset(cx, cy + outerR - tickLen / 2), Offset(cx, cy + outerR + tickLen), stroke15.width)
            drawLine(tickColor, Offset(cx - outerR - tickLen, cy), Offset(cx - outerR + tickLen / 2, cy), stroke15.width)
            drawLine(tickColor, Offset(cx + outerR - tickLen / 2, cy), Offset(cx + outerR + tickLen, cy), stroke15.width)
        }

        // Ship silhouette
        val shipAlpha = revealAlpha(reveal, 0.85f)
        if (shipAlpha > 0f) {
            val hw = innerR * 0.52f; val hf = innerR * 0.88f
            val bowLen = innerR * 0.42f; val sternW = innerR * 0.32f
            val shipPath = Path().apply {
                moveTo(cx, cy - hf - bowLen)
                lineTo(cx + hw, cy - hf + hf * 0.18f)
                lineTo(cx + hw, cy + hf * 0.75f)
                lineTo(cx + sternW, cy + hf + bowLen * 0.25f)
                lineTo(cx - sternW, cy + hf + bowLen * 0.25f)
                lineTo(cx - hw, cy + hf * 0.75f)
                lineTo(cx - hw, cy - hf + hf * 0.18f)
                close()
            }
            drawPath(shipPath, SonarCyan.copy(alpha = 0.90f * shipAlpha))
            val bw = hw * 0.65f
            val bridgePath = Path().apply {
                moveTo(cx - bw, cy - hf * 0.30f); lineTo(cx + bw, cy - hf * 0.30f)
                lineTo(cx + bw, cy + hf * 0.28f); lineTo(cx - bw, cy + hf * 0.28f); close()
            }
            drawPath(bridgePath, androidx.compose.ui.graphics.Color(0xFF091628))
        }
    }
}
