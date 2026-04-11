package com.cocode.battleship.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocode.battleship.domain.ai.BattleshipAI
import com.cocode.battleship.domain.model.CellState
import com.cocode.battleship.domain.model.GamePhase
import com.cocode.battleship.domain.model.Ship
import com.cocode.battleship.domain.model.SuperWeapon
import com.cocode.battleship.domain.model.resolveWeaponCells
import com.cocode.battleship.domain.scoring.GameOutcome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val sounds = SoundManager()

    override fun onCleared() {
        super.onCleared()
        sounds.release()
    }

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
        viewModelScope.launch {
            val aiBoard = withContext(Dispatchers.Default) { BattleshipAI.placeShipsRandomly() }
            _state.value = s.copy(
                phase = GamePhase.BATTLE,
                aiBoard = aiBoard,
                isPlayerTurn = true,
                message = "Your turn — tap to fire!"
            )
        }
    }

    fun selectWeapon(weapon: SuperWeapon) {
        val s = _state.value
        if (s.phase != GamePhase.BATTLE || !s.isPlayerTurn) return
        if (weapon !in s.availableWeapons) return
        _state.value = s.copy(
            selectedWeapon = if (s.selectedWeapon == weapon) null else weapon
        )
    }

    fun deselectWeapon() {
        _state.value = _state.value.copy(selectedWeapon = null)
    }

    fun playerAttack(row: Int, col: Int) {
        val s = _state.value
        if (s.phase != GamePhase.BATTLE || !s.isPlayerTurn) return

        val selected = s.selectedWeapon
        val firedCells: List<Pair<Int, Int>>
        val newAiBoard: com.cocode.battleship.domain.model.Board

        if (selected != null) {
            firedCells = resolveWeaponCells(selected, row, col)
            newAiBoard = s.aiBoard.receiveWeaponAttack(firedCells)
        } else {
            if (s.aiBoard.hasBeenAttacked(row, col)) return
            firedCells = listOf(row to col)
            newAiBoard = s.aiBoard.receiveAttack(row, col)
        }

        val previouslySunk = s.aiBoard.ships.filter { it.isSunk }.map { it.type }.toSet()
        val nowSunk = newAiBoard.ships.filter { it.isSunk }.map { it.type }.toSet()
        val newlySunkTypes = nowSunk - previouslySunk

        val alreadyGrantedTypes = (s.availableWeapons + listOfNotNull(selected))
            .map { it.unlockShip }.toSet()
        val newAvailable = s.availableWeapons.filter { it != selected } +
            newlySunkTypes.filter { it !in alreadyGrantedTypes }.map { SuperWeapon.forShipType(it) }

        val primaryCellState = newAiBoard.getCellState(row, col)

        if (newAiBoard.allShipsSunk()) {
            sounds.playWin()
            val newTrackers = updateTrackersForFire(s.trackers, newAiBoard, firedCells, newlySunkTypes)
            val stats = buildGameStats(newTrackers, s.playerBoard, newAiBoard, GameOutcome.WIN)
            val result = computeScoreResult(stats, SessionStats.currentWinStreak + 1)
            SessionStats.record(result.score, isWin = true)
            _state.value = s.copy(
                aiBoard = newAiBoard,
                trackers = newTrackers,
                availableWeapons = newAvailable,
                selectedWeapon = null,
                phase = GamePhase.GAME_OVER,
                winner = "Player",
                message = "You sunk the fleet! You win!",
                scoreResult = result
            )
            return
        }

        playAttackSound(primaryCellState)
        val hitMsg = buildPlayerHitMessage(selected, primaryCellState, newlySunkTypes, newAiBoard)
        val newTrackers = updateTrackersForFire(s.trackers, newAiBoard, firedCells, newlySunkTypes)

        _state.value = s.copy(
            aiBoard = newAiBoard,
            trackers = newTrackers,
            availableWeapons = newAvailable,
            selectedWeapon = null,
            isPlayerTurn = false,
            message = hitMsg
        )

        viewModelScope.launch {
            delay(800)
            aiAttack()
        }
    }

    private suspend fun aiAttack() {
        val s = _state.value
        val (row, col) = withContext(Dispatchers.Default) { BattleshipAI.chooseAttack(s.playerBoard) }
        val newPlayerBoard = s.playerBoard.receiveAttack(row, col)
        val cellState = newPlayerBoard.getCellState(row, col)

        if (newPlayerBoard.allShipsSunk()) {
            sounds.playLose()
            val stats = buildGameStats(s.trackers, newPlayerBoard, s.aiBoard, GameOutcome.LOSS)
            val result = computeScoreResult(stats, SessionStats.currentWinStreak)
            SessionStats.record(result.score, isWin = false)
            _state.value = s.copy(
                playerBoard = newPlayerBoard,
                phase = GamePhase.GAME_OVER,
                winner = "AI",
                message = "AI sunk your fleet! You lose!",
                scoreResult = result
            )
            return
        }

        playAttackSound(cellState)

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

    private fun playAttackSound(cellState: CellState) {
        when (cellState) {
            CellState.HIT -> sounds.playHit()
            CellState.SUNK -> sounds.playSunk()
            else -> sounds.playMiss()
        }
    }

    fun resetGame() {
        _state.value = GameUiState()
    }
}
