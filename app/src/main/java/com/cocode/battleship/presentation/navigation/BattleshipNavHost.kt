package com.cocode.battleship.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cocode.battleship.presentation.game.GameOverScreen
import com.cocode.battleship.presentation.game.GameScreen
import com.cocode.battleship.presentation.game.GameViewModel
import com.cocode.battleship.presentation.menu.MenuScreen
import com.cocode.battleship.presentation.placement.PlacementScreen

@Composable
fun BattleshipNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // Single ViewModel shared across all screens — created outside NavHost so all routes share the same instance
    val gameViewModel: GameViewModel = viewModel()
    val state by gameViewModel.state.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Menu.route,
        modifier = modifier
    ) {
        composable(Screen.Menu.route) {
            MenuScreen(
                onStartGame = {
                    gameViewModel.resetGame()
                    navController.navigate(Screen.Placement.route)
                }
            )
        }
        composable(Screen.Placement.route) {
            PlacementScreen(
                viewModel = gameViewModel,
                onPlacementComplete = {
                    // Pop Placement off the stack so Back from Game returns to Menu
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Placement.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Game.route) {
            GameScreen(
                viewModel = gameViewModel,
                onGameOver = {
                    // Pop Game off the stack so Back from GameOver doesn't re-trigger GAME_OVER
                    navController.navigate(Screen.GameOver.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.GameOver.route) {
            GameOverScreen(
                winner = state.winner ?: "",
                onPlayAgain = {
                    gameViewModel.resetGame()
                    navController.navigate(Screen.Placement.route) {
                        popUpTo(Screen.Menu.route)
                    }
                },
                onMainMenu = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Menu.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
