package com.cocode.battleship.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.domain.model.Board
import com.cocode.battleship.domain.model.CellState
import com.cocode.battleship.domain.model.Ship
import com.cocode.battleship.domain.model.ShipType
import com.cocode.battleship.domain.model.SuperWeapon
import com.cocode.battleship.presentation.game.WeaponEffectCell
import com.cocode.battleship.ui.theme.MissWhite
import com.cocode.battleship.ui.theme.NavyBorder
import com.cocode.battleship.ui.theme.NavyCard
import com.cocode.battleship.ui.theme.NavySurface
import com.cocode.battleship.ui.theme.NeonLime
import com.cocode.battleship.ui.theme.NeonMagenta
import com.cocode.battleship.ui.theme.NeonOrange
import com.cocode.battleship.ui.theme.NeonViolet
import com.cocode.battleship.ui.theme.NeonYellow
import com.cocode.battleship.ui.theme.PhosphorGreen
import com.cocode.battleship.ui.theme.PhosphorGreenDim
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TorpedoRed
import com.cocode.battleship.ui.theme.TorpedoRedDim

internal data class CellAppearance(
    val bg: Color,
    val border: Color,
    val marker: String?,
    val markerColor: Color?,
)

@Composable
internal fun GridCell(
    board: Board,
    row: Int,
    col: Int,
    showShips: Boolean,
    previewPositions: Set<Pair<Int, Int>>,
    isPreviewValid: Boolean,
    weaponEffectCell: WeaponEffectCell?,
    onCellClick: ((row: Int, col: Int) -> Unit)?,
    allowAttackedClicks: Boolean,
    modifier: Modifier,
) {
    val cellState = board.getCellState(row, col)
    val ship = if (showShips) board.getShipAt(row, col) else null
    val hasShip = ship != null
    val isPreviewCell = previewPositions.contains(Pair(row, col))
    val hasBeenAttacked = board.hasBeenAttacked(row, col)
    val isClickable = onCellClick != null && (allowAttackedClicks || !hasBeenAttacked)

    val appearance = when {
        isPreviewCell -> if (isPreviewValid)
            CellAppearance(SonarCyan.copy(alpha = 0.35f), SonarCyan, null, null)
        else
            CellAppearance(TorpedoRed.copy(alpha = 0.3f), TorpedoRed, null, null)
        cellState == CellState.HIT -> if (showShips)
            CellAppearance(TorpedoRedDim, TorpedoRed, "✕", TorpedoRed)
        else
            CellAppearance(PhosphorGreenDim, PhosphorGreen, "✕", PhosphorGreen)
        cellState == CellState.SUNK -> if (showShips)
            CellAppearance(TorpedoRedDim, TorpedoRed.copy(alpha = 0.5f), "✕", TorpedoRed.copy(alpha = 0.8f))
        else
            CellAppearance(PhosphorGreenDim, PhosphorGreen.copy(alpha = 0.5f), "✕", PhosphorGreen.copy(alpha = 0.9f))
        cellState == CellState.MISS ->
            CellAppearance(NavyCard, NavyBorder, "·", MissWhite.copy(alpha = 0.6f))
        hasShip -> {
            val neon = shipNeonColor(ship!!.type)
            CellAppearance(neon.copy(alpha = 0.20f), neon.copy(alpha = 0.80f), null, null)
        }
        isClickable ->
            CellAppearance(NavySurface, SonarCyan.copy(alpha = 0.2f), null, null)
        else ->
            CellAppearance(NavySurface, NavyBorder.copy(alpha = 0.5f), null, null)
    }

    // One-shot flash on hit/sunk
    val flashAlpha = remember { Animatable(0f) }
    LaunchedEffect(cellState) {
        if (cellState == CellState.HIT || cellState == CellState.SUNK) {
            flashAlpha.snapTo(0.75f)
            flashAlpha.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
        }
    }

    val weaponEffectAlpha = remember { Animatable(0f) }
    val weaponEffectScale = remember { Animatable(0.45f) }
    LaunchedEffect(weaponEffectCell?.triggerId) {
        if (weaponEffectCell != null) {
            kotlinx.coroutines.delay(weaponEffectCell.delayMs.toLong())
            weaponEffectAlpha.snapTo(0.95f)
            weaponEffectScale.snapTo(0.45f)
            weaponEffectScale.animateTo(1f, tween(320, easing = FastOutSlowInEasing))
            weaponEffectAlpha.animateTo(0f, tween(420, easing = FastOutSlowInEasing))
        } else {
            weaponEffectAlpha.snapTo(0f)
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(0.5.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(appearance.bg)
            .border(0.5.dp, appearance.border, RoundedCornerShape(2.dp))
            .then(if (isClickable) Modifier.clickable { onCellClick!!(row, col) } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (appearance.marker != null && appearance.markerColor != null) {
            Text(
                text = appearance.marker,
                color = appearance.markerColor,
                fontSize = if (appearance.marker == "·") 14.sp else 9.sp,
                lineHeight = 9.sp,
            )
        }
        // Flash overlay
        if (flashAlpha.value > 0f) {
            Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = flashAlpha.value)))
        }
        if (weaponEffectCell != null && weaponEffectAlpha.value > 0f) {
            WeaponEffectOverlay(
                weapon = weaponEffectCell.weapon,
                alpha = weaponEffectAlpha.value,
                scale = weaponEffectScale.value
            )
        }
        // Sunk glow
        if (cellState == CellState.SUNK) {
            SunkGlowOverlay()
        }
    }
}

@Composable
private fun SunkGlowOverlay() {
    val inf = rememberInfiniteTransition(label = "sunk_glow")
    val alpha by inf.animateFloat(
        initialValue = 0.08f, targetValue = 0.35f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow"
    )
    Box(Modifier.fillMaxSize().background(TorpedoRed.copy(alpha = alpha)))
}

internal fun shipNeonColor(type: ShipType): Color = when (type) {
    ShipType.CARRIER    -> NeonMagenta
    ShipType.BATTLESHIP -> NeonOrange
    ShipType.CRUISER    -> NeonLime
    ShipType.SUBMARINE  -> NeonViolet
    ShipType.DESTROYER  -> NeonYellow
}
