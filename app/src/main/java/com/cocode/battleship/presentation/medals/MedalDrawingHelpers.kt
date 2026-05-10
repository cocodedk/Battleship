package com.cocode.battleship.presentation.medals

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

internal fun DrawScope.drawCrosshair(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    val gap = r * 0.36f
    drawCircle(color, r * 0.3f, Offset(cx, cy), style = stroke)
    drawLine(color, Offset(cx, cy - r), Offset(cx, cy - gap), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx, cy + gap), Offset(cx, cy + r), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx - r, cy), Offset(cx - gap, cy), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx + gap, cy), Offset(cx + r, cy), stroke.width, StrokeCap.Round)
    drawCircle(color, r * 0.08f, Offset(cx, cy))
}

internal fun DrawScope.drawArrow(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawLine(color, Offset(cx - r, cy), Offset(cx + r * 0.55f, cy), stroke.width, StrokeCap.Round)
    val head = Path().apply {
        moveTo(cx + r, cy); lineTo(cx + r * 0.5f, cy - r * 0.42f); lineTo(cx + r * 0.5f, cy + r * 0.42f); close()
    }
    drawPath(head, color)
    drawLine(color, Offset(cx - r, cy), Offset(cx - r * 0.58f, cy - r * 0.32f), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx - r, cy), Offset(cx - r * 0.58f, cy + r * 0.32f), stroke.width, StrokeCap.Round)
}

internal fun DrawScope.drawDoubleTarget(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawCircle(color, r * 0.52f, Offset(cx, cy), style = stroke)
    drawCircle(color, r * 0.22f, Offset(cx, cy), style = stroke)
    drawCircle(color, r * 0.07f, Offset(cx, cy))
}

internal fun DrawScope.drawLightning(cx: Float, cy: Float, r: Float, color: Color) {
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

internal fun DrawScope.drawDoubleLightning(cx: Float, cy: Float, r: Float, color: Color) {
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

internal fun DrawScope.drawCrown(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
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

internal fun DrawScope.drawDiamond(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
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

internal fun DrawScope.drawWaveArc(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawLine(color, Offset(cx - r, cy + r * 0.25f), Offset(cx + r, cy + r * 0.25f), stroke.width, StrokeCap.Round)
    val arc = Path().apply {
        moveTo(cx - r * 0.72f, cy + r * 0.25f)
        cubicTo(cx - r * 0.3f, cy - r * 0.85f, cx + r * 0.3f, cy - r * 0.85f, cx + r * 0.72f, cy + r * 0.25f)
    }
    drawPath(arc, color, style = stroke)
    drawCircle(color, r * 0.07f, Offset(cx - r * 0.78f, cy + r * 0.05f))
    drawCircle(color, r * 0.07f, Offset(cx + r * 0.78f, cy + r * 0.05f))
}

internal fun DrawScope.drawScatterDots(cx: Float, cy: Float, r: Float, color: Color) {
    val positions = listOf(
        Offset(cx, cy) to r * 0.13f,
        Offset(cx - r * 0.5f, cy - r * 0.5f) to r * 0.09f,
        Offset(cx + r * 0.52f, cy - r * 0.42f) to r * 0.11f,
        Offset(cx - r * 0.42f, cy + r * 0.52f) to r * 0.09f,
        Offset(cx + r * 0.55f, cy + r * 0.46f) to r * 0.08f,
    )
    positions.forEach { (pos, rad) -> drawCircle(color, rad, pos) }
}

internal fun DrawScope.drawBomb(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawCircle(color, r * 0.52f, Offset(cx, cy + r * 0.22f), style = stroke)
    val fuse = Path().apply {
        moveTo(cx, cy - r * 0.3f)
        cubicTo(cx + r * 0.28f, cy - r * 0.7f, cx + r * 0.58f, cy - r * 0.52f, cx + r * 0.5f, cy - r)
    }
    drawPath(fuse, color, style = stroke)
    drawCircle(color, r * 0.09f, Offset(cx + r * 0.5f, cy - r))
}

internal fun DrawScope.drawPawPrint(cx: Float, cy: Float, r: Float, color: Color) {
    drawCircle(color, r * 0.38f, Offset(cx, cy + r * 0.22f))
    drawCircle(color, r * 0.17f, Offset(cx - r * 0.45f, cy - r * 0.12f))
    drawCircle(color, r * 0.17f, Offset(cx + r * 0.45f, cy - r * 0.12f))
    drawCircle(color, r * 0.15f, Offset(cx - r * 0.22f, cy - r * 0.58f))
    drawCircle(color, r * 0.15f, Offset(cx + r * 0.22f, cy - r * 0.58f))
}
