# Action Splash Screen Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the static menu splash with a cinematic 900ms entry sequence, animated radar+particle background, color-diverse HUD, and a glowing START MISSION button.

**Architecture:** Four composable files handle distinct concerns: `MenuBackground` draws the animated canvas layer (gradient + radar sweep + particles + HUD corners), `MenuEntryState` manages the staggered `Animatable` entry sequence, `MenuLogo` gains a `reveal: Float` param to animate ring draw-in, and `MenuScreen` wires everything together. All files stay ≤ 200 lines.

**Tech Stack:** Kotlin 2.2.10, Jetpack Compose (Material3 BOM 2026.02.01), `Animatable`, `spring()`, `tween()`, `rememberInfiniteTransition` — no new Gradle dependencies.

---

## File Map

| Action | Path | Responsibility |
|--------|------|----------------|
| Create | `app/src/main/java/com/cocode/battleship/presentation/menu/MenuEntryState.kt` | Entry animation state + 900ms sequence |
| Create | `app/src/main/java/com/cocode/battleship/presentation/menu/MenuBackground.kt` | Gradient + radar sweep + particles + HUD corners |
| Modify | `app/src/main/java/com/cocode/battleship/presentation/menu/MenuLogo.kt` | Add `reveal: Float` param for ring draw-in |
| Modify | `app/src/main/java/com/cocode/battleship/presentation/menu/MenuScreen.kt` | Layout rewire, entry state, colors, glow button |

---

## Task 1 — MenuEntryState.kt

**Files:**
- Create: `app/src/main/java/com/cocode/battleship/presentation/menu/MenuEntryState.kt`

No unit test: pure Compose animation state, no business logic.

- [ ] **Step 1.1: Create the file**

```kotlin
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
```

- [ ] **Step 1.2: Compile**

```bash
cd /home/cocodedk/0-projects/Battleship && ./gradlew :app:compileDebugKotlin 2>&1 | tail -5
```

Expected: `BUILD SUCCESSFUL`

---

## Task 2 — MenuBackground.kt

**Files:**
- Create: `app/src/main/java/com/cocode/battleship/presentation/menu/MenuBackground.kt`

`MenuBackground` renders the full-screen animated canvas layer AND the HUD corner brackets (moving them out of `MenuScreen.kt` to keep line counts down).

- [ ] **Step 2.1: Create the file**

```kotlin
package com.cocode.battleship.presentation.menu

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.cocode.battleship.ui.theme.AmberWarning
import com.cocode.battleship.ui.theme.DeepNavy
import com.cocode.battleship.ui.theme.NavySurface
import com.cocode.battleship.ui.theme.PhosphorGreen
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TorpedoRed
import kotlin.math.cos
import kotlin.math.sin

private val PARTICLE_COLORS = listOf(SonarCyan, PhosphorGreen, AmberWarning, TorpedoRed)
private const val PARTICLE_COUNT = 25

@Composable
fun MenuBackground(animate: Boolean, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")

    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "radar"
    )
    val particleTime by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Restart),
        label = "particles"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Gradient background
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(NavySurface, DeepNavy),
                center = center,
                radius = size.maxDimension * 0.7f
            )
        )

        if (!animate) return@Canvas

        // Radar sweep — PhosphorGreen with 3-line trail
        val sweepRadius = size.maxDimension
        val sw = 1.5.dp.toPx()
        listOf(0f to 1.0f, -8f to 0.4f, -18f to 0.1f).forEach { (offset, alpha) ->
            val rad = Math.toRadians((radarAngle + offset).toDouble())
            drawLine(
                color = PhosphorGreen.copy(alpha = (alpha * 0.6f)),
                start = center,
                end = Offset(center.x + sweepRadius * cos(rad).toFloat(),
                             center.y + sweepRadius * sin(rad).toFloat()),
                strokeWidth = sw, cap = StrokeCap.Round
            )
        }

        // Particle field — 25 dots drifting upward
        for (i in 0 until PARTICLE_COUNT) {
            val col = i.toFloat() / PARTICLE_COUNT
            val speed = 0.3f + (i % 5) * 0.1f
            val x = col * size.width
            val y = (size.height - (i * size.height / PARTICLE_COUNT +
                     particleTime * speed * 0.001f * size.height)) % size.height
            drawCircle(
                color = PARTICLE_COLORS[i % 4].copy(alpha = 0.08f + (i % 3) * 0.04f),
                radius = (1f + (i % 3) * 0.5f).dp.toPx(),
                center = Offset(x, (y + size.height) % size.height)
            )
        }

        // HUD corner brackets — cyan top, green bottom
        val arm = 28.dp.toPx(); val margin = 20.dp.toPx(); val capSq = StrokeCap.Square
        val cyan = SonarCyan.copy(alpha = 0.75f); val green = PhosphorGreen.copy(alpha = 0.75f)
        // Top-left (cyan)
        drawLine(cyan, Offset(margin, margin + arm), Offset(margin, margin), sw, capSq)
        drawLine(cyan, Offset(margin, margin), Offset(margin + arm, margin), sw, capSq)
        // Top-right (cyan)
        drawLine(cyan, Offset(size.width - margin, margin + arm), Offset(size.width - margin, margin), sw, capSq)
        drawLine(cyan, Offset(size.width - margin, margin), Offset(size.width - margin - arm, margin), sw, capSq)
        // Bottom-left (green)
        drawLine(green, Offset(margin, size.height - margin - arm), Offset(margin, size.height - margin), sw, capSq)
        drawLine(green, Offset(margin, size.height - margin), Offset(margin + arm, size.height - margin), sw, capSq)
        // Bottom-right (green)
        drawLine(green, Offset(size.width - margin, size.height - margin - arm), Offset(size.width - margin, size.height - margin), sw, capSq)
        drawLine(green, Offset(size.width - margin, size.height - margin), Offset(size.width - margin - arm, size.height - margin), sw, capSq)
    }
}
```

