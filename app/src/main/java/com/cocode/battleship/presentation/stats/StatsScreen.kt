package com.cocode.battleship.presentation.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.domain.scoring.Rank
import com.cocode.battleship.presentation.game.SessionStats
import com.cocode.battleship.ui.theme.AmberWarning
import com.cocode.battleship.ui.theme.DeepNavy
import com.cocode.battleship.ui.theme.NavyBorder
import com.cocode.battleship.ui.theme.NavyCard
import com.cocode.battleship.ui.theme.NavySurface
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TextDim
import com.cocode.battleship.ui.theme.TextSecondary

private const val SYM_SECTION = "◆"

@Composable
fun StatsScreen(onBack: () -> Unit) {
    val noGames = SessionStats.gamesPlayed == 0
    val rank = Rank.fromScore(SessionStats.bestScore)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepNavy, NavySurface, DeepNavy)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.stats_title),
                style = MaterialTheme.typography.titleLarge,
                color = SonarCyan,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.ExtraBold,
            )

            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.stats_naval_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary.copy(alpha = 0.5f),
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.height(20.dp))

            if (noGames) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavyCard, RoundedCornerShape(4.dp))
                        .border(1.dp, NavyBorder, RoundedCornerShape(4.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.stats_no_games),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDim,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                RankBanner(rank = rank)
                Spacer(Modifier.height(12.dp))
                SessionStatsPanel()
                Spacer(Modifier.height(12.dp))
                LifetimeCombatPanel(totalShots = SessionStats.totalShotsLifetime, totalHits = SessionStats.totalHitsLifetime)
                Spacer(Modifier.height(12.dp))
                BadgesEarnedPanel(earnedBadges = SessionStats.allEarnedBadges)
            }

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, SonarCyan.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SonarCyan),
            ) {
                Text(
                    text = stringResource(R.string.stats_back),
                    letterSpacing = 2.sp,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun RankBanner(rank: Rank) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AmberWarning.copy(alpha = 0.07f), RoundedCornerShape(4.dp))
            .border(1.dp, AmberWarning.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
            .padding(vertical = 14.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "$SYM_SECTION  ${stringResource(R.string.stats_rank_label)}",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            letterSpacing = 3.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = rank.displayName.uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            color = AmberWarning,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
        )
        Text(
            text = stringResource(R.string.stats_best_score_value, SessionStats.bestScore),
            style = MaterialTheme.typography.labelSmall,
            color = AmberWarning.copy(alpha = 0.6f),
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun SessionStatsPanel() {
    val winRate = if (SessionStats.gamesPlayed > 0) SessionStats.totalWins * 100 / SessionStats.gamesPlayed else 0
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyCard, RoundedCornerShape(4.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "$SYM_SECTION  ${stringResource(R.string.stats_session_title)}",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = stringResource(R.string.stats_games_played), value = "${SessionStats.gamesPlayed}")
            StatItem(label = stringResource(R.string.stats_wins), value = "${SessionStats.totalWins}")
            StatItem(label = stringResource(R.string.stats_win_rate), value = "$winRate%")
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = stringResource(R.string.stats_current_streak), value = "${SessionStats.currentWinStreak}")
            StatItem(label = stringResource(R.string.stats_longest_streak), value = "${SessionStats.longestWinStreak}")
        }
    }
}

