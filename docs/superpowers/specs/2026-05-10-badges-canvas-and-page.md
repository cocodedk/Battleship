# Badges Canvas & Badges Page — Design Spec

## Goal

Replace emoji badge chips with custom-drawn canvas art, add a dedicated Badges screen (all 33 badges, earned + locked), show earned badges and medals separately on the game over screen, and add Badges to the main menu.

## Context

The app has two distinct collectible systems that must stay visually separate:

- **Medals** — hex-framed canvas art, tracked by lifetime count in `MedalsStorage`, shown in the Medal Registry screen.
- **Badges** — rarity-shaped canvas art (new), same underlying `Badge` enum, same count data from `MedalsStorage`, shown on the game over screen and the new Badges screen.

Both use the same `Badge` enum and the same `MedalsStorage` data source. The distinction is purely visual: frame shape and screen placement.

---

## 1. Badge Shape System

Badge frame shape is determined by rarity — a visual rarity signal:

| Rarity    | Frame shape | Color (`rarityColor()`)        |
|-----------|-------------|--------------------------------|
| COMMON    | Circle      | `Color(0xFFB87333)` — bronze   |
| RARE      | Shield      | `Color(0xFF7EB8D4)` — steel blue |
| EPIC      | Diamond     | `Color(0xFFFFD700)` — gold     |
| LEGENDARY | Star (5-pt) | `Color(0xFF00D4FF)` — bright cyan |

These are the same colors already used by `MedalCanvas`. They are extracted to a shared `RarityColors.kt` (see Section 3).

Interior art reuses `drawBadgeSymbol(badge, ...)` from `MedalSymbols.kt` (already `internal`).
Lock art reuses `drawLockSymbol(...)` moved to `MedalDrawingHelpers.kt` (extracted from `MedalCanvas.kt` private → `internal`).

Locked (unearned) badges render at 28% alpha (matching `MedalCanvas`). No count badge shown when `count == 0` (same behaviour as `MedalCanvas`).

---

## 2. New & Modified Files

### New files

| File | Purpose |
|------|---------|
| `presentation/medals/RarityColors.kt` | Shared `internal fun rarityColor(Rarity): Color` used by both `MedalCanvas` and `BadgeCanvas` |
| `presentation/badges/BadgeCanvas.kt` | Composable: rarity-based frame + interior art via `Canvas { }` (same pattern as `MedalCanvas`) |
| `presentation/badges/BadgesUiState.kt` | `BadgeItem(badge: Badge, count: Int)` + `BadgesUiState(items, selectedItem)` |
| `presentation/badges/BadgesViewModel.kt` | Reads `MedalsStorage` once on init; `StateFlow<BadgesUiState>`; `ViewModelProvider.Factory` companion |
| `presentation/badges/BadgesScreen.kt` | 3-column grid of all 33 badges, header with earned count, back button |
| `presentation/badges/BadgeDetailSheet.kt` | Bottom sheet on badge tap: `BadgeCanvas` (120dp), name, rarity, unlock hint, earned count |
| `presentation/game/components/MedalsEarnedSection.kt` | Composable showing `MedalCanvas` for unique medals earned this game (extracted to keep `GameOverScreen` under 200 lines) |

### Modified files

| File | Change |
|------|--------|
| `presentation/medals/MedalCanvas.kt` | Remove private `RarityCommon/Rare/Epic/Legendary` constants and `rarityColor()`. Remove private `drawLockSymbol()`. Import both from shared files. |
| `presentation/medals/MedalDrawingHelpers.kt` | Add `internal fun DrawScope.drawLockSymbol(...)` moved from `MedalCanvas.kt` |
| `presentation/game/components/BadgeShowcase.kt` | Remove `BadgeChip` (emoji). Add `onViewAllBadges: () -> Unit` parameter. Render `BadgeCanvas` (56dp) per badge. Fix section label. Add "VIEW ALL BADGES" button calling `onViewAllBadges()`. Remove private `rarityColor()`. |
| `presentation/game/GameOverScreen.kt` | Add `onBadges: () -> Unit` callback. Add `MedalsEarnedSection` call. Pass `onBadges` to `BadgeShowcase`. |
| `presentation/menu/MenuScreen.kt` | Add `onViewBadges: () -> Unit = {}` callback (matching existing `onViewStats`/`onViewMedals` pattern). Add BADGES button calling `onViewBadges()`. |
| `presentation/navigation/Screen.kt` | Add `data object Badges : Screen("badges")`. |
| `presentation/navigation/BattleshipNavHost.kt` | Wire `onViewBadges = { navController.navigate(Screen.Badges.route) }` in menu composable. Add badges route with `BadgesViewModel.factory(medalsStorage)`. Pass `onBadges = { navController.navigate(Screen.Badges.route) }` to `GameOverScreen`. |
| `app/src/main/res/values/strings.xml` | Modify and add strings (see Section 7). |

---

## 3. RarityColors.kt

