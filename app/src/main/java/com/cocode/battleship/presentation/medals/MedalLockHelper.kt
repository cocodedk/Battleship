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

internal fun DrawScope.drawLockSymbol(cx: Float, cy: Float, r: Float, color: Color) {
    val stroke = Stroke(1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    drawRect(color, topLeft = Offset(cx - r * 0.55f, cy - r * 0.08f),
        size = Size(r * 1.1f, r * 0.88f), style = stroke)
    val bow = Path().apply {
        moveTo(cx - r * 0.35f, cy - r * 0.08f)
        lineTo(cx - r * 0.35f, cy - r * 0.52f)
        cubicTo(cx - r * 0.35f, cy - r, cx + r * 0.35f, cy - r, cx + r * 0.35f, cy - r * 0.52f)
        lineTo(cx + r * 0.35f, cy - r * 0.08f)
    }
    drawPath(bow, color, style = stroke)
    drawCircle(color, r * 0.14f, Offset(cx, cy + r * 0.28f))
}