- [ ] **Step 2.2: Compile**

```bash
cd /home/cocodedk/0-projects/Battleship && ./gradlew :app:compileDebugKotlin 2>&1 | tail -5
```

Expected: `BUILD SUCCESSFUL`

---

## Task 3 — MenuLogo.kt: add reveal param

**Files:**
- Modify: `app/src/main/java/com/cocode/battleship/presentation/menu/MenuLogo.kt`

Add `reveal: Float` (0f = nothing drawn, 1f = fully drawn). Rings appear sequentially: inner at reveal≥0.1, mid at reveal≥0.35, outer at reveal≥0.6, crosshairs at reveal≥0.75, ship at reveal≥0.85.

- [ ] **Step 3.1: Replace the entire file**

```kotlin
package com.cocode.battleship.presentation.menu

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.cocode.battleship.ui.theme.SonarCyan

private fun revealAlpha(reveal: Float, threshold: Float): Float =
    ((reveal - threshold) / (1f - threshold)).coerceIn(0f, 1f)

@Composable
fun MenuLogo(
    pulseScale: Float,
    pulseAlpha: Float,
    reveal: Float = 1f,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val outerR = size.minDimension / 2f * 0.82f
        val innerR = outerR * 0.34f
        val midR = outerR * 0.65f
        val stroke15 = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        val stroke10 = Stroke(width = 1.dp.toPx())
        val stroke08 = Stroke(width = 0.8.dp.toPx())
        val center = Offset(cx, cy)

        // Sonar pulse ring (animated)
        if (pulseAlpha > 0f) {
            drawCircle(
                color = SonarCyan.copy(alpha = pulseAlpha * 0.35f * revealAlpha(reveal, 0.6f)),
                radius = outerR * pulseScale.coerceAtLeast(0.1f),
                center = center, style = stroke15
            )
        }

        // Inner ring
        val innerAlpha = revealAlpha(reveal, 0.1f)
        if (innerAlpha > 0f)
            drawCircle(SonarCyan.copy(alpha = 0.35f * innerAlpha), innerR, center, style = stroke08)

        // Mid ring
        val midAlpha = revealAlpha(reveal, 0.35f)
        if (midAlpha > 0f)
            drawCircle(SonarCyan.copy(alpha = 0.50f * midAlpha), midR, center, style = stroke10)

        // Outer ring
        val outerAlpha = revealAlpha(reveal, 0.6f)
        if (outerAlpha > 0f)
            drawCircle(SonarCyan.copy(alpha = 0.85f * outerAlpha), outerR, center, style = stroke15)

        // Crosshairs + tick marks
        val crosshairAlpha = revealAlpha(reveal, 0.75f)
        if (crosshairAlpha > 0f) {
            val gap = innerR + 3.dp.toPx()
            val lineColor = SonarCyan.copy(alpha = 0.70f * crosshairAlpha)
            val tickColor = SonarCyan.copy(alpha = 0.9f * crosshairAlpha)
            val tickLen = 5.dp.toPx()
            drawLine(lineColor, Offset(cx - outerR, cy), Offset(cx - gap, cy), stroke15.width, StrokeCap.Round)
            drawLine(lineColor, Offset(cx + gap, cy),   Offset(cx + outerR, cy), stroke15.width, StrokeCap.Round)
            drawLine(lineColor, Offset(cx, cy - outerR), Offset(cx, cy - gap), stroke15.width, StrokeCap.Round)
            drawLine(lineColor, Offset(cx, cy + gap),   Offset(cx, cy + outerR), stroke15.width, StrokeCap.Round)
            drawLine(tickColor, Offset(cx, cy - outerR - tickLen), Offset(cx, cy - outerR + tickLen / 2), stroke15.width)
            drawLine(tickColor, Offset(cx, cy + outerR - tickLen / 2), Offset(cx, cy + outerR + tickLen), stroke15.width)
            drawLine(tickColor, Offset(cx - outerR - tickLen, cy), Offset(cx - outerR + tickLen / 2, cy), stroke15.width)
            drawLine(tickColor, Offset(cx + outerR - tickLen / 2, cy), Offset(cx + outerR + tickLen, cy), stroke15.width)
        }

        // Ship silhouette
        val shipAlpha = revealAlpha(reveal, 0.85f)
        if (shipAlpha > 0f) {
            val hw = innerR * 0.52f; val hf = innerR * 0.88f
            val bowLen = innerR * 0.42f; val sternW = innerR * 0.32f
            val shipPath = Path().apply {
                moveTo(cx, cy - hf - bowLen)
                lineTo(cx + hw, cy - hf + hf * 0.18f)
                lineTo(cx + hw, cy + hf * 0.75f)
                lineTo(cx + sternW, cy + hf + bowLen * 0.25f)
                lineTo(cx - sternW, cy + hf + bowLen * 0.25f)
                lineTo(cx - hw, cy + hf * 0.75f)
                lineTo(cx - hw, cy - hf + hf * 0.18f)
                close()
            }
            drawPath(shipPath, SonarCyan.copy(alpha = 0.90f * shipAlpha))
            val bw = hw * 0.65f
            val bridgePath = Path().apply {
                moveTo(cx - bw, cy - hf * 0.30f); lineTo(cx + bw, cy - hf * 0.30f)
                lineTo(cx + bw, cy + hf * 0.28f); lineTo(cx - bw, cy + hf * 0.28f); close()
            }
            drawPath(bridgePath, androidx.compose.ui.graphics.Color(0xFF091628))
        }
    }
}
```

