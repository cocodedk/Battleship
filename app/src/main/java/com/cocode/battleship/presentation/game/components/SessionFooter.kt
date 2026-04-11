package com.cocode.battleship.presentation.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.ui.theme.TextDim

@Composable
fun SessionFooter(
    gamesPlayed: Int,
    totalWins: Int,
    currentWinStreak: Int,
    bestScore: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.game_over_session_footer, gamesPlayed, totalWins, currentWinStreak, bestScore),
            style = MaterialTheme.typography.labelSmall,
            color = TextDim,
            letterSpacing = 1.sp,
        )
    }
}
