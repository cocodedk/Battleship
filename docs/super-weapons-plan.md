# Super Weapons — Implementation Plan

## 1. Design Summary

Super weapons are **one-shot player-only specials** unlocked when an AI ship is sunk. Each of the 5 `ShipType`s unlocks exactly one weapon, added to `availableWeapons` the moment that AI ship is first sunk. The player may tap a weapon chip to select it (toggle), then tap a target cell on the AI board — the weapon fires its pattern (multiple cells), each cell resolved independently against `hasBeenAttacked`. After firing, the weapon is consumed from `availableWeapons`. No stacking: sinking the same type twice never re-grants. `resetGame()` resets weapons because it replaces the entire `GameUiState` with `GameUiState()`, which defaults `availableWeapons = emptyList()`.

### 1.1 Weapon Catalogue (5 weapons, one per ShipType)

| # | Weapon Name | Icon | Unlocked By | Pattern Shape | Relative Offsets `(dRow, dCol)` |
|---|---|---|---|---|---|
| 1 | Carpet Bomb | 💣 | `CARRIER` (size 5) | 3×3 square | `(-1,-1)(-1,0)(-1,1)(0,-1)(0,0)(0,1)(1,-1)(1,0)(1,1)` |
| 2 | Battleship Barrage | 🎯 | `BATTLESHIP` (size 4) | Plus/cross radius 2 | `(-2,0)(-1,0)(0,-2)(0,-1)(0,0)(0,1)(0,2)(1,0)(2,0)` |
| 3 | Sonar Sweep | 📡 | `CRUISER` (size 3) | 5×1 horizontal | `(0,-2)(0,-1)(0,0)(0,1)(0,2)` |
| 4 | Torpedo Spread | 🚀 | `SUBMARINE` (size 3) | 5×1 vertical | `(-2,0)(-1,0)(0,0)(1,0)(2,0)` |
| 5 | Precision Strike | ✖ | `DESTROYER` (size 2) | X diagonal | `(-1,-1)(-1,1)(0,0)(1,-1)(1,1)` |

All offsets are clamped via `resolveWeaponCells()`: cells outside `[0, GRID_SIZE)` are discarded. Every weapon's offsets include `(0,0)` (the target cell is always fired at).

---

## 2. New Files

### `domain/model/SuperWeapon.kt` (~55 lines)
Enum with 5 entries. Each entry has: `displayName`, `icon`, `displayNameKey`, `descriptionKey`, `unlockShip: ShipType`, `offsets: List<Pair<Int,Int>>`. Companion object: `forShipType(type): SuperWeapon`.

**Important:** Composables must NOT use `displayName` from the enum. Instead, `WeaponSelector.kt` maps via a `@Composable fun weaponDisplayName(weapon: SuperWeapon): String` using `when` + `stringResource(R.string.*)`.

### `domain/model/SuperWeaponAttack.kt` (~20 lines)
Single top-level pure function:
```
fun resolveWeaponCells(weapon: SuperWeapon, row: Int, col: Int): List<Pair<Int,Int>>
```
Maps offsets to absolute cells, discards out-of-bounds, deduplicates.

### `test/domain/model/SuperWeaponTest.kt` (~140 lines)
15 TDD tests — see section 4.

### `presentation/game/components/WeaponSelector.kt` (~140 lines)
Horizontal `LazyRow` of `WeaponChip` composables. Props: `available`, `selected`, `onSelect`. Chips highlight when selected.

### `presentation/game/GameWeaponLogic.kt` (~55 lines)
Top-level functions in package `com.cocode.battleship.presentation.game`:
- `buildPlayerHitMessage(weapon, primaryCellState, newlySunkTypes, board): String`
- `updateTrackersForFire(current, board, firedCells, newlySunkTypes): TrackerState`

---

## 3. Modified Files