- [ ] **Step 3.2: Compile**

```bash
cd /home/cocodedk/0-projects/Battleship && ./gradlew :app:compileDebugKotlin 2>&1 | tail -5
```

Expected: `BUILD SUCCESSFUL`

---

## Task 4 — MenuScreen.kt: full rewire

**Files:**
- Modify: `app/src/main/java/com/cocode/battleship/presentation/menu/MenuScreen.kt`

Replace with the complete updated version below. `HudCorners` is removed (now inside `MenuBackground`). Background gradient moved to `MenuBackground`. Entry state applied to each element.

- [ ] **Step 4.1: Replace the entire file**

```kotlin
package com.cocode.battleship.presentation.menu

import android.provider.Settings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.ui.theme.AmberWarning
import com.cocode.battleship.ui.theme.DeepNavy
import com.cocode.battleship.ui.theme.PhosphorGreen
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TextDim
import com.cocode.battleship.ui.theme.TextSecondary

@Composable
fun MenuScreen(onStartGame: () -> Unit, onViewStats: () -> Unit = {}) {
    val context = LocalContext.current
    val prefersReducedMotion = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }

    val infiniteTransition = rememberInfiniteTransition(label = "menu")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(700, easing = LinearEasing), RepeatMode.Reverse),
        label = "blink"
    )
    val pulseScale by infiniteTransition.animateFloat(
        0.2f, 2.0f,
        infiniteRepeatable(tween(2800, easing = LinearEasing), RepeatMode.Restart),
        label = "scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        0.5f, 0f,
        infiniteRepeatable(tween(2800, easing = LinearEasing), RepeatMode.Restart),
        label = "alpha"
    )

    val entry = rememberMenuEntryState(prefersReducedMotion)
    val effectivePulse = if (prefersReducedMotion) 0f else pulseAlpha
    val effectiveScale = if (prefersReducedMotion) 0f else pulseScale

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MenuBackground(animate = !prefersReducedMotion)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            MenuLogo(
                pulseScale = effectiveScale,
                pulseAlpha = effectivePulse,
                reveal = entry.reticleReveal,
                modifier = Modifier.size(180.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.menu_title),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 48.sp,
                    letterSpacing = 2.sp,
                    shadow = Shadow(SonarCyan.copy(alpha = 0.5f), blurRadius = 24f)
                ),
                color = SonarCyan,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .scale(entry.titleScale)
                    .alpha(entry.titleAlpha)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.menu_system_label),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                letterSpacing = 3.sp,
                modifier = Modifier.alpha(entry.subtitleAlpha).offset(y = entry.subtitleOffsetY)
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = stringResource(R.string.menu_tagline).uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                color = PhosphorGreen.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp,
                modifier = Modifier.alpha(entry.subtitleAlpha).offset(y = entry.subtitleOffsetY)
            )

            Spacer(Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .alpha(entry.primaryButtonAlpha)
                    .offset(x = entry.primaryButtonOffsetX)
                    .drawBehind {
                        drawOval(
                            color = AmberWarning.copy(alpha = blinkAlpha * 0.35f),
                            style = Stroke(width = 10.dp.toPx())
                        )
                    }
            ) {
                Button(
                    onClick = onStartGame,
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SonarCyan, contentColor = DeepNavy)
                ) {
                    Text(
                        text = stringResource(R.string.menu_start_game),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 3.sp,
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onViewStats,
                modifier = Modifier
                    .fillMaxWidth().height(44.dp)
                    .alpha(entry.secondaryButtonAlpha)
                    .offset(x = entry.secondaryButtonOffsetX),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, SonarCyan.copy(alpha = 0.45f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SonarCyan),
            ) {
                Text(
                    text = stringResource(R.string.menu_view_stats),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.menu_game_mode),
                style = MaterialTheme.typography.labelSmall,
                color = TextDim,
                letterSpacing = 1.sp,
            )

            MenuFooter()
        }

        // Static flash overlay — top layer
        if (entry.staticFlashAlpha > 0f) {
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = entry.staticFlashAlpha)))
        }
    }
}
```

