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

@Composable
fun MenuLogo(
    pulseScale: Float,      // 0.2f..2.0f — sonar ring scale
    pulseAlpha: Float,      // 0.5f..0f   — sonar ring alpha
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

        // --- Sonar pulse ring (animated) ---
        drawCircle(
            color = SonarCyan.copy(alpha = pulseAlpha * 0.35f),
            radius = outerR * pulseScale.coerceAtLeast(0.1f),
            center = center,
            style = stroke15
        )

        // --- Static rings ---
        drawCircle(SonarCyan.copy(alpha = 0.85f), outerR, center, style = stroke15)
        drawCircle(SonarCyan.copy(alpha = 0.50f), midR,   center, style = stroke10)
        drawCircle(SonarCyan.copy(alpha = 0.35f), innerR, center, style = stroke08)

        // --- Crosshair lines (stop at inner ring, gap in center) ---
        val gap = innerR + 3.dp.toPx()
        val lineColor = SonarCyan.copy(alpha = 0.70f)
        // horizontal
        drawLine(lineColor, Offset(cx - outerR, cy), Offset(cx - gap, cy), stroke15.width, StrokeCap.Round)
        drawLine(lineColor, Offset(cx + gap, cy),   Offset(cx + outerR, cy), stroke15.width, StrokeCap.Round)
        // vertical
        drawLine(lineColor, Offset(cx, cy - outerR), Offset(cx, cy - gap), stroke15.width, StrokeCap.Round)
        drawLine(lineColor, Offset(cx, cy + gap),   Offset(cx, cy + outerR), stroke15.width, StrokeCap.Round)

        // --- Compass tick marks at outer ring ---
        val tickLen = 5.dp.toPx()
        val tickColor = SonarCyan.copy(alpha = 0.9f)
        drawLine(tickColor, Offset(cx, cy - outerR - tickLen), Offset(cx, cy - outerR + tickLen / 2), stroke15.width)
        drawLine(tickColor, Offset(cx, cy + outerR - tickLen / 2), Offset(cx, cy + outerR + tickLen), stroke15.width)
        drawLine(tickColor, Offset(cx - outerR - tickLen, cy), Offset(cx - outerR + tickLen / 2, cy), stroke15.width)
        drawLine(tickColor, Offset(cx + outerR - tickLen / 2, cy), Offset(cx + outerR + tickLen, cy), stroke15.width)

        // --- Ship silhouette (top-down, inside inner ring) ---
        val hw = innerR * 0.52f   // half-width of hull
        val hf = innerR * 0.88f   // half-height of hull body
        val bowLen = innerR * 0.42f  // bow extension
        val sternW = innerR * 0.32f  // stern half-width (narrower)
        val shipPath = Path().apply {
            moveTo(cx, cy - hf - bowLen)          // bow tip
            lineTo(cx + hw, cy - hf + hf * 0.18f) // starboard shoulder
            lineTo(cx + hw, cy + hf * 0.75f)       // starboard side
            lineTo(cx + sternW, cy + hf + bowLen * 0.25f) // stern starboard
            lineTo(cx - sternW, cy + hf + bowLen * 0.25f) // stern port
            lineTo(cx - hw, cy + hf * 0.75f)       // port side
            lineTo(cx - hw, cy - hf + hf * 0.18f)  // port shoulder
            close()
        }
        drawPath(shipPath, SonarCyan.copy(alpha = 0.90f))

        // Bridge/superstructure cutout (dark rect to add depth)
        val bw = hw * 0.65f
        val bridgePath = Path().apply {
            moveTo(cx - bw, cy - hf * 0.30f)
            lineTo(cx + bw, cy - hf * 0.30f)
            lineTo(cx + bw, cy + hf * 0.28f)
            lineTo(cx - bw, cy + hf * 0.28f)
            close()
        }
        drawPath(bridgePath, androidx.compose.ui.graphics.Color(0xFF091628))
    }
}
