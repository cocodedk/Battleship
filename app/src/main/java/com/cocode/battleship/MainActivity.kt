package com.cocode.battleship

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.cocode.battleship.presentation.navigation.BattleshipNavHost
import com.cocode.battleship.presentation.game.SessionStats
import com.cocode.battleship.presentation.game.SharedPreferencesSessionStatsStorage
import com.cocode.battleship.ui.theme.BattleshipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionStats.initialize(SharedPreferencesSessionStatsStorage(applicationContext))
        enableEdgeToEdge()
        setContent {
            BattleshipTheme {
                BattleshipNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
