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

internal fun DrawScope.drawSubmarine(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawOval(color, topLeft = Offset(cx - r, cy - r * 0.28f), size = Size(r * 2f, r * 0.56f), style = stroke)
    drawRect(color, topLeft = Offset(cx - r * 0.22f, cy - r * 0.78f), size = Size(r * 0.44f, r * 0.52f), style = stroke)
    drawLine(color, Offset(cx + r * 0.12f, cy - r * 0.78f), Offset(cx + r * 0.12f, cy - r * 1.05f), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx + r * 0.12f, cy - r * 1.05f), Offset(cx + r * 0.38f, cy - r * 1.05f), stroke.width, StrokeCap.Round)
}

internal fun DrawScope.drawShield(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
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

internal fun DrawScope.drawShipSilhouette(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawRect(color, topLeft = Offset(cx - r, cy - r * 0.22f), size = Size(r * 2f, r * 0.44f), style = stroke)
    val bow = Path().apply {
        moveTo(cx + r, cy - r * 0.22f); lineTo(cx + r, cy + r * 0.22f); lineTo(cx + r * 1.38f, cy); close()
    }
    drawPath(bow, color)
    drawRect(color, topLeft = Offset(cx - r * 0.48f, cy - r * 0.68f), size = Size(r * 0.96f, r * 0.46f), style = stroke)
    drawLine(color, Offset(cx, cy - r * 0.68f), Offset(cx, cy - r), stroke.width * 0.75f, StrokeCap.Round)
}

internal fun DrawScope.drawSwimWaves(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    for (dy in listOf(-r * 0.28f, r * 0.28f)) {
        val wave = Path().apply {
            moveTo(cx - r, cy + dy)
            cubicTo(cx - r * 0.5f, cy + dy - r * 0.32f, cx, cy + dy + r * 0.32f, cx + r * 0.5f, cy + dy - r * 0.32f)
            cubicTo(cx + r * 0.5f, cy + dy - r * 0.32f, cx + r * 0.75f, cy + dy, cx + r, cy + dy)
        }
        drawPath(wave, color, style = stroke)
    }
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

internal fun DrawScope.drawFlames(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
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

internal fun DrawScope.drawPawPrint(cx: Float, cy: Float, r: Float, color: Color) {
    drawCircle(color, r * 0.38f, Offset(cx, cy + r * 0.22f))
    drawCircle(color, r * 0.17f, Offset(cx - r * 0.45f, cy - r * 0.12f))
    drawCircle(color, r * 0.17f, Offset(cx + r * 0.45f, cy - r * 0.12f))
    drawCircle(color, r * 0.15f, Offset(cx - r * 0.22f, cy - r * 0.58f))
    drawCircle(color, r * 0.15f, Offset(cx + r * 0.22f, cy - r * 0.58f))
}

internal fun DrawScope.drawTorpedo(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawOval(color, topLeft = Offset(cx - r * 0.82f, cy - r * 0.26f), size = Size(r * 1.42f, r * 0.52f), style = stroke)
    val nose = Path().apply {
        moveTo(cx + r * 0.6f, cy - r * 0.26f); lineTo(cx + r * 0.6f, cy + r * 0.26f); lineTo(cx + r, cy); close()
    }
    drawPath(nose, color)
    val fin = Path().apply {
        moveTo(cx - r * 0.82f, cy); lineTo(cx - r, cy - r * 0.52f); lineTo(cx - r * 0.68f, cy - r * 0.26f); close()
    }
    drawPath(fin, color)
}

internal fun DrawScope.drawAnchor(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawCircle(color, r * 0.24f, Offset(cx, cy - r * 0.58f), style = stroke)
    drawLine(color, Offset(cx, cy - r * 0.34f), Offset(cx, cy + r * 0.68f), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx - r * 0.55f, cy - r * 0.58f), Offset(cx + r * 0.55f, cy - r * 0.58f), stroke.width, StrokeCap.Round)
    drawArc(color, startAngle = 0f, sweepAngle = 180f, useCenter = false,
        topLeft = Offset(cx - r * 0.6f, cy + r * 0.12f), size = Size(r * 1.2f, r * 0.56f), style = stroke)
    drawLine(color, Offset(cx - r * 0.6f, cy + r * 0.4f), Offset(cx + r * 0.6f, cy + r * 0.4f), stroke.width, StrokeCap.Round)
}

internal fun DrawScope.drawFan(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    val base = Offset(cx, cy + r * 0.25f)
    listOf(
        Offset(cx - r * 0.87f, cy - r * 0.25f),
        Offset(cx - r * 0.5f, cy - r * 0.62f),
        Offset(cx, cy - r * 0.75f),
        Offset(cx + r * 0.5f, cy - r * 0.62f),
        Offset(cx + r * 0.87f, cy - r * 0.25f)
    ).forEach { tip -> drawLine(color, base, tip, stroke.width, StrokeCap.Round) }
    drawCircle(color, r * 0.1f, base)
}

internal fun DrawScope.drawRadiation(cx: Float, cy: Float, r: Float, color: Color) {
    val outerR = r * 0.82f
    listOf(30f, 150f, 270f).forEach { angle ->
        drawArc(color, startAngle = angle, sweepAngle = 60f, useCenter = true,
            topLeft = Offset(cx - outerR, cy - outerR), size = Size(outerR * 2f, outerR * 2f))
    }
    drawCircle(color, r * 0.22f, Offset(cx, cy))
}

internal fun DrawScope.drawRetreat(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    val arrow = Path().apply {
        moveTo(cx - r, cy)
        lineTo(cx - r * 0.12f, cy - r); lineTo(cx - r * 0.12f, cy - r * 0.38f)
        lineTo(cx + r, cy - r * 0.38f); lineTo(cx + r, cy + r * 0.38f)
        lineTo(cx - r * 0.12f, cy + r * 0.38f); lineTo(cx - r * 0.12f, cy + r)
        close()
    }
    drawPath(arrow, color, style = stroke)
}

internal fun DrawScope.drawSunrise(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    val hy = cy + r * 0.18f
    drawArc(color, startAngle = 180f, sweepAngle = 180f, useCenter = false,
        topLeft = Offset(cx - r * 0.72f, hy - r * 0.72f), size = Size(r * 1.44f, r * 0.72f), style = stroke)
    drawLine(color, Offset(cx - r * 1.05f, hy), Offset(cx + r * 1.05f, hy), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx - r * 0.72f, hy - r * 0.52f), Offset(cx - r, hy - r * 0.85f), stroke.width * 0.75f, StrokeCap.Round)
    drawLine(color, Offset(cx, hy - r * 0.72f), Offset(cx, hy - r * 1.05f), stroke.width * 0.75f, StrokeCap.Round)
    drawLine(color, Offset(cx + r * 0.72f, hy - r * 0.52f), Offset(cx + r, hy - r * 0.85f), stroke.width * 0.75f, StrokeCap.Round)
}

internal fun DrawScope.drawMedal(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawCircle(color, r * 0.6f, Offset(cx, cy + r * 0.14f), style = stroke)
    drawLine(color, Offset(cx - r * 0.28f, cy - r * 0.46f), Offset(cx - r * 0.28f, cy - r), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx + r * 0.28f, cy - r * 0.46f), Offset(cx + r * 0.28f, cy - r), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx - r * 0.55f, cy - r), Offset(cx + r * 0.55f, cy - r), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx - r * 0.55f, cy - r * 0.46f), Offset(cx + r * 0.55f, cy - r * 0.46f), stroke.width, StrokeCap.Round)
}
