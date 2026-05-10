package com.cocode.battleship.presentation.medals

import androidx.compose.ui.graphics.Color
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
        Badge.BLITZ                 -> drawLightning(cx, cy, r, color)
        Badge.SEA_WOLF              -> drawPawPrint(cx, cy, r, color)
        Badge.LUCKY_DOG             -> drawScatterDots(cx, cy, r, color)
        Badge.COLD_OPENER           -> drawArrow(cx, cy, r, color, stroke)
        Badge.IRON_HULL             -> drawShipSilhouette(cx, cy, r, color, stroke)
        Badge.CRUISER_LIVES         -> drawShipSilhouette(cx, cy, r, color, stroke)
        Badge.TORPEDO_ACE           -> drawTorpedo(cx, cy, r, color, stroke)
        Badge.BATTLESHIP_HUNTER     -> drawAnchor(cx, cy, r, color, stroke)
        Badge.SMALL_GAME            -> drawWaveArc(cx, cy, r, color, stroke)
        Badge.SPRAY_AND_PRAY        -> drawFan(cx, cy, r, color, stroke)
        Badge.NUCLEAR_OPTION        -> drawRadiation(cx, cy, r, color)
        Badge.SCATTERSHOT           -> drawScatterDots(cx, cy, r, color)
        Badge.TACTICAL_RETREAT      -> drawRetreat(cx, cy, r, color, stroke)
        Badge.PHOENIX               -> drawSunrise(cx, cy, r, color, stroke)
        Badge.SPITE                 -> drawDoubleTarget(cx, cy, r, color, stroke)
        Badge.FLEET_COMMANDER       -> drawMedal(cx, cy, r, color, stroke)
        Badge.SEA_VETERAN           -> drawAnchor(cx, cy, r, color, stroke)
        Badge.IRON_ADMIRAL          -> drawMedal(cx, cy, r, color, stroke)
    }
}
