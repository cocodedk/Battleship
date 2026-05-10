package com.cocode.battleship.presentation.badges

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.cocode.battleship.domain.scoring.Badge
import com.cocode.battleship.domain.scoring.Rarity
import com.cocode.battleship.presentation.medals.CountBadgeOverlay
import com.cocode.battleship.presentation.medals.drawBadgeSymbol
import com.cocode.battleship.presentation.medals.drawLockSymbol
import com.cocode.battleship.presentation.medals.rarityColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BadgeCanvas(badge: Badge, count: Int, modifier: Modifier = Modifier) {
    val isEarned = count > 0
    val color = rarityColor(badge.rarity)
    Box(modifier = modifier) {
        Spacer(
            Modifier.fillMaxSize().drawWithCache {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val outerR = size.minDimension * 0.46f
                val innerR = size.minDimension * 0.20f
                val strokeOuter = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                val strokeInner = Stroke(1.dp.toPx())
                val outerPath: Path? = when (badge.rarity) {
                    Rarity.RARE      -> shieldPath(size.width, size.height)
                    Rarity.EPIC      -> diamondPath(cx, cy, outerR)
                    Rarity.LEGENDARY -> starPath(cx, cy, outerR, innerR)
                    else             -> null
                }
                val innerPath: Path? = when (badge.rarity) {
                    Rarity.RARE -> shieldPath(size.width, size.height, scale = 0.82f)
                    Rarity.EPIC -> diamondPath(cx, cy, outerR * 0.82f)
                    else        -> null
                }
                onDrawBehind {
                    val drawAlpha = if (isEarned) 1f else 0.28f
                    val c = color.copy(alpha = drawAlpha)
                    val bg = Color(0xFF071828).copy(alpha = drawAlpha)
                    if (outerPath != null) {
                        drawPath(outerPath, bg)
                        drawPath(outerPath, c, style = strokeOuter)
                        if (innerPath != null) {
                            drawPath(innerPath, c.copy(alpha = c.alpha * 0.45f), style = strokeInner)
                        }
                    } else {
                        drawCircle(bg, outerR, Offset(cx, cy))
                        drawCircle(c, outerR, Offset(cx, cy), style = strokeOuter)
                        drawCircle(c.copy(alpha = c.alpha * 0.45f), outerR * 0.82f, Offset(cx, cy), style = strokeInner)
                    }
                    if (isEarned) drawBadgeSymbol(badge, cx, cy, outerR * 0.42f, c)
                    else drawLockSymbol(cx, cy, outerR * 0.42f, c)
                }
            }
        )
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

private fun shieldPath(w: Float, h: Float, scale: Float = 1f): Path {
    val sw = w * scale
    val sh = h * scale
    val dx = (w - sw) / 2f
    val dy = (h - sh) / 2f
    return Path().apply {
        moveTo(dx + sw * 0.50f, dy + sh * 0.03f)
        lineTo(dx + sw * 0.97f, dy + sh * 0.22f)
        lineTo(dx + sw * 0.97f, dy + sh * 0.56f)
        cubicTo(dx + sw * 0.97f, dy + sh * 0.84f,
                dx + sw * 0.50f, dy + sh * 0.97f,
                dx + sw * 0.50f, dy + sh * 0.97f)
        cubicTo(dx + sw * 0.03f, dy + sh * 0.84f,
                dx + sw * 0.03f, dy + sh * 0.56f,
                dx + sw * 0.03f, dy + sh * 0.56f)
        lineTo(dx + sw * 0.03f, dy + sh * 0.22f)
        close()
    }
}

private fun diamondPath(cx: Float, cy: Float, r: Float): Path = Path().apply {
    moveTo(cx,     cy - r)
    lineTo(cx + r, cy)
    lineTo(cx,     cy + r)
    lineTo(cx - r, cy)
    close()
}

private fun starPath(cx: Float, cy: Float, outerR: Float, innerR: Float): Path {
    val path = Path()
    for (i in 0 until 10) {
        val angle = (i * PI / 5.0 - PI / 2.0).toFloat()
        val r = if (i % 2 == 0) outerR else innerR
        val x = cx + r * cos(angle)
        val y = cy + r * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}
