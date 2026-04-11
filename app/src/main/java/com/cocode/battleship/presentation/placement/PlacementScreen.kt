package com.cocode.battleship.presentation.placement

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.presentation.components.BattleGrid
import com.cocode.battleship.presentation.game.GameViewModel
import com.cocode.battleship.ui.theme.DeepNavy
import com.cocode.battleship.ui.theme.NavyBorder
import com.cocode.battleship.ui.theme.NavyCard
import com.cocode.battleship.ui.theme.PhosphorGreen
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TextDim
import com.cocode.battleship.ui.theme.TextSecondary

private const val SYMBOL_DEPLOY = "▶"
private const val SYMBOL_AUTO = "⚡"

@Composable
fun PlacementScreen(
    viewModel: GameViewModel,
    onPlacementComplete: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.placement_title),
                style = MaterialTheme.typography.headlineMedium,
                color = SonarCyan,
            )
            Text(
                text = stringResource(R.string.placement_select_cell),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                letterSpacing = 1.sp,
            )

            Spacer(modifier = Modifier.height(12.dp))

            BattleGrid(
                board = state.playerBoard,
                showShips = true,
                onCellClick = { r, c -> viewModel.placeShip(r, c) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            val currentShip = state.currentShipType
            if (currentShip != null) {
                Text(
                    text = currentShip.displayName.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = SonarCyan,
                )
                Text(
                    text = "■".repeat(currentShip.size) + "□".repeat(5 - currentShip.size),
                    fontSize = 20.sp,
                    color = SonarCyan,
                    letterSpacing = 5.sp,
                )
            } else {
                Text(
                    text = "$SYMBOL_DEPLOY  ${stringResource(R.string.placement_all_placed)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PhosphorGreen,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { viewModel.toggleOrientation() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, SonarCyan.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SonarCyan),
                ) {
                    Text(
                        text = if (state.isHorizontal) "↔  ${stringResource(R.string.placement_rotate_horizontal)}"
                        else "↕  ${stringResource(R.string.placement_rotate_vertical)}",
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                    )
                }
                OutlinedButton(
                    onClick = { viewModel.autoPlaceShips() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, SonarCyan.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SonarCyan),
                ) {
                    Text(text = "$SYMBOL_AUTO  ${stringResource(R.string.placement_auto_place)}", fontSize = 10.sp, letterSpacing = 1.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.confirmPlacement(); onPlacementComplete() },
                enabled = state.shipsToPlace.isEmpty(),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PhosphorGreen,
                    contentColor = DeepNavy,
                    disabledContainerColor = NavyCard,
                    disabledContentColor = TextDim,
                )
            ) {
                Text(
                    text = stringResource(R.string.placement_start_battle),
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    fontSize = 12.sp,
                )
            }
        }
    }
}
