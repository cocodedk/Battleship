# Medals Screen Design

**Date:** 2026-04-12
**Branch:** fix/persist-career-stats
**Status:** Approved

---

## Goal

Add a dedicated Medals screen showing all 15 badges with custom Canvas-drawn hexagonal art (no emojis), earn-count per badge (amber corner bubble, slightly larger tag), and a data migration from `Set<Badge>` to `Map<Badge,Int>`. Naval HUD aesthetic throughout.

---

## Section 1: Approved Design Decisions

| Question | Decision |
|----------|----------|
| Badge visibility | All 15 always visible; locked/unearned dimmed at ~30% opacity with lock symbol |
| Medal art style | Hexagonal Tactical Badge — hex outline with corner accents, geometric inner symbol |
| Earn count display | Amber corner bubble (top-right of hex), slightly larger tag (~12sp bold) |
| Data migration | Replace StringSet with count map; migrate on first launch |
| Navigation | Second route from MenuScreen: `Screen.Medals`; remove `BadgeShowcase` from `StatsScreen` |

---

## Section 2: Medal Art Specification

### Shape

Each medal is a hexagonal Canvas drawing (`pointy-top` orientation: flat sides left/right, vertices top/bottom).

Rarity → border and accent color:

| Rarity | Color | Notes |
|--------|-------|-------|
| COMMON | `#b87333` (bronze) | plain border |
| RARE | `#7eb8d4` (silver-blue) | circle dots at 6 vertices |
| EPIC | `#ffd700` (gold) | diamond tick marks at 6 vertices |
| LEGENDARY | `#00d4ff` (cyan) | diamond ticks + SVG-style glow filter (via `BlurMaskFilter`) |

All medals share:
- Outer hex stroke: 2.5dp, rarity color
- Inner hex stroke (offset inward ~8dp): 1dp, 50% rarity color
- Dark navy fill: background

Locked state: entire composable at `alpha = 0.28f`, inner symbol replaced by a lock shape (rectangle + arc bow).

### Inner Symbols (one per badge)

| Badge | Symbol |
|-------|--------|
| FIRST_BLOOD | Crosshair — 2 circles + 4 lines + center dot |
| SHARPSHOOTER | Arrow pointing right — shaft + triangular head |
| DEAD_EYE | Double-ring target — 2 concentric circles + center dot |
| HOT_STREAK | Lightning bolt — angular zigzag path |
| UNSTOPPABLE | Double lightning — two offset zigzag paths |
| FLAWLESS_VICTORY | Crown — polyline peaks + base bar + 3 dots |
| PERFECT_GUNNER | Diamond gem — polygon + inner face + dividing line |
| LEVIATHAN_SLAYER | Wave arc — smooth curve simulating a breaching shape |
| SILENT_SERVICE | Submarine — horizontal oval hull + conning tower rect + periscope line |
| LAST_STAND | Shield — D-path outline + center vertical bar |
| DESTROYER_LIVES | Ship silhouette — horizontal rect hull + superstructure rect + bow triangle |
| SWIM_FOR_IT | Swim waves — 2 wavy horizontal lines |
| FOG_OF_WAR | Scatter dots — 5 circles at random-but-fixed offsets |
| DEPTH_CHARGE_DIPLOMAT | Bomb circle — circle + short fuse arc + dot |
| ON_FIRE | Double flame — two teardrop/flame paths offset from center |

---

## Section 3: Screen Layout

### Header

```
◆  MEDAL REGISTRY
▶  7 / 15 MEDALS EARNED          (PhosphorGreen, monospace)
```

### Grid

- `LazyVerticalGrid(columns = Fixed(3))`
- Each cell: hex medal composable (64dp wide) + badge name below (9sp, rarity color, `maxLines=2`)
- Earned badges: full color + amber corner bubble `×N` (12sp bold, `#ffa500`)
- Locked badges: 28% alpha, lock symbol inside hex, no bubble

### Color mapping by rarity (name label)

| Rarity | Label color |
|--------|-------------|
| COMMON | `#b87333` |
| RARE | `SonarCyan.copy(alpha=0.85f)` |
| EPIC | `#ffd700` |
| LEGENDARY | `SonarCyan` (full, may glow) |

