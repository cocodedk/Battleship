package com.cocode.battleship.presentation.badges

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.cocode.battleship.presentation.medals.rarityColor
import com.cocode.battleship.ui.theme.DeepNavy
import com.cocode.battleship.ui.theme.NavySurface
import com.cocode.battleship.ui.theme.PhosphorGreen
import com.cocode.battleship.presentation.SYM_ARROW
import com.cocode.battleship.presentation.SYM_SECTION
import com.cocode.battleship.ui.theme.SonarCyan
import java.util.Locale

@Composable
fun BadgesScreen(viewModel: BadgesViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepNavy, NavySurface, DeepNavy)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            BadgesHeader(earnedCount = state.earnedCount, totalCount = state.totalCount)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.items) { item ->
                    BadgeCell(item = item, onClick = { viewModel.selectItem(item) })
                }
            }
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, SonarCyan.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SonarCyan),
            ) {
                Text(
                    text = stringResource(R.string.badges_back),
                    letterSpacing = 2.sp,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }

    state.selectedItem?.let { item ->
        BadgeDetailSheet(item = item, onDismiss = { viewModel.selectItem(null) })
    }
}

@Composable
private fun BadgesHeader(earnedCount: Int, totalCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "$SYM_SECTION  ${stringResource(R.string.badges_title)}",
            style = MaterialTheme.typography.titleLarge,
            color = SonarCyan,
            letterSpacing = 3.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "$SYM_ARROW  ${stringResource(R.string.badges_earned_format, earnedCount, totalCount)}",
            style = MaterialTheme.typography.labelMedium,
            color = PhosphorGreen,
            letterSpacing = 1.5.sp,
        )
    }
}

@Composable
private fun BadgeCell(item: BadgeItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        BadgeCanvas(
            badge = item.badge,
            count = item.count,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = item.badge.displayName.uppercase(Locale.ROOT),
            fontSize = 8.sp,
            letterSpacing = 0.4.sp,
            color = rarityColor(item.badge.rarity).copy(alpha = if (item.isEarned) 0.88f else 0.28f),
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 10.sp,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