```kotlin
package com.cocode.battleship.presentation.medals

import androidx.compose.ui.graphics.Color
import com.cocode.battleship.domain.scoring.Rarity

internal val RarityCommon    = Color(0xFFB87333)
internal val RarityRare      = Color(0xFF7EB8D4)
internal val RarityEpic      = Color(0xFFFFD700)
internal val RarityLegendary = Color(0xFF00D4FF)

internal fun rarityColor(rarity: Rarity): Color = when (rarity) {
    Rarity.COMMON    -> RarityCommon
    Rarity.RARE      -> RarityRare
    Rarity.EPIC      -> RarityEpic
    Rarity.LEGENDARY -> RarityLegendary
}
```

`MedalCanvas.kt` removes its private copy and imports this. `BadgeCanvas.kt` also imports this.

---

## 4. BadgeCanvas

```kotlin
@Composable
fun BadgeCanvas(badge: Badge, count: Int, modifier: Modifier = Modifier)
```

Uses `Canvas { }` — not `drawWithCache` — matching `MedalCanvas` pattern exactly.

### Frame paths (in DrawScope, scaled to `size`)

**Circle (COMMON)**
```kotlin
val r = size.minDimension * 0.46f
drawCircle(Color(0xFF071828).copy(alpha = drawAlpha), r, center)
drawCircle(c, r, center, style = strokeOuter)
drawCircle(c.copy(alpha = c.alpha * 0.45f), r * 0.82f, center, style = Stroke(1.dp.toPx()))
```

**Shield (RARE)**
```kotlin
fun shieldPath(w: Float, h: Float): Path = Path().apply {
    moveTo(w * 0.50f, h * 0.03f)
    lineTo(w * 0.97f, h * 0.22f)
    lineTo(w * 0.97f, h * 0.56f)
    cubicTo(w * 0.97f, h * 0.84f, w * 0.50f, h * 0.97f,
            w * 0.50f, h * 0.97f)
    cubicTo(w * 0.03f, h * 0.84f, w * 0.03f, h * 0.56f,
            w * 0.03f, h * 0.56f)
    lineTo(w * 0.03f, h * 0.22f)
    close()
}
```

**Diamond (EPIC)**
```kotlin
fun diamondPath(cx: Float, cy: Float, r: Float): Path = Path().apply {
    moveTo(cx,     cy - r)
    lineTo(cx + r, cy)
    lineTo(cx,     cy + r)
    lineTo(cx - r, cy)
    close()
}
// r = size.minDimension * 0.46f
```

**Star (LEGENDARY)**
```kotlin
fun starPath(cx: Float, cy: Float, outerR: Float, innerR: Float): Path {
    val path = Path()
    for (i in 0 until 10) {
        val angle = (i * PI / 5.0 - PI / 2.0).toFloat()
        val r = if (i % 2 == 0) outerR else innerR
        val x = cx + r * cos(angle)
        val y = cy + r * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}
// outerR = size.minDimension * 0.46f, innerR = size.minDimension * 0.20f
```

### Drawing logic (all shapes)

```kotlin
Canvas(modifier = Modifier.fillMaxSize()) {
    val cx = size.width / 2f; val cy = size.height / 2f
    val drawAlpha = if (count > 0) 1f else 0.28f
    val c = rarityColor(badge.rarity).copy(alpha = drawAlpha)
    val strokeOuter = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    // 1. Fill frame background (Color 0xFF071828 at drawAlpha)
    // 2. Stroke frame border outer + inner ring at 45% alpha
    // 3. if (count > 0) drawBadgeSymbol(badge, cx, cy, outerR * 0.42f, c)  // MedalSymbols.kt
    //    else drawLockSymbol(cx, cy, outerR * 0.42f, c)                      // MedalDrawingHelpers.kt
}
```

Count badge (`×N`) in `Box` overlay: shown only when `count > 0`, same styling as `MedalCanvas`.

---

## 5. Composable Signatures

```kotlin
// BadgeShowcase.kt
@Composable
fun BadgeShowcase(badges: List<Badge>, onViewAllBadges: () -> Unit)

// BadgesScreen.kt
@Composable
fun BadgesScreen(viewModel: BadgesViewModel, onBack: () -> Unit)

// BadgeDetailSheet.kt
@Composable
fun BadgeDetailSheet(item: BadgeItem, onDismiss: () -> Unit)
// onDismiss calls viewModel.selectItem(null) from the caller (same pattern as MedalDetailSheet)

// MedalsEarnedSection.kt
@Composable
fun MedalsEarnedSection(earnedBadges: List<Badge>)
// earnedBadges = scoreResult.earnedBadges — passed from GameOverScreen, not observed from ViewModel
```

`BadgeCanvas` applies the passed `modifier` to the outer `Box`, and `Canvas(modifier = Modifier.fillMaxSize())` fills that box — same structure as `MedalCanvas`.

---

## 5a. BadgesUiState

