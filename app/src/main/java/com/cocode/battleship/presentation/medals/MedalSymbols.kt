package com.cocode.battleship.presentation.medals

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.cocode.battleship.domain.scoring.Badge

internal fun DrawScope.drawBadgeSymbol(badge: Badge, cx: Float, cy: Float, r: Float, color: Color) {
    val stroke = Stroke(1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    when (badge) {
        Badge.FIRST_BLOOD           -> drawCrosshair(cx, cy, r, color, stroke)
        Badge.SHARPSHOOTER          -> drawArrow(cx, cy, r, color, stroke)
        Badge.DEAD_EYE              -> drawDoubleTarget(cx, cy, r, color, stroke)
        Badge.HOT_STREAK            -> drawLightning(cx, cy, r, color)
        Badge.UNSTOPPABLE           -> drawDoubleLightning(cx, cy, r, color)
        Badge.FLAWLESS_VICTORY      -> drawCrown(cx, cy, r, color, stroke)
        Badge.PERFECT_GUNNER        -> drawDiamond(cx, cy, r, color, stroke)
        Badge.LEVIATHAN_SLAYER      -> drawWaveArc(cx, cy, r, color, stroke)
        Badge.SILENT_SERVICE        -> drawSubmarine(cx, cy, r, color, stroke)
        Badge.LAST_STAND            -> drawShield(cx, cy, r, color, stroke)
        Badge.DESTROYER_LIVES       -> drawShipSilhouette(cx, cy, r, color, stroke)
        Badge.SWIM_FOR_IT           -> drawSwimWaves(cx, cy, r, color, stroke)
        Badge.FOG_OF_WAR            -> drawScatterDots(cx, cy, r, color)
        Badge.DEPTH_CHARGE_DIPLOMAT -> drawBomb(cx, cy, r, color, stroke)
        Badge.ON_FIRE               -> drawFlames(cx, cy, r, color, stroke)
    }
}

private fun DrawScope.drawCrosshair(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    val gap = r * 0.36f
    drawCircle(color, r * 0.3f, Offset(cx, cy), style = stroke)
    drawLine(color, Offset(cx, cy - r), Offset(cx, cy - gap), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx, cy + gap), Offset(cx, cy + r), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx - r, cy), Offset(cx - gap, cy), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx + gap, cy), Offset(cx + r, cy), stroke.width, StrokeCap.Round)
    drawCircle(color, r * 0.08f, Offset(cx, cy))
}

private fun DrawScope.drawArrow(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawLine(color, Offset(cx - r, cy), Offset(cx + r * 0.55f, cy), stroke.width, StrokeCap.Round)
    val head = Path().apply {
        moveTo(cx + r, cy); lineTo(cx + r * 0.5f, cy - r * 0.42f); lineTo(cx + r * 0.5f, cy + r * 0.42f); close()
    }
    drawPath(head, color)
    drawLine(color, Offset(cx - r, cy), Offset(cx - r * 0.58f, cy - r * 0.32f), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx - r, cy), Offset(cx - r * 0.58f, cy + r * 0.32f), stroke.width, StrokeCap.Round)
}

private fun DrawScope.drawDoubleTarget(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawCircle(color, r * 0.52f, Offset(cx, cy), style = stroke)
    drawCircle(color, r * 0.22f, Offset(cx, cy), style = stroke)
    drawCircle(color, r * 0.07f, Offset(cx, cy))
}

private fun DrawScope.drawLightning(cx: Float, cy: Float, r: Float, color: Color) {
    val bolt = Path().apply {
        moveTo(cx + r * 0.2f, cy - r)
        lineTo(cx - r * 0.15f, cy - r * 0.05f)
        lineTo(cx + r * 0.18f, cy - r * 0.05f)
        lineTo(cx - r * 0.2f, cy + r)
        lineTo(cx + r * 0.1f, cy + r * 0.05f)
        lineTo(cx - r * 0.12f, cy + r * 0.05f)
        close()
    }
    drawPath(bolt, color)
}

