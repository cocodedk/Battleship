package com.cocode.battleship.presentation.menu

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MenuEntryState(
    val staticFlashAlpha: Float = 0f,
    val titleScale: Float = 1f,
    val titleAlpha: Float = 1f,
    val reticleReveal: Float = 1f,
    val subtitleAlpha: Float = 1f,
    val subtitleOffsetY: Dp = 0.dp,
    val primaryButtonAlpha: Float = 1f,
    val primaryButtonOffsetX: Dp = 0.dp,
    val secondaryButtonAlpha: Float = 1f,
    val secondaryButtonOffsetX: Dp = 0.dp,
)

@Composable
fun rememberMenuEntryState(prefersReducedMotion: Boolean): MenuEntryState {
    if (prefersReducedMotion) return MenuEntryState()

    val staticFlashAlpha = remember { Animatable(0f) }
    val titleScale = remember { Animatable(2f) }
    val titleAlpha = remember { Animatable(0f) }
    val reticleReveal = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val subtitleOffsetY = remember { Animatable(12f) }
    val primaryButtonAlpha = remember { Animatable(0f) }
    val primaryButtonOffsetX = remember { Animatable(100f) }
    val secondaryButtonAlpha = remember { Animatable(0f) }
    val secondaryButtonOffsetX = remember { Animatable(100f) }

    LaunchedEffect(Unit) {
        // Phase 1: static flash 0–80ms
        launch {
            staticFlashAlpha.animateTo(0.6f, tween(40))
            staticFlashAlpha.animateTo(0f, tween(40))
        }
        delay(80)

        // Phase 2: title slams in at 80ms
        launch { titleAlpha.animateTo(1f, tween(200)) }
        launch {
            titleScale.animateTo(
                1f,
                spring(stiffness = Spring.StiffnessHigh, dampingRatio = 0.5f)
            )
        }
        delay(220) // now at 300ms

        // Phase 3: reticle rings draw in at 300ms
        launch { reticleReveal.animateTo(1f, tween(300)) }
        delay(200) // now at 500ms

        // Phase 4: subtitle scans in at 500ms
        launch { subtitleAlpha.animateTo(1f, tween(200)) }
        launch { subtitleOffsetY.animateTo(0f, tween(200)) }
        delay(150) // now at 650ms

        // Phase 5: primary button slides in at 650ms
        launch { primaryButtonAlpha.animateTo(1f, tween(180)) }
        launch { primaryButtonOffsetX.animateTo(0f, tween(180)) }
        delay(100) // now at 750ms

        // Phase 6: secondary button slides in at 750ms
        launch { secondaryButtonAlpha.animateTo(1f, tween(180)) }
        launch { secondaryButtonOffsetX.animateTo(0f, tween(180)) }
    }

    return MenuEntryState(
        staticFlashAlpha = staticFlashAlpha.value,
        titleScale = titleScale.value,
        titleAlpha = titleAlpha.value,
        reticleReveal = reticleReveal.value,
        subtitleAlpha = subtitleAlpha.value,
        subtitleOffsetY = subtitleOffsetY.value.dp,
        primaryButtonAlpha = primaryButtonAlpha.value,
        primaryButtonOffsetX = primaryButtonOffsetX.value.dp,
        secondaryButtonAlpha = secondaryButtonAlpha.value,
        secondaryButtonOffsetX = secondaryButtonOffsetX.value.dp,
    )
}
