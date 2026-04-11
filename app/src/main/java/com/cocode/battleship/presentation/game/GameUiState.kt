package com.cocode.battleship.presentation.game

import com.cocode.battleship.domain.model.Board
import com.cocode.battleship.domain.model.FLEET
import com.cocode.battleship.domain.model.GamePhase
import com.cocode.battleship.domain.model.ShipType
import com.cocode.battleship.domain.scoring.ScoreResult

data class GameUiState(
    val phase: GamePhase = GamePhase.PLACEMENT,
    val playerBoard: Board = Board(),
    val aiBoard: Board = Board(),
    val isPlayerTurn: Boolean = true,
    val winner: String? = null,
    val message: String = "Place your ships",
    val shipsToPlace: List<ShipType> = FLEET,
    val currentShipType: ShipType? = FLEET.first(),
    val isHorizontal: Boolean = true,
    val trackers: TrackerState = TrackerState(),
    val scoreResult: ScoreResult? = null
)
