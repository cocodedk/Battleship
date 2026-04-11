package com.cocode.battleship.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocode.battleship.domain.ai.BattleshipAI
import com.cocode.battleship.domain.model.CellState
import com.cocode.battleship.domain.model.GamePhase
import com.cocode.battleship.domain.model.Ship
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    fun toggleOrientation() {
        _state.value = _state.value.copy(isHorizontal = !_state.value.isHorizontal)
    }

    fun placeShip(row: Int, col: Int) {
        val s = _state.value
        val shipType = s.currentShipType ?: return
        val ship = Ship(shipType, row, col, s.isHorizontal)
        if (!s.playerBoard.isValidPlacement(ship)) return

        val newBoard = s.playerBoard.placeShip(ship)
        val remaining = s.shipsToPlace.drop(1)
        _state.value = s.copy(
            playerBoard = newBoard,
            shipsToPlace = remaining,
            currentShipType = remaining.firstOrNull()
        )
    }

    fun autoPlaceShips() {
        val aiPlacedBoard = BattleshipAI.placeShipsRandomly()
        val s = _state.value
        _state.value = s.copy(
            playerBoard = aiPlacedBoard,
            shipsToPlace = emptyList(),
            currentShipType = null
        )
    }

    fun confirmPlacement() {
        val s = _state.value
        if (s.shipsToPlace.isNotEmpty()) return
        _state.value = s.copy(
            phase = GamePhase.BATTLE,
            aiBoard = BattleshipAI.placeShipsRandomly(),
            isPlayerTurn = true,
            message = "Your turn — tap to fire!"
        )
    }

    fun playerAttack(row: Int, col: Int) {
        val s = _state.value
        if (s.phase != GamePhase.BATTLE || !s.isPlayerTurn) return
        if (s.aiBoard.hasBeenAttacked(row, col)) return

        val newAiBoard = s.aiBoard.receiveAttack(row, col)
        val cellState = newAiBoard.getCellState(row, col)

        if (newAiBoard.allShipsSunk()) {
            _state.value = s.copy(
                aiBoard = newAiBoard,
                phase = GamePhase.GAME_OVER,
                winner = "Player",
                message = "You sunk the fleet! You win!"
            )
            return
        }

        val hitMsg = when (cellState) {
            CellState.HIT -> "Hit! Keep going!"
            CellState.SUNK -> "You sunk a ${newAiBoard.ships.find { it.isSunk && it.occupies(row, col) }?.type?.displayName ?: "ship"}!"
            else -> "Miss."
        }

        _state.value = s.copy(
            aiBoard = newAiBoard,
            isPlayerTurn = false,
            message = hitMsg
        )

        viewModelScope.launch {
            delay(800)
            aiAttack()
        }
    }

    private fun aiAttack() {
        val s = _state.value
        val (row, col) = BattleshipAI.chooseAttack(s.playerBoard)
        val newPlayerBoard = s.playerBoard.receiveAttack(row, col)
        val cellState = newPlayerBoard.getCellState(row, col)

        if (newPlayerBoard.allShipsSunk()) {
            _state.value = s.copy(
                playerBoard = newPlayerBoard,
                phase = GamePhase.GAME_OVER,
                winner = "AI",
                message = "AI sunk your fleet! You lose!"
            )
            return
        }

        val aiMsg = when (cellState) {
            CellState.HIT -> "AI hit your ship!"
            CellState.SUNK -> "AI sunk your ${newPlayerBoard.ships.find { it.isSunk && it.occupies(row, col) }?.type?.displayName ?: "ship"}!"
            else -> "AI missed. Your turn!"
        }

        _state.value = s.copy(
            playerBoard = newPlayerBoard,
            isPlayerTurn = true,
            message = aiMsg
        )
    }

    fun resetGame() {
        _state.value = GameUiState()
    }
}
