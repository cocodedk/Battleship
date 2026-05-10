package com.cocode.battleship.presentation.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.domain.scoring.Badge
import com.cocode.battleship.presentation.SYM_SECTION
import com.cocode.battleship.presentation.medals.MedalCanvas
import com.cocode.battleship.ui.theme.NavyBorder
import com.cocode.battleship.ui.theme.NavyCard
import com.cocode.battleship.ui.theme.TextSecondary


@Composable
fun MedalsEarnedSection(earnedBadges: List<Badge>) {
    if (earnedBadges.isEmpty()) return
    val badgeCounts = earnedBadges.groupingBy { it }.eachCount()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyCard, RoundedCornerShape(4.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = "$SYM_SECTION  ${stringResource(R.string.game_over_medals_earned_title)}",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            badgeCounts.forEach { (badge, count) ->
                MedalCanvas(
                    badge = badge,
                    count = count,
                    modifier = Modifier.size(56.dp)
                )
            }
        }
    }
}
