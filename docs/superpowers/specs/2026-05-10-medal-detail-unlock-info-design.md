# Medal Detail — Unlock Info Bottom Sheet

**Date:** 2026-05-10  
**Branch:** `feature/medal-detail-unlock-info`  
**Status:** Approved

## Problem

The Medal Registry displays all 15 badges as hex tiles. Locked medals show a padlock symbol but give the player no indication of what they must do to earn them. Earned medals similarly have no contextual description. Players have no in-app way to learn badge requirements.

## Solution

Tap any medal tile → a `ModalBottomSheet` slides up showing the badge art, rarity, and a plain-English description of exactly how to earn it.

---

## Data Layer

**File:** `app/src/main/java/com/cocode/battleship/domain/scoring/Badge.kt`

Add `val unlockHint: String` as a constructor parameter to the `Badge` enum. Each value is a single, human-readable sentence. The domain layer remains pure Kotlin — no Android imports.

Unlock hints per badge:

| Badge | Hint |
|-------|------|
| `FIRST_BLOOD` | Hit the enemy on your very first shot |
| `SHARPSHOOTER` | Finish with 60% accuracy or better (minimum 10 shots) |
| `DEAD_EYE` | Finish with 80% accuracy or better (minimum 10 shots) |
| `HOT_STREAK` | Land 5 hits in a row without missing |
| `UNSTOPPABLE` | Land 8 hits in a row without missing |
| `FLAWLESS_VICTORY` | Win with all 5 of your ships still afloat |
| `PERFECT_GUNNER` | Win without missing a single shot |
| `LEVIATHAN_SLAYER` | Sink the enemy Carrier as your first kill |
| `SILENT_SERVICE` | Win without your Submarine taking a single hit |
| `LAST_STAND` | Win with only 1 ship surviving |
| `DESTROYER_LIVES` | Win without your Destroyer taking a single hit |
| `SWIM_FOR_IT` | Lose without landing a single hit on the enemy |
| `FOG_OF_WAR` | Miss 10 shots in a row |
| `DEPTH_CHARGE_DIPLOMAT` | Fire 100 or more shots in a single game |
| `ON_FIRE` | Win 3 games in a row in the same session |

---

## Presentation Layer

### New file — `MedalDetailSheet.kt`

**Path:** `app/src/main/java/com/cocode/battleship/presentation/medals/MedalDetailSheet.kt`

A `@OptIn(ExperimentalMaterial3Api::class) @Composable` function `MedalDetailSheet(item: MedalItem, onDismiss: () -> Unit)`.

Layout (top → bottom inside the sheet):

1. **MedalCanvas** — 120dp, centred, same hex art as the grid tile
2. **Badge display name** — `titleMedium`, rarity colour
3. **Rarity chip** — small text label `◆ RARE` / `◆ EPIC` etc., rarity colour at 60% alpha
4. **Horizontal divider**
5. **Section label** — `HOW TO EARN`, `labelSmall`, `TextSecondary`, 3sp letter spacing
6. **Unlock hint** — `item.badge.unlockHint`, `bodyMedium`, `TextPrimary`
7. **Earned count** (conditional, only when `item.isEarned`) — `× N earned`, `labelMedium`, `AmberWarning`

The sheet uses the app's existing navy background (`NavyCard`) as its container colour.

### Changed file — `MedalsScreen.kt`

- Add local state: `var selectedItem: MedalItem? by remember { mutableStateOf(null) }`
- Pass `onClick = { selectedItem = item }` into each `MedalCell`
- `MedalCell` wraps its root `Column` with `Modifier.clickable { onClick() }`
- After the `LazyVerticalGrid`, add:  
  `if (selectedItem != null) MedalDetailSheet(item = selectedItem!!, onDismiss = { selectedItem = null })`

No changes to `MedalsViewModel`, `MedalsUiState`, or `MedalsStorage`.

---

## File Impact

| File | Type | Change |
|------|------|--------|
| `domain/scoring/Badge.kt` | Modified | Add `unlockHint: String` to all 15 entries |
| `presentation/medals/MedalsScreen.kt` | Modified | Click state + sheet trigger |
| `presentation/medals/MedalDetailSheet.kt` | New | Bottom sheet composable (~80 lines) |

## Out of Scope

- Persisting which detail sheet was last opened
- Animations beyond the stock `ModalBottomSheet` slide-up
- Any changes to the scoring or badge logic
