package com.cocode.battleship.presentation.medals

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.cocode.battleship.domain.scoring.Badge
import com.cocode.battleship.domain.scoring.Rarity
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MedalCanvas(badge: Badge, count: Int, modifier: Modifier = Modifier) {
    val isEarned = count > 0
    val color = rarityColor(badge.rarity)
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val outerR = size.minDimension * 0.46f
            val drawAlpha = if (isEarned) 1f else 0.28f
            val c = color.copy(alpha = drawAlpha)
            val strokeOuter = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

            drawPath(hexPath(cx, cy, outerR), Color(0xFF071828).copy(alpha = drawAlpha))
            drawPath(hexPath(cx, cy, outerR), c, style = strokeOuter)
            drawPath(hexPath(cx, cy, outerR * 0.82f), c.copy(alpha = c.alpha * 0.45f), style = Stroke(1.dp.toPx()))
            drawVertexAccents(cx, cy, outerR, badge.rarity, c)
            if (badge.rarity == Rarity.LEGENDARY && isEarned) {
                drawCircle(c.copy(alpha = 0.12f), outerR * 1.15f, Offset(cx, cy))
                drawCircle(c.copy(alpha = 0.06f), outerR * 1.3f, Offset(cx, cy))
            }
            if (isEarned) drawBadgeSymbol(badge, cx, cy, outerR * 0.42f, c)
            else drawLockSymbol(cx, cy, outerR * 0.42f, c)
        }
        if (isEarned) {
            CountBadgeOverlay(
                count = count,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 1.dp, end = 1.dp)
            )
        }
    }
}

private fun hexPath(cx: Float, cy: Float, radius: Float): Path {
    val path = Path()
    for (i in 0..5) {
        val angle = Math.toRadians(-90.0 + 60.0 * i).toFloat()
        val x = cx + radius * cos(angle)
        val y = cy + radius * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun DrawScope.drawVertexAccents(cx: Float, cy: Float, r: Float, rarity: Rarity, color: Color) {
    if (rarity == Rarity.COMMON) return
    for (i in 0..5) {
        val angle = Math.toRadians(-90.0 + 60.0 * i).toFloat()
        val vx = cx + r * cos(angle)
        val vy = cy + r * sin(angle)
        when (rarity) {
            Rarity.RARE -> drawCircle(color, 2.5.dp.toPx(), Offset(vx, vy))
            Rarity.EPIC, Rarity.LEGENDARY -> {
                val tl = 4.dp.toPx()
                val ax = cos(angle); val ay = sin(angle)
                drawLine(color, Offset(vx - ax * tl, vy - ay * tl), Offset(vx + ax * tl, vy + ay * tl), 1.5.dp.toPx(), StrokeCap.Round)
            }
            else -> {}
        }
    }
}