### `domain/model/Board.kt` (+18 lines → ~58 total)
Add after `getCellState()`:
```kotlin
fun receiveWeaponAttack(cells: List<Pair<Int, Int>>): Board {
    val newCells = cells.filter { it !in attacks }
    if (newCells.isEmpty()) return this
    val updatedShips = ships.map { ship ->
        newCells.fold(ship) { acc, (r, c) -> acc.receiveHit(r, c) }
    }
    return copy(ships = updatedShips, attacks = attacks + newCells.toSet())
}

fun getCellStates(cells: List<Pair<Int, Int>>): List<CellState> =
    cells.map { (r, c) -> getCellState(r, c) }
```

### `presentation/game/GameUiState.kt` (+2 fields → ~25 lines)
Add after `trackers`:
```kotlin
val availableWeapons: List<SuperWeapon> = emptyList(),
val selectedWeapon: SuperWeapon? = null,
```
Add import: `import com.cocode.battleship.domain.model.SuperWeapon`

### `presentation/game/GameViewModel.kt` (~185 lines after extraction)
- Import `SuperWeapon`, `resolveWeaponCells`
- Add `selectWeapon(weapon: SuperWeapon)` — toggles selection if it's in `availableWeapons`
- Add `deselectWeapon()` — clears `selectedWeapon`
- Rewrite `playerAttack(row, col)`:
  - If `selectedWeapon != null`: `firedCells = resolveWeaponCells(selected, row, col)`, `newAiBoard = aiBoard.receiveWeaponAttack(firedCells)`
  - Else: `if (hasBeenAttacked) return`, `firedCells = listOf(row to col)`, `newAiBoard = aiBoard.receiveAttack(row, col)`
  - Detect `newlySunkTypes = nowSunk - previouslySunk`
  - Grant new weapons for sunk types (skip already-granted types and consumed weapon)
  - Clear `selectedWeapon = null` after fire
  - Delegate messaging to `buildPlayerHitMessage`, tracker updates to `updateTrackersForFire`
- Move `buildPlayerHitMessage` and `updateTrackersForFire` to `GameWeaponLogic.kt` to stay under 200 lines

### `presentation/components/BattleGrid.kt` (+4 lines → ~181 lines)
Add param `allowAttackedClicks: Boolean = false` to `BattleGrid` signature and thread to `GridCell`:
```kotlin
// BattleGrid signature:
fun BattleGrid(board, showShips, onCellClick, previewShip, allowAttackedClicks: Boolean = false, modifier)

// GridCell:
val isClickable = onCellClick != null && (allowAttackedClicks || !hasBeenAttacked)
```

### `presentation/game/GameScreen.kt` (+24 lines → ~175 lines)
- Import `WeaponSelector`
- After turn-indicator block, insert:
  ```kotlin
  if (state.availableWeapons.isNotEmpty() && state.isPlayerTurn) {
      Spacer(Modifier.height(10.dp))
      WeaponSelector(
          available = state.availableWeapons,
          selected = state.selectedWeapon,
          onSelect = { viewModel.selectWeapon(it) },
          modifier = Modifier.fillMaxWidth()
      )
  }
  ```
- Pass `allowAttackedClicks = state.selectedWeapon != null` to the AI `BattleGrid`

### `res/values/strings.xml` (+15 lines)
```xml
<!-- Super Weapons -->
<string name="weapons_available_label">SUPER WEAPONS</string>
<string name="weapon_carpet_bomb_name">CARPET BOMB</string>
<string name="weapon_carpet_bomb_desc">3×3 area bombardment centered on target.</string>
<string name="weapon_barrage_name">BARRAGE</string>
<string name="weapon_barrage_desc">9-cell cross saturation fire.</string>
<string name="weapon_sonar_name">SONAR SWEEP</string>
<string name="weapon_sonar_desc">5-cell horizontal sonar ping.</string>
<string name="weapon_torpedo_name">TORPEDO SPREAD</string>
<string name="weapon_torpedo_desc">5-cell vertical torpedo salvo.</string>
<string name="weapon_precision_name">PRECISION STRIKE</string>
<string name="weapon_precision_desc">5-cell X-pattern surgical strike.</string>
```

---

## 4. Test Plan

### `SuperWeaponTest.kt` — 15 tests (TDD, write first)