private fun DrawScope.drawDoubleLightning(cx: Float, cy: Float, r: Float, color: Color) {
    val bolt1 = Path().apply {
        moveTo(cx - r * 0.1f, cy - r)
        lineTo(cx - r * 0.4f, cy - r * 0.05f)
        lineTo(cx - r * 0.12f, cy - r * 0.05f)
        lineTo(cx - r * 0.42f, cy + r)
        lineTo(cx - r * 0.2f, cy + r * 0.05f)
        lineTo(cx - r * 0.4f, cy + r * 0.05f)
        close()
    }
    val bolt2 = Path().apply {
        moveTo(cx + r * 0.32f, cy - r)
        lineTo(cx + r * 0.02f, cy - r * 0.05f)
        lineTo(cx + r * 0.3f, cy - r * 0.05f)
        lineTo(cx + r * 0.0f, cy + r)
        lineTo(cx + r * 0.22f, cy + r * 0.05f)
        lineTo(cx + r * 0.02f, cy + r * 0.05f)
        close()
    }
    drawPath(bolt1, color.copy(alpha = color.alpha * 0.65f))
    drawPath(bolt2, color)
}

private fun DrawScope.drawCrown(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    val base = cy + r * 0.55f
    val crownPath = Path().apply {
        moveTo(cx - r, base)
        lineTo(cx - r, cy - r * 0.15f)
        lineTo(cx - r * 0.32f, cy + r * 0.15f)
        lineTo(cx, cy - r)
        lineTo(cx + r * 0.32f, cy + r * 0.15f)
        lineTo(cx + r, cy - r * 0.15f)
        lineTo(cx + r, base)
    }
    drawPath(crownPath, color, style = stroke)
    drawLine(color, Offset(cx - r, base), Offset(cx + r, base), stroke.width, StrokeCap.Round)
    drawCircle(color, r * 0.1f, Offset(cx, cy - r))
    drawCircle(color, r * 0.08f, Offset(cx - r, cy - r * 0.15f))
    drawCircle(color, r * 0.08f, Offset(cx + r, cy - r * 0.15f))
}

private fun DrawScope.drawDiamond(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    val gem = Path().apply {
        moveTo(cx, cy - r); lineTo(cx + r, cy); lineTo(cx, cy + r); lineTo(cx - r, cy); close()
    }
    val topFace = Path().apply {
        moveTo(cx, cy - r); lineTo(cx + r, cy); lineTo(cx, cy - r * 0.1f); lineTo(cx - r, cy); close()
    }
    drawPath(topFace, color.copy(alpha = color.alpha * 0.22f))
    drawPath(gem, color, style = stroke)
    drawLine(color.copy(alpha = color.alpha * 0.75f), Offset(cx - r, cy), Offset(cx + r, cy), 0.8.dp.toPx())
}

private fun DrawScope.drawWaveArc(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawLine(color, Offset(cx - r, cy + r * 0.25f), Offset(cx + r, cy + r * 0.25f), stroke.width, StrokeCap.Round)
    val arc = Path().apply {
        moveTo(cx - r * 0.72f, cy + r * 0.25f)
        cubicTo(cx - r * 0.3f, cy - r * 0.85f, cx + r * 0.3f, cy - r * 0.85f, cx + r * 0.72f, cy + r * 0.25f)
    }
    drawPath(arc, color, style = stroke)
    drawCircle(color, r * 0.07f, Offset(cx - r * 0.78f, cy + r * 0.05f))
    drawCircle(color, r * 0.07f, Offset(cx + r * 0.78f, cy + r * 0.05f))
}

private fun DrawScope.drawSubmarine(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawOval(color, topLeft = Offset(cx - r, cy - r * 0.28f), size = Size(r * 2f, r * 0.56f), style = stroke)
    drawRect(color, topLeft = Offset(cx - r * 0.22f, cy - r * 0.78f), size = Size(r * 0.44f, r * 0.52f), style = stroke)
    drawLine(color, Offset(cx + r * 0.12f, cy - r * 0.78f), Offset(cx + r * 0.12f, cy - r * 1.05f), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx + r * 0.12f, cy - r * 1.05f), Offset(cx + r * 0.38f, cy - r * 1.05f), stroke.width, StrokeCap.Round)
}