### Navigation

- `MenuScreen` gains `onViewMedals: () -> Unit` parameter and a second `OutlinedButton` labeled `VIEW MEDALS` (below VIEW STATS, same visual style)
- `Screen.Medals` added to `Screen.kt`
- `BattleshipNavHost` creates `MedalsViewModel`, adds `composable(Screen.Medals.route)` block
- `StatsScreen.kt`: remove `BadgeShowcase` composable call (medals have their own screen)

---

## Section 4: Data Layer

### Interface

```kotlin
// presentation/medals/MedalsStorage.kt
interface MedalsStorage {
    fun load(): Map<Badge, Int>
    fun increment(badges: List<Badge>)
}
```

### SharedPreferences implementation

Key: `"badge_counts"` — stored as comma-separated `"NAME:count"` pairs, e.g. `"FIRST_BLOOD:4,SHARPSHOOTER:2"`.

**Migration** (runs once on first `load()` call):
1. Read old key `"earned_badges"` (StringSet)
2. If present: write each badge with count=1 to `"badge_counts"`, delete `"earned_badges"`
3. Use `.commit()` for durability (same as `SharedPreferencesSessionStatsStorage`)

### Hooking into game-over

`SessionStats.initialize()` gains an optional second parameter:
```kotlin
fun initialize(storage: SessionStatsStorage, medalsStorage: MedalsStorage? = null)
```
Inside `record(score, isWin, earnedBadges)`, after updating internal state, call:
```kotlin
medalsStorage?.increment(earnedBadges)
```
`MainActivity` passes both storage instances to `SessionStats.initialize(...)`.

### ViewModel

```kotlin
// presentation/medals/MedalsViewModel.kt
class MedalsViewModel(storage: MedalsStorage) : ViewModel() {
    private val _state = MutableStateFlow(buildUiState(storage.load()))
    val state: StateFlow<MedalsUiState> = _state.asStateFlow()
}

data class MedalsUiState(val items: List<MedalItem>, val earnedCount: Int)
data class MedalItem(val badge: Badge, val count: Int)
// isEarned = count > 0
```

`MedalsViewModel` is created at `BattleshipNavHost` level (outside `NavHost`) using a `ViewModelProvider.Factory` that injects `SharedPreferencesMedalsStorage` — consistent with how `GameViewModel` is created. It is not the shared `GameViewModel`.

---

## Section 5: File Architecture

| Action | File | Lines (est.) |
|--------|------|-------------|
| Create | `presentation/medals/MedalCanvas.kt` | ~180 |
| Create | `presentation/medals/MedalsScreen.kt` | ~130 |
| Create | `presentation/medals/MedalsViewModel.kt` | ~40 |
| Create | `presentation/medals/MedalsUiState.kt` | ~15 |
| Create | `presentation/medals/MedalsStorage.kt` | ~10 |
| Create | `presentation/medals/SharedPreferencesMedalsStorage.kt` | ~70 |
| Modify | `presentation/navigation/Screen.kt` | +1 object |
| Modify | `presentation/navigation/BattleshipNavHost.kt` | +composable block |
| Modify | `presentation/menu/MenuScreen.kt` | +1 button param |
| Modify | `presentation/game/SessionStats.kt` | +medalsStorage param |
| Modify | `MainActivity.kt` | pass MedalsStorage to SessionStats |
| Modify | `presentation/stats/StatsScreen.kt` | remove BadgeShowcase |
| Create | `test/.../medals/MedalsStorageTest.kt` | ~60 |

---

## Constraints

- All files ≤ 200 lines
- No new Gradle dependencies — Canvas drawing only, existing Compose APIs
- `MedalCanvas.kt` may be split into `MedalCanvas.kt` + `MedalSymbols.kt` if it approaches 200 lines
- Lock symbol drawn in Canvas (no emoji)
- Earn count bubble drawn in Canvas (not a composable overlay) — keep `MedalsScreen.kt` simpler
- `MedalsStorage` lives in `presentation/medals/` (local game state, not domain logic)
