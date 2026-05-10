# Badges Canvas & Badges Page — Design Spec

## Goal

Replace emoji badge chips with custom-drawn canvas art, add a dedicated Badges screen (all 33 badges, earned + locked), show earned badges and medals separately on the game over screen, and add Badges to the main menu.

## Context

The app has two distinct collectible systems that must stay visually separate:

- **Medals** — hex-framed canvas art, tracked by lifetime count in `MedalsStorage`, shown in the Medal Registry screen.
- **Badges** — rarity-shaped canvas art (new), same underlying `Badge` enum, same count data from `MedalsStorage`, shown on the game over screen and the new Badges screen.

Both use the same `Badge` enum and the same `MedalsStorage` data source. The difference is purely visual: frame shape and screen placement.

---

## 1. Badge Shape System

Badge frame shape is determined by rarity — a visual rarity signal:

| Rarity    | Frame shape | Border color  |
|-----------|-------------|---------------|
| COMMON    | Circle      | `TextSecondary` (grey-blue) |
| RARE      | Shield      | `SonarCyan` |
| EPIC      | Diamond     | `AmberWarning` (purple-ish) |
| LEGENDARY | Star (5-pt) | `BronzeGold` |

Interior art is identical to the medal art — the same geometric drawing functions from `MedalDrawingHelpers.kt` are reused. The frame changes; the symbol inside does not.

Locked (unearned) badges render at 25% opacity.

Count badge (`×N`) appears in the top-right corner, same as medals.

---

## 2. New Files

| File | Purpose |
|------|---------|
| `presentation/badges/BadgeCanvas.kt` | Composable that draws a badge: rarity-based frame + interior art from `MedalDrawingHelpers` |
| `presentation/badges/BadgesUiState.kt` | `BadgeItem(badge, count)` data class + `BadgesUiState` |
| `presentation/badges/BadgesViewModel.kt` | Reads `MedalsStorage`, builds `BadgesUiState` |
| `presentation/badges/BadgesScreen.kt` | 3-column grid of all 33 badges, header with earned count, back button |
| `presentation/badges/BadgeDetailSheet.kt` | Bottom sheet shown on badge tap: `BadgeCanvas`, name, rarity, unlock hint, earned count |

---

## 3. Modified Files

| File | Change |
|------|--------|
| `presentation/game/components/BadgeShowcase.kt` | Replace `BadgeChip` (emoji) with `BadgeCanvas`; rename section label to `BADGES EARNED` |
| `presentation/game/GameOverScreen.kt` | Add `MedalsEarnedSection` below badges (hex `MedalCanvas` for each unique earned badge); add `onBadges` callback; add "VIEW ALL BADGES" button inside the badges section |
| `presentation/menu/MenuScreen.kt` | Add BADGES button entry |
| `presentation/navigation/Screen.kt` | Add `data object Badges : Screen("badges")` |
| `presentation/navigation/BattleshipNavHost.kt` | Add badges composable route; pass `onBadges` to `GameOverScreen` |

---

## 4. BadgeCanvas

```kotlin
@Composable
fun BadgeCanvas(badge: Badge, count: Int, modifier: Modifier = Modifier)
```

- Draws on an Android `Canvas` via `Composable` + `drawWithCache` (same pattern as `MedalCanvas`)
- Frame drawn first based on `badge.rarity`: circle, shield path, diamond polygon, or 5-point star polygon
- Interior art: calls the existing `drawBadgeSymbol(badge, ...)` from `MedalDrawingHelpers.kt` — no new drawing functions needed
- If `count == 0`: entire drawing rendered at 25% alpha (locked state)
- If `count > 0`: count badge (`×N`) drawn in top-right corner (circle + text), same as `MedalCanvas`
- Border stroke color from `rarityColor(badge.rarity)`

The 4 frame paths (in normalised 0–1 space, scaled to canvas size):
- **Circle**: `drawCircle(radius = 0.44f)`
- **Shield**: path `M0.5,0.06 L0.88,0.22 L0.88,0.56 Q0.88,0.84 0.5,0.97 Q0.12,0.84 0.12,0.56 L0.12,0.22 Z`
- **Diamond**: polygon `(0.5,0.03) (0.97,0.5) (0.5,0.97) (0.03,0.5)`
- **Star**: 5-point star with outer radius 0.47, inner radius 0.20, centred at (0.5, 0.47)

---

## 5. Badges Screen

Layout mirrors Medal Registry:

```
◆  BADGES                    ← SonarCyan header
▶  8 / 33 EARNED             ← PhosphorGreen subheader

[LazyVerticalGrid, 3 columns]
  BadgeCanvas(72dp) + name label per cell

[RETURN TO BASE button]
```

Tapping a badge opens `BadgeDetailSheet` (bottom sheet).

`BadgesViewModel` is scoped to the Badges screen (created inside the composable route, not shared). It reads `MedalsStorage` once on init and does not observe live updates — the screen is opened after a game ends, so counts are already final.

---

## 6. Game Over Screen

Updated section order:

1. Rank & Score panel
2. Stats Breakdown panel
3. **BADGES EARNED** — horizontal scroll of `BadgeCanvas` (56dp each, earned this game from `scoreResult.earnedBadges`). Shows empty-state text if none. Includes "▶ VIEW ALL BADGES" button at the bottom of this panel.
4. **MEDALS EARNED** — horizontal scroll of `MedalCanvas` (56dp each, unique badges from `scoreResult.earnedBadges` deduped). Shows nothing (section hidden) if no badges were earned.
5. Session Footer

The medals section is hidden entirely when `scoreResult.earnedBadges` is empty — no empty-state panel, since badges and medals are always earned together.

---

## 7. Navigation

`GameOverScreen` gains a new `onBadges: () -> Unit` callback, wired in `BattleshipNavHost` to navigate to `Screen.Badges`.

`BadgesScreen` receives a `MedalsStorage` instance (passed from `BattleshipNavHost`, same instance already used by `MedalsViewModel`).

---

## 8. What Does Not Change

- `MedalsStorage`, `MedalsViewModel`, `MedalsScreen`, `MedalCanvas` — untouched
- `Badge` enum, `Rarity` enum — untouched
- `MedalDrawingHelpers.kt` drawing functions — reused as-is, not modified
- `BadgeResources.kt` (`unlockHintResId()`) — reused by `BadgeDetailSheet`

---

## 9. Out of Scope

- Persisting badge history per game session (not requested)
- Sorting or filtering the Badges grid
- Animations on badge earn
