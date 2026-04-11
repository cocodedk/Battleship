package com.cocode.battleship.domain.model

enum class GamePhase {
    PLACEMENT,
    BATTLE,
    GAME_OVER
}

data class GameState(
    val phase: GamePhase = GamePhase.PLACEMENT,
    val playerBoard: Board = Board(),
    val aiBoard: Board = Board(),
    val isPlayerTurn: Boolean = true,
    val winner: String? = null,
    val message: String = "Place your ships"
)