1. `carpetBomb_centerOfBoard_returns9Cells` — fire at (5,5), expect 9 cells
2. `carpetBomb_topLeftCorner_clampsTo4Cells` — fire at (0,0), expect `{(0,0),(0,1),(1,0),(1,1)}`
3. `carpetBomb_bottomRightCorner_clampsTo4Cells` — fire at (9,9), expect `{(8,8),(8,9),(9,8),(9,9)}`
4. `battleshipBarrage_center_returns9UniqueCells` — fire at (5,5), expect 9 unique cells
5. `battleshipBarrage_leftEdge_clampsHorizontalArm` — fire at (5,0), expect 7 cells (cols -2,-1 dropped)
6. `sonarSweep_center_returns5HorizontalCells` — fire at (3,5), expect row=3, cols 3..7
7. `sonarSweep_nearLeftEdge_clampsCorrectly` — fire at (3,1), expect cols `{0,1,2,3}` (4 cells)
8. `torpedoSpread_center_returns5VerticalCells` — fire at (5,5), expect col=5, rows 3..7
9. `torpedoSpread_nearBottomEdge_clampsCorrectly` — fire at (8,5), expect rows `{6,7,8,9}` (4 cells)
10. `precisionStrike_center_returns5XCells` — fire at (5,5), expect `{(4,4),(4,6),(5,5),(6,4),(6,6)}`
11. `precisionStrike_topLeftCorner_clampsTo2Cells` — fire at (0,0), expect `{(0,0),(1,1)}`
12. `forShipType_returnsCorrectWeaponForEachType` — assert each ShipType maps to right weapon
13. `allOffsets_containCenter_00` — every weapon's offsets include `(0,0)`
14. `allOffsetLists_haveNoDuplicates` — sanity check
15. `allWeapons_haveUniqueUnlockShip` — `entries.map { it.unlockShip }.distinct().size == 5`

### `BoardTest.kt` — 5 new tests (TDD, write first)

1. `receiveWeaponAttack_appliesAllHitsAtOnce`
2. `receiveWeaponAttack_skipsAlreadyAttackedCells`
3. `receiveWeaponAttack_emptyCellsList_returnsSameBoard`
4. `receiveWeaponAttack_allCellsAlreadyAttacked_returnsSameBoard`
5. `receiveWeaponAttack_partialHit_sinksOnlyCoveredShipPortion`

---

## 5. Wave-Based Implementation Order

### Wave 1 — Domain (parallel TDD)
- **Task 1**: `SuperWeapon.kt` + `SuperWeaponAttack.kt` + `SuperWeaponTest.kt`
- **Task 2**: Extend `Board.kt` + new tests in `BoardTest.kt`

### Wave 2 — State + Assets (parallel, depends on Wave 1)
- **Task 3**: `GameUiState.kt` + `strings.xml`
- **Task 4**: `WeaponSelector.kt`
- **Task 5**: `BattleGrid.kt` extension

### Wave 3 — ViewModel (depends on Wave 2)
- **Task 6**: `GameWeaponLogic.kt` (helpers)
- **Task 7**: `GameViewModel.kt` rewrite

### Wave 4 — Screen (depends on Wave 3)
- **Task 8**: `GameScreen.kt`

---

## 6. File Line-Count Estimates

| File | Current | After | Limit |
|---|---|---|---|
| `SuperWeapon.kt` | new | ~90 | 200 |
| `SuperWeaponAttack.kt` | new | ~20 | 200 |
| `Board.kt` | 40 | ~58 | 200 |
| `GameUiState.kt` | 21 | ~25 | 200 |
| `GameViewModel.kt` | 170 | ~185 | 200 ⚠️ monitor |
| `GameWeaponLogic.kt` | new | ~55 | 200 |
| `WeaponSelector.kt` | new | ~140 | 200 |
| `GameScreen.kt` | 151 | ~175 | 200 |
| `BattleGrid.kt` | 177 | ~181 | 200 ⚠️ close |

**BattleGrid.kt is already at 177 lines.** The 4-line change keeps it at ~181 — safe, but no room for extra additions.
