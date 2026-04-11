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

private val ROW_LABELS = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
private val SHIP_COLOR = Color(0xFF1E88E5)
private val MISS_COLOR = Color(0xFF78909C)
private val SUNK_COLOR = Color(0xFF37474F)
private val PREVIEW_COLOR = Color(0xFF80CBC4).copy(alpha = 0.6f)
private val INVALID_PREVIEW_COLOR = Color(0xFFEF9A9A).copy(alpha = 0.6f)

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
        // Column headers row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            // Corner spacer matching label width
            Box(modifier = Modifier.size(20.dp))
            for (col in 0 until GRID_SIZE) {
                Text(
                    text = "${col + 1}",
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Grid rows
        for (row in 0 until GRID_SIZE) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Row label
                Text(
                    text = ROW_LABELS[row],
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Cells
                for (col in 0 until GRID_SIZE) {
                    val cellState = board.getCellState(row, col)
                    val hasShip = showShips && board.getShipAt(row, col) != null
                    val isPreviewCell = previewPositions.contains(Pair(row, col))
                    val hasBeenAttacked = board.hasBeenAttacked(row, col)

                    val cellColor = when {
                        isPreviewCell -> if (isPreviewValid) PREVIEW_COLOR else INVALID_PREVIEW_COLOR
                        cellState == CellState.HIT -> MaterialTheme.colorScheme.error
                        cellState == CellState.SUNK -> SUNK_COLOR
                        cellState == CellState.MISS -> MISS_COLOR
                        hasShip -> SHIP_COLOR
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val isClickable = onCellClick != null && !hasBeenAttacked
                    val cellModifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(1.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(cellColor)
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(2.dp)
                        )
                        .then(
                            if (isClickable) {
                                Modifier.clickable { onCellClick(row, col) }
                            } else {
                                Modifier
                            }
                        )

                    Box(modifier = cellModifier) {
                        if (cellState == CellState.HIT || cellState == CellState.SUNK) {
                            Text(
                                text = "×",
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else if (cellState == CellState.MISS) {
                            Text(
                                text = "•",
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}
