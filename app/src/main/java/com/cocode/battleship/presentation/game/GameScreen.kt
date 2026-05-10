package com.cocode.battleship.presentation.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.domain.model.GamePhase
import com.cocode.battleship.domain.model.SuperWeapon
import com.cocode.battleship.presentation.components.BattleGrid
import com.cocode.battleship.presentation.game.components.WeaponSelector
import com.cocode.battleship.ui.theme.AmberWarning
import com.cocode.battleship.ui.theme.NavyCard
import com.cocode.battleship.ui.theme.PhosphorGreen
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.presentation.SYM_ARROW
import com.cocode.battleship.presentation.SYM_SECTION
import com.cocode.battleship.ui.theme.TextSecondary
import kotlinx.coroutines.delay

internal fun weaponSelectorVisible(availableWeapons: List<SuperWeapon>): Boolean =
    availableWeapons.isNotEmpty()

@Composable
fun GameScreen(viewModel: GameViewModel, onGameOver: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val weaponHaptics = remember(context) { WeaponHaptics(context.applicationContext) }

    LaunchedEffect(state.phase) {
        if (state.phase == GamePhase.GAME_OVER) {
            if (state.activeWeaponEffect != null) delay(650)
            onGameOver()
        }
    }

    LaunchedEffect(state.activeWeaponEffect?.triggerId) {
        state.activeWeaponEffect?.let { weaponHaptics.perform(it.weapon) }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(700, easing = LinearEasing), RepeatMode.Reverse),
        label = "blink_alpha"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status message bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(NavyCard)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(R.string.game_message_format, state.message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SonarCyan.copy(alpha = 0.9f),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Turn indicator
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                val indicatorBg = if (state.isPlayerTurn) PhosphorGreen.copy(alpha = 0.15f) else AmberWarning.copy(alpha = 0.1f)
                val indicatorBorder = if (state.isPlayerTurn) PhosphorGreen else AmberWarning
                val indicatorText = if (state.isPlayerTurn) stringResource(R.string.game_your_turn)
                else stringResource(R.string.game_ai_thinking)
                val indicatorColor = if (state.isPlayerTurn) PhosphorGreen
                else AmberWarning.copy(alpha = blinkAlpha)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(indicatorBg)
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "$SYM_ARROW  $indicatorText",
                        color = indicatorColor,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                    )
                }
            }

            AnimatedVisibility(
                visible = weaponSelectorVisible(state.availableWeapons),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    WeaponSelector(
                        available = state.availableWeapons,
                        selected = state.selectedWeapon,
                        onSelect = { viewModel.selectWeapon(it) },
                        enabled = state.isPlayerTurn,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SectionLabel(text = stringResource(R.string.game_enemy_waters))

            BattleGrid(
                board = state.aiBoard,
                showShips = false,
                onCellClick = if (state.isPlayerTurn) { r, c -> viewModel.playerAttack(r, c) } else null,
                allowAttackedClicks = state.selectedWeapon != null,
                weaponEffect = state.activeWeaponEffect,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            SectionLabel(text = stringResource(R.string.game_your_fleet))

            BattleGrid(
                board = state.playerBoard,
                showShips = true,
                onCellClick = null,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = "$SYM_SECTION  $text",
        style = MaterialTheme.typography.titleMedium,
        color = TextSecondary,
        letterSpacing = 1.5.sp,
        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
    )
}