private fun DrawScope.drawShield(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    val shield = Path().apply {
        moveTo(cx - r, cy - r * 0.65f)
        lineTo(cx + r, cy - r * 0.65f)
        lineTo(cx + r, cy + r * 0.15f)
        cubicTo(cx + r, cy + r * 0.85f, cx, cy + r, cx, cy + r)
        cubicTo(cx, cy + r, cx - r, cy + r * 0.85f, cx - r, cy + r * 0.15f)
        close()
    }
    drawPath(shield, color, style = stroke)
    drawLine(color, Offset(cx, cy - r * 0.65f), Offset(cx, cy + r * 0.65f), stroke.width * 0.75f, StrokeCap.Round)
}

private fun DrawScope.drawShipSilhouette(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawRect(color, topLeft = Offset(cx - r, cy - r * 0.22f), size = Size(r * 2f, r * 0.44f), style = stroke)
    val bow = Path().apply {
        moveTo(cx + r, cy - r * 0.22f); lineTo(cx + r, cy + r * 0.22f); lineTo(cx + r * 1.38f, cy); close()
    }
    drawPath(bow, color)
    drawRect(color, topLeft = Offset(cx - r * 0.48f, cy - r * 0.68f), size = Size(r * 0.96f, r * 0.46f), style = stroke)
    drawLine(color, Offset(cx, cy - r * 0.68f), Offset(cx, cy - r), stroke.width * 0.75f, StrokeCap.Round)
}

private fun DrawScope.drawSwimWaves(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    for (dy in listOf(-r * 0.28f, r * 0.28f)) {
        val wave = Path().apply {
            moveTo(cx - r, cy + dy)
            cubicTo(cx - r * 0.5f, cy + dy - r * 0.32f, cx, cy + dy + r * 0.32f, cx + r * 0.5f, cy + dy - r * 0.32f)
            cubicTo(cx + r * 0.5f, cy + dy - r * 0.32f, cx + r * 0.75f, cy + dy, cx + r, cy + dy)
        }
        drawPath(wave, color, style = stroke)
    }
}

private fun DrawScope.drawScatterDots(cx: Float, cy: Float, r: Float, color: Color) {
    val positions = listOf(
        Offset(cx, cy) to r * 0.13f,
        Offset(cx - r * 0.5f, cy - r * 0.5f) to r * 0.09f,
        Offset(cx + r * 0.52f, cy - r * 0.42f) to r * 0.11f,
        Offset(cx - r * 0.42f, cy + r * 0.52f) to r * 0.09f,
        Offset(cx + r * 0.55f, cy + r * 0.46f) to r * 0.08f,
    )
    positions.forEach { (pos, rad) -> drawCircle(color, rad, pos) }
}

private fun DrawScope.drawBomb(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawCircle(color, r * 0.52f, Offset(cx, cy + r * 0.22f), style = stroke)
    val fuse = Path().apply {
        moveTo(cx, cy - r * 0.3f)
        cubicTo(cx + r * 0.28f, cy - r * 0.7f, cx + r * 0.58f, cy - r * 0.52f, cx + r * 0.5f, cy - r)
    }
    drawPath(fuse, color, style = stroke)
    drawCircle(color, r * 0.09f, Offset(cx + r * 0.5f, cy - r))
}

private fun DrawScope.drawFlames(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    val flame1 = Path().apply {
        moveTo(cx + r * 0.28f, cy + r)
        cubicTo(cx + r * 0.82f, cy + r * 0.38f, cx + r, cy - r * 0.22f, cx + r * 0.38f, cy - r)
        cubicTo(cx + r * 0.28f, cy - r * 0.32f, cx + r * 0.14f, cy + r * 0.32f, cx + r * 0.28f, cy + r)
    }
    val flame2 = Path().apply {
        moveTo(cx - r * 0.28f, cy + r)
        cubicTo(cx - r * 0.82f, cy + r * 0.38f, cx - r, cy - r * 0.22f, cx - r * 0.38f, cy - r)
        cubicTo(cx - r * 0.28f, cy - r * 0.32f, cx - r * 0.14f, cy + r * 0.32f, cx - r * 0.28f, cy + r)
    }
    drawPath(flame2, color.copy(alpha = color.alpha * 0.55f), style = stroke)
    drawPath(flame1, color, style = stroke)
    drawPath(flame1, color.copy(alpha = color.alpha * 0.14f))
}
