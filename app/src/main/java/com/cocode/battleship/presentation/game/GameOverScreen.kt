package com.cocode.battleship.presentation.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.domain.scoring.Badge
import com.cocode.battleship.domain.scoring.GameStats
import com.cocode.battleship.domain.scoring.ScoreResult
import com.cocode.battleship.presentation.game.components.BadgeShowcase
import com.cocode.battleship.presentation.game.components.RankScorePanel
import com.cocode.battleship.presentation.game.components.SessionFooter
import com.cocode.battleship.presentation.game.components.StatsBreakdownPanel
import com.cocode.battleship.ui.theme.DeepNavy
import com.cocode.battleship.ui.theme.NavyCard
import com.cocode.battleship.ui.theme.NavySurface
import com.cocode.battleship.ui.theme.PhosphorGreen
import com.cocode.battleship.ui.theme.PhosphorGreenDim
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TextSecondary
import com.cocode.battleship.ui.theme.TorpedoRed
import com.cocode.battleship.ui.theme.TorpedoRedDim

@Composable
fun GameOverScreen(
    viewModel: GameViewModel,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val winner = state.winner ?: ""
    val isPlayerWinner = winner == "Player"
    val scoreResult = state.scoreResult
    val accentColor = if (isPlayerWinner) PhosphorGreen else TorpedoRed
    val accentDim = if (isPlayerWinner) PhosphorGreenDim else TorpedoRedDim
    val statusLabel = stringResource(if (isPlayerWinner) R.string.game_over_status_victory else R.string.game_over_status_defeat)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepNavy, NavySurface, DeepNavy))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isPlayerWinner) "◉" else "✕",
                fontSize = 48.sp,
                color = accentColor,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isPlayerWinner) stringResource(R.string.game_over_you_win)
                else stringResource(R.string.game_over_ai_wins),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = accentColor,
                letterSpacing = 2.sp,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = statusLabel,
                style = MaterialTheme.typography.labelMedium,
                color = accentColor.copy(alpha = 0.6f),
                letterSpacing = 3.sp,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)), RoundedCornerShape(4.dp))
                    .background(accentDim.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = if (isPlayerWinner) stringResource(R.string.game_over_win_subtitle)
                    else stringResource(R.string.game_over_lose_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (scoreResult != null) {
                Spacer(modifier = Modifier.height(16.dp))
                RankScorePanel(scoreResult = scoreResult, isPlayerWinner = isPlayerWinner)
                Spacer(modifier = Modifier.height(8.dp))
                StatsBreakdownPanel(stats = scoreResult.stats)
                Spacer(modifier = Modifier.height(8.dp))
                BadgeShowcase(badges = scoreResult.earnedBadges)
                Spacer(modifier = Modifier.height(8.dp))
                SessionFooter(
                    gamesPlayed = SessionStats.gamesPlayed,
                    totalWins = SessionStats.totalWins,
                    currentWinStreak = SessionStats.currentWinStreak,
                    bestScore = SessionStats.bestScore
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SonarCyan,
                    contentColor = DeepNavy,
                )
            ) {
                Text(
                    text = stringResource(R.string.game_over_play_again),
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    fontSize = 12.sp,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onMainMenu,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, SonarCyan.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SonarCyan),
            ) {
                Text(
                    text = stringResource(R.string.game_over_main_menu),
                    letterSpacing = 2.sp,
                    fontSize = 12.sp,
                )
            }
        }
    }
}
