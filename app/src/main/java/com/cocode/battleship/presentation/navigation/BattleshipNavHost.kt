package com.cocode.battleship.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cocode.battleship.presentation.game.GameOverScreen
import com.cocode.battleship.presentation.game.GameScreen
import com.cocode.battleship.presentation.game.GameViewModel
import com.cocode.battleship.presentation.badges.BadgesScreen
import com.cocode.battleship.presentation.badges.BadgesViewModel
import com.cocode.battleship.presentation.medals.MedalsScreen
import com.cocode.battleship.presentation.medals.MedalsViewModel
import com.cocode.battleship.presentation.medals.SharedPreferencesMedalsStorage
import com.cocode.battleship.presentation.menu.MenuScreen
import com.cocode.battleship.presentation.placement.PlacementScreen
import com.cocode.battleship.presentation.stats.StatsScreen

@Composable
fun BattleshipNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // Single ViewModel shared across all screens — created outside NavHost so all routes share the same instance
    val gameViewModel: GameViewModel = viewModel()
    val context = LocalContext.current
    val medalsStorage = remember { SharedPreferencesMedalsStorage(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Menu.route,
            modifier = modifier.systemBarsPadding()
        ) {
            composable(Screen.Menu.route) {
                MenuScreen(
                    onStartGame = {
                        gameViewModel.resetGame()
                        navController.navigate(Screen.Placement.route)
                    },
                    onViewStats = { navController.navigate(Screen.Stats.route) },
                    onViewMedals = { navController.navigate(Screen.Medals.route) },
                    onViewBadges = { navController.navigate(Screen.Badges.route) }
                )
            }
            composable(Screen.Stats.route) {
                StatsScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Medals.route) {
                val medalsViewModel: MedalsViewModel = viewModel(
                    factory = MedalsViewModel.factory(medalsStorage)
                )
                MedalsScreen(
                    viewModel = medalsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Badges.route) {
                val badgesViewModel: BadgesViewModel = viewModel(
                    factory = BadgesViewModel.factory(medalsStorage)
                )
                BadgesScreen(
                    viewModel = badgesViewModel,
                    onBack = { navController.popBackStack() }
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
                    viewModel = gameViewModel,
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
                    },
                    onBadges = { navController.navigate(Screen.Badges.route) }
                )
            }
        }
    }
}
