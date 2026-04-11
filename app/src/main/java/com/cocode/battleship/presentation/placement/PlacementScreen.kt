package com.cocode.battleship.presentation.placement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cocode.battleship.presentation.components.BattleGrid
import com.cocode.battleship.presentation.game.GameViewModel

@Composable
fun PlacementScreen(
    viewModel: GameViewModel,
    onPlacementComplete: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Place Your Ships",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            BattleGrid(
                board = state.playerBoard,
                showShips = true,
                onCellClick = { r, c -> viewModel.placeShip(r, c) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Current ship info
            if (state.currentShipType != null) {
                Text(
                    text = "Placing: ${state.currentShipType!!.displayName} (size ${state.currentShipType!!.size})",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "All ships placed!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Orientation and auto-place buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.toggleOrientation() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (state.isHorizontal) "Rotate: Horizontal" else "Rotate: Vertical"
                    )
                }
                OutlinedButton(
                    onClick = { viewModel.autoPlaceShips() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Auto Place")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Start Battle button
            Button(
                onClick = {
                    viewModel.confirmPlacement()
                    onPlacementComplete()
                },
                enabled = state.shipsToPlace.isEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Start Battle")
            }
        }
    }
}
