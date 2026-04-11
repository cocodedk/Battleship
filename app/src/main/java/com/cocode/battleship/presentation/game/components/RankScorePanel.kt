package com.cocode.battleship.presentation.game.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.domain.scoring.Rank
import com.cocode.battleship.domain.scoring.ScoreResult
import com.cocode.battleship.ui.theme.BronzeGold
import com.cocode.battleship.ui.theme.NavyCard
import com.cocode.battleship.ui.theme.PhosphorGreen
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TextSecondary
import com.cocode.battleship.ui.theme.TorpedoRed

@Composable
fun RankScorePanel(scoreResult: ScoreResult, isPlayerWinner: Boolean) {
    val animatedScore by animateIntAsState(
        targetValue = scoreResult.score,
        animationSpec = tween(durationMillis = 1200),
        label = "score_anim"
    )

    val rankColor = rankColor(scoreResult.rank, isPlayerWinner)
    val borderColor = rankColor.copy(alpha = 0.5f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyCard, RoundedCornerShape(4.dp))
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.game_over_rank_label),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            letterSpacing = 3.sp,
        )
        Text(
            text = scoreResult.rank.displayName.uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = rankColor,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.game_over_score_label),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            letterSpacing = 3.sp,
        )
        Text(
            text = animatedScore.toString(),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = rankColor,
            textAlign = TextAlign.Center,
        )
    }
}

private fun rankColor(rank: Rank, isPlayerWinner: Boolean): Color = when {
    rank == Rank.FLEET_ADMIRAL -> BronzeGold
    isPlayerWinner -> PhosphorGreen
    rank.minScore < 700 -> TorpedoRed   // ENSIGN or CADET
    else -> SonarCyan
}
