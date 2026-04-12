package com.cocode.battleship.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cocode.battleship.domain.model.Board
import com.cocode.battleship.domain.model.GRID_SIZE
import com.cocode.battleship.domain.model.Ship
import com.cocode.battleship.presentation.game.SuperWeaponEffect
import com.cocode.battleship.ui.theme.TextDim

private val ROW_LABELS = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")

@Composable
fun BattleGrid(
    board: Board,
    showShips: Boolean,
    onCellClick: ((row: Int, col: Int) -> Unit)? = null,
    previewShip: Ship? = null,
    allowAttackedClicks: Boolean = false,
    weaponEffect: SuperWeaponEffect? = null,
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
                modifier = Modifier.fillMaxWidth(),
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
                        weaponEffectCell = weaponEffect?.effectCellAt(row, col),
                        onCellClick = onCellClick,
                        allowAttackedClicks = allowAttackedClicks,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
