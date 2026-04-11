package com.cocode.battleship.presentation.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.domain.scoring.GameStats
import com.cocode.battleship.ui.theme.NavyBorder
import com.cocode.battleship.ui.theme.NavyCard
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TextSecondary

@Composable
fun StatsBreakdownPanel(stats: GameStats) {
    val accuracyPct = (stats.accuracy * 100).toInt()
    val rows = listOf(
        "Shots" to stats.totalShots.toString(),
        "Hits" to stats.hits.toString(),
        "Misses" to stats.misses.toString(),
        "Accuracy" to "$accuracyPct%",
        "Ships Sunk" to stats.shipsSunkByPlayer.toString(),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyCard, RoundedCornerShape(4.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = "COMBAT STATS",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        rows.forEach { (label, value) ->
            StatRow(label = label, value = value)
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = SonarCyan,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
        )
    }
}