```kotlin
data class BadgeItem(val badge: Badge, val count: Int) {
    val isEarned: Boolean get() = count > 0
}

data class BadgesUiState(
    val items: List<BadgeItem>,
    val selectedItem: BadgeItem? = null,
) {
    val earnedCount: Int get() = items.count { it.isEarned }
    val totalCount: Int get() = items.size
}
```

`items` is ordered by `Badge.entries` order (same stable order as `Badge` enum declaration). All 33 badges are always present; count is 0 for unearned badges. Derived from `MedalsStorage.load()` which returns a `Map<Badge, Int>` — every badge not in the map gets count 0.

---

## 5b. BadgesViewModel

```kotlin
class BadgesViewModel(storage: MedalsStorage) : ViewModel() {
    private val _state = MutableStateFlow(buildState(storage.load()))
    val state: StateFlow<BadgesUiState> = _state.asStateFlow()

    fun selectItem(item: BadgeItem?) {
        _state.update { it.copy(selectedItem = item) }
    }

    private fun buildState(counts: Map<Badge, Int>): BadgesUiState {
        val items = Badge.entries.map { badge -> BadgeItem(badge, counts[badge] ?: 0) }
        return BadgesUiState(items)
    }
}
```

Storage is read once at construction — no live observation needed (same as `MedalsViewModel`).

---

## 5c. BadgesViewModel Factory Pattern

Mirrors `MedalsViewModel` exactly — uses `ViewModelProvider.Factory` anonymous object:

```kotlin
companion object {
    fun factory(storage: MedalsStorage): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return BadgesViewModel(storage) as T
            }
        }
}
```

Instantiated in `BattleshipNavHost`:
```kotlin
composable(Screen.Badges.route) {
    val vm: BadgesViewModel = viewModel(factory = BadgesViewModel.factory(medalsStorage))
    BadgesScreen(viewModel = vm, onBack = { navController.popBackStack() })
}
```

---

## 6. Badges Screen

Layout mirrors Medal Registry:

```
◆  BADGES                            ← SonarCyan header
▶  8 / 33 EARNED                     ← PhosphorGreen, R.string.badges_earned_format

[LazyVerticalGrid, GridCells.Fixed(3), weight(1f)]
  per cell: BadgeCanvas(72dp) + name label (8sp, rarityColor, 28% alpha if locked)

[RETURN TO BASE OutlinedButton]       ← R.string.badges_back
```

Tapping a cell calls `viewModel.selectItem(item)` → `BadgeDetailSheet` opens.

---

## 7. Game Over Screen

Updated signature:
```kotlin
fun GameOverScreen(
    viewModel: GameViewModel,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit,
    onBadges: () -> Unit,
)
```

Updated `BadgeShowcase` call:
```kotlin
BadgeShowcase(badges = scoreResult.earnedBadges, onViewAllBadges = onBadges)
```

Section order after stats:
1. `BadgeShowcase(badges, onViewAllBadges)` — `BadgeCanvas` (56dp) per badge; empty-state if none; "VIEW ALL BADGES" button at bottom
2. `MedalsEarnedSection(earnedBadges)` — shown only when `earnedBadges.isNotEmpty()`; `MedalCanvas` (56dp) for each distinct badge (`earnedBadges.distinct()`); count = `earnedBadges.count { it == badge }`

`MedalsEarnedSection` lives in `presentation/game/components/MedalsEarnedSection.kt` to keep `GameOverScreen.kt` under 200 lines.

---

## 8. String Resource Changes

```xml
<!-- MODIFY existing (currently wrong label) -->
<string name="game_over_badges_title">BADGES EARNED</string>
<string name="game_over_badges_none">No badges earned — keep fighting, sailor.</string>

<!-- ADD new -->
<string name="game_over_medals_earned_title">MEDALS EARNED</string>
<string name="game_over_view_all_badges">VIEW ALL BADGES</string>
<string name="menu_badges">BADGES</string>
<string name="badges_title">BADGES</string>
<string name="badges_earned_format">%1$d / %2$d EARNED</string>
<string name="badges_back">RETURN TO BASE</string>
```

`BadgeDetailSheet` reuses these existing strings unchanged — the wording is generic enough for badges:
- `R.string.medal_detail_how_to_earn` ("HOW TO EARN")
- `R.string.medal_detail_earned_count` ("Earned %1$d times")
- `R.string.medal_detail_rarity_common/rare/epic/legendary`

No new strings needed for `BadgeDetailSheet`.

---

## 9. What Does Not Change

- `MedalsStorage`, `MedalsViewModel`, `MedalsScreen` — untouched
- `MedalCanvas` internal drawing logic — untouched (only shared helpers extracted)
- `MedalSymbols.kt` — untouched (`drawBadgeSymbol` already `internal`)
- `Badge` enum, `Rarity` enum — untouched
- `BadgeResources.kt` (`unlockHintResId()`) — reused by `BadgeDetailSheet`
- Domain layer — zero changes

---

## 10. Out of Scope

- Sorting or filtering the Badges grid
- Animations on badge earn
- Separate persistence for badge history per game
