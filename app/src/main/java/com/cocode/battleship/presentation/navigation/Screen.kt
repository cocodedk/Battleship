package com.cocode.battleship.presentation.navigation

sealed class Screen(val route: String) {
    data object Menu : Screen("menu")
    data object Placement : Screen("placement")
    data object Game : Screen("game")
    data object GameOver : Screen("game_over")
    data object Stats : Screen("stats")
    data object Medals : Screen("medals")
}
