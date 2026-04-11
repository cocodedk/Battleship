package com.cocode.battleship.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.domain.model.Board
import com.cocode.battleship.domain.model.CellState
import com.cocode.battleship.domain.model.GRID_SIZE
import com.cocode.battleship.domain.model.Ship
import com.cocode.battleship.ui.theme.DeepNavy
import com.cocode.battleship.ui.theme.MissWhite
import com.cocode.battleship.ui.theme.NavyBorder
import com.cocode.battleship.ui.theme.NavyCard
import com.cocode.battleship.ui.theme.NavySurface
import com.cocode.battleship.ui.theme.PhosphorGreen
import com.cocode.battleship.ui.theme.PhosphorGreenDim
import com.cocode.battleship.ui.theme.ShipSteel
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TextDim
import com.cocode.battleship.ui.theme.TorpedoRed
import com.cocode.battleship.ui.theme.TorpedoRedDim

private val ROW_LABELS = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")

private data class CellAppearance(
    val bg: Color,
    val border: Color,
    val marker: String?,
    val markerColor: Color?,
)

@Composable
fun BattleGrid(
    board: Board,
    showShips: Boolean,
    onCellClick: ((row: Int, col: Int) -> Unit)? = null,
    previewShip: Ship? = null,
    modifier: Modifier = Modifier
) {
    val previewPositions = previewShip?.positions?.toSet() ?: emptySet()
    val isPreviewValid = previewShip?.let { board.isValidPlacement(it) } ?: true

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Box(modifier = Modifier.size(20.dp))
            for (col in 0 until GRID_SIZE) {
                Text(
                    text = "${col + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    color = TextDim,
                )
            }
        }
        for (row in 0 until GRID_SIZE) {
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ROW_LABELS[row],
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.size(20.dp),
                    color = TextDim,
                )
                for (col in 0 until GRID_SIZE) {
                    GridCell(
                        board = board, row = row, col = col,
                        showShips = showShips,
                        previewPositions = previewPositions,
                        isPreviewValid = isPreviewValid,
                        onCellClick = onCellClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun GridCell(
    board: Board,
    row: Int,
    col: Int,
    showShips: Boolean,
    previewPositions: Set<Pair<Int, Int>>,
    isPreviewValid: Boolean,
    onCellClick: ((row: Int, col: Int) -> Unit)?,
    modifier: Modifier,
) {
    val cellState = board.getCellState(row, col)
    val hasShip = showShips && board.getShipAt(row, col) != null
    val isPreviewCell = previewPositions.contains(Pair(row, col))
    val hasBeenAttacked = board.hasBeenAttacked(row, col)
    val isClickable = onCellClick != null && !hasBeenAttacked

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
        hasShip ->
            CellAppearance(ShipSteel.copy(alpha = 0.65f), ShipSteel, null, null)
        isClickable ->
            CellAppearance(NavySurface, SonarCyan.copy(alpha = 0.2f), null, null)
        else ->
            CellAppearance(NavySurface, NavyBorder.copy(alpha = 0.5f), null, null)
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
    }
}
