# Action Splash Screen Design

**Date:** 2026-04-12
**Branch:** fix/persist-career-stats
**Status:** Approved

---

## Goal

Replace the static, low-energy menu splash screen with a cinematic, action-packed version that plays an entrance sequence every time the screen appears and keeps the background alive during idle. Naval command HUD aesthetic is retained and extended with color diversity.

---

## Section 1: Background & Composition

### MenuBackground (new file)

`MenuBackground.kt` is a full-screen Canvas composable rendered as the lowest layer behind all UI elements.

**Layers (bottom to top):**

1. **Radial gradient fill** — same `NavySurface → DeepNavy` as before, drawn as a full rect
2. **Radar sweep** — a single `PhosphorGreen` line rotates from screen center, 360° per 4 seconds (`LinearEasing`, infinite). Trail rendered as 3 overlapping lines at 100% / 40% / 10% alpha
3. **Particle field** — 25 dots, 1–2dp radius, drifting upward at slightly different speeds per column. Position derived deterministically from a single animated `time` float (0→1000, 20s loop) + particle index. No per-particle `Animatable`. Colors distributed across: `SonarCyan`, `PhosphorGreen`, `AmberWarning`, `TorpedoRed` at 8–20% alpha, assigned by `index % 4`

### Typography

- Title "BATTLESHIP": **48sp ExtraBold** (was 36sp), `SonarCyan`, `TextStyle.shadow = Shadow(SonarCyan.copy(alpha=0.5f), blurRadius=24f)`
- Subtitle/tagline: `PhosphorGreen.copy(alpha=0.8f)` (was `TextSecondary` grey)

### HUD Corners

- Top-left + top-right: `SonarCyan.copy(alpha=0.75f)` (was 0.4f uniform)
- Bottom-left + bottom-right: `PhosphorGreen.copy(alpha=0.75f)` (was 0.4f uniform)

### START MISSION Button

- Retains existing `SonarCyan` fill + `DeepNavy` text
- Gains an outer glow ring: `Box` with a `drawBehind` that draws a blurred circle in `AmberWarning.copy(alpha = blinkAlpha * 0.35f)` — throbs using the existing `blinkAlpha` infinite animation

---

## Section 2: Cinematic Entry Sequence

Fires every time the menu screen appears via `LaunchedEffect(Unit)`.

### Timeline

```
  0ms  Screen renders: background visible, all foreground elements invisible
  0ms  Static flash overlay: alpha animates 0.0 → 0.6 → 0.0 over 80ms (white, full-screen)
 80ms  Title: scale 2f → 1f (spring, stiffness=High, dampingRatio=0.5) + alpha 0→1 over 200ms
300ms  Reticle reveal: MenuLogo `reveal` float animates 0→1 via tween(300ms)
500ms  Subtitle: alpha 0→1 + translateY 12dp→0dp via tween(200ms)
650ms  START MISSION: translateX +100dp→0dp + alpha 0→1 via tween(180ms)
750ms  CAREER STATS:  translateX +100dp→0dp + alpha 0→1 via tween(180ms)
900ms  All settled — idle animations run indefinitely
```

### MenuEntryState

`MenuEntryState.kt` exposes:

```kotlin
data class MenuEntryState(
    val staticFlashAlpha: Float,   // 0f when settled
    val titleScale: Float,         // 1f when settled
    val titleAlpha: Float,         // 1f when settled
    val reticleReveal: Float,      // 0→1, passed to MenuLogo
    val subtitleAlpha: Float,      // 1f when settled
    val subtitleOffsetY: Dp,       // 0.dp when settled
    val primaryButtonAlpha: Float, // 1f when settled
    val primaryButtonOffsetX: Dp,  // 0.dp when settled
    val secondaryButtonAlpha: Float,
    val secondaryButtonOffsetX: Dp,
)

@Composable
fun rememberMenuEntryState(): MenuEntryState
```

`rememberMenuEntryState()` creates `Animatable` instances and fires the sequence in a `LaunchedEffect(Unit)`. Returns a snapshot data class each recomposition.

---

## Section 3: File Architecture

| Action | File | Responsibility | Est. lines |
|--------|------|----------------|-----------|
| Create | `presentation/menu/MenuBackground.kt` | Radar sweep + particle field Canvas | ~120 |
| Create | `presentation/menu/MenuEntryState.kt` | Entry animation state + sequence | ~80 |
| Modify | `presentation/menu/MenuLogo.kt` | Add `reveal: Float` param for ring draw-in | ~100 |
| Modify | `presentation/menu/MenuScreen.kt` | Layout, entry state wiring, color updates | ≤200 |
| No change | `presentation/menu/MenuFooter.kt` | Attribution footer | — |

### MenuLogo change

Add `reveal: Float` parameter (0f = nothing drawn, 1f = fully drawn). Each ring's sweep angle is `reveal * 360f`. The ship silhouette is drawn only when `reveal >= 0.8f` (alpha `((reveal - 0.8f) / 0.2f).coerceIn(0f, 1f)`).

### MenuScreen layout structure

```
Box(fillMaxSize) {
    MenuBackground()           // bottom layer: gradient + radar + particles
    HudCorners()               // corner brackets (cyan top, green bottom)
    Column(center) {
        MenuLogo(reveal = entryState.reticleReveal, ...)
        Title(scale + alpha from entryState)
        Subtitle(alpha + offsetY from entryState)
        Spacer
        PrimaryButton(alpha + offsetX from entryState, glow pulse)
        SecondaryButton(alpha + offsetX from entryState)
        Spacer
        GameModeLabel
        MenuFooter
    }
    StaticFlashOverlay(alpha = entryState.staticFlashAlpha)  // top layer
}
```

---

## Constraints

- All files ≤ 200 lines
- No new Gradle dependencies — existing Compose animation APIs only
- Respects `prefersReducedMotion`: if `ANIMATOR_DURATION_SCALE == 0`, skip all `LaunchedEffect` sequences (jump to settled state immediately) and disable background animations
- No hardcoded strings added — no `strings.xml` changes needed
- `MenuFooter.kt` untouched