- [ ] **Step 4.2: Verify line count ≤ 200**

```bash
wc -l app/src/main/java/com/cocode/battleship/presentation/menu/MenuScreen.kt
```

Expected: ≤ 200

- [ ] **Step 4.3: Compile**

```bash
cd /home/cocodedk/0-projects/Battleship && ./gradlew :app:compileDebugKotlin 2>&1 | tail -5
```

Expected: `BUILD SUCCESSFUL`

---

## Task 5 — Verify and commit

- [ ] **Step 5.1: Run full test suite**

```bash
cd /home/cocodedk/0-projects/Battleship && ./gradlew test 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`, 128 tests, 0 failures.

- [ ] **Step 5.2: Commit**

```bash
git add \
  app/src/main/java/com/cocode/battleship/presentation/menu/MenuEntryState.kt \
  app/src/main/java/com/cocode/battleship/presentation/menu/MenuBackground.kt \
  app/src/main/java/com/cocode/battleship/presentation/menu/MenuLogo.kt \
  app/src/main/java/com/cocode/battleship/presentation/menu/MenuScreen.kt
git commit -m "$(cat <<'EOF'
feat: action splash screen with cinematic entry sequence

900ms staggered entry fires every visit: static flash → title slam
(spring) → reticle rings draw in → subtitle scan → buttons slide in.

Background: rotating PhosphorGreen radar sweep + 25-particle drift
field using 4 colors (cyan/green/amber/red). HUD corners: cyan top,
green bottom. Title 48sp ExtraBold with cyan glow shadow. Tagline in
PhosphorGreen. START MISSION gets an amber pulsing glow ring.

Respects ANIMATOR_DURATION_SCALE=0 (reduced motion): jumps to settled
state, disables background animations.

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>
EOF
)"
```
