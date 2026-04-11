# Score + Badge System — Implementation Plan

## Design Summary

### Score Formula

```
base:           WIN=1000, LOSS=200
victory_bonus:  +300 × survivingPlayerShips  +5 × totalPlayerShipHp  (WIN only)
accuracy_bonus: +round(accuracy × 500)  +250 if ≥50%  +500 if perfectGame
speed_bonus:    +max(0, (50 - totalShots) × 25)  (WIN only, par=50 shots)
streak_bonus:   +longestHitStreak × 40  +200 if streak≥5  +500 if streak≥8
sink_bonus:     +100 × shipsSunkByPlayer
flourish_bonus: +150 if firstShotHit
penalties:      -10 × max(0, totalShots-80)  -50 if LOSS && hits==0
final =         max(0, sum)
```

### Rank Thresholds

| Rank | Min Score |
|------|-----------|
| Fleet Admiral | 3500 |
| Admiral | 2600 |
| Vice Admiral | 2000 |
| Commodore | 1500 |
| Captain | 1100 |
| Lieutenant | 700 |
| Ensign | 350 |
| Cadet | 0 |

### Badge Catalogue (15 badges)

| # | Name | Icon | Rarity | Condition |
|---|------|------|--------|-----------|
| 1 | First Blood | 🎯 | Rare | firstShotHit == true |
| 2 | Sharpshooter | 🏹 | Rare | accuracy >= 0.60 && totalShots >= 10 |
| 3 | Dead-Eye | 🎯🎯 | Epic | accuracy >= 0.80 && totalShots >= 10 |
| 4 | Hot Streak | 🔥 | Rare | longestHitStreak >= 5 |
| 5 | Unstoppable | ⚡ | Epic | longestHitStreak >= 8 |
| 6 | Flawless Victory | 👑 | Epic | WIN && survivingPlayerShips == 5 |
| 7 | Perfect Gunner | 💎 | Legendary | WIN && misses == 0 |
| 8 | Leviathan Slayer | 🐋 | Rare | first enemy ship sunk was CARRIER |
| 9 | Silent Service | 🤫 | Rare | player's SUBMARINE untouched at end |
| 10 | Last Stand | 🛡️ | Rare | WIN && survivingPlayerShips == 1 |
| 11 | Destroyer Lives | 🚤 | Common | player's DESTROYER untouched at end |
| 12 | Swim for It | 🏊 | Rare | LOSS && hits == 0 |
| 13 | Fog of War | 🌫️ | Common | longestMissStreak >= 10 |
| 14 | Depth Charge Diplomat | 💣 | Common | totalShots >= 100 |
| 15 | On Fire | 🔥🔥 | Epic | sessionStats.currentWinStreak >= 3 |

---

## Implementation Plan

### Wave 1 — Domain Models (pure Kotlin, all parallel, TDD)

**Task 1 — `GameStats` data class**
- File: `domain/scoring/GameStats.kt`
- Enum: `GameOutcome { WIN, LOSS }`, `ShipEndState { UNTOUCHED, DAMAGED, SUNK }`
- Data: outcome, totalShots, hits, misses, accuracy, survivingPlayerShips, totalPlayerShipHp, shipsSunkByPlayer, longestHitStreak, longestMissStreak, firstShotHit, firstEnemyShipSunkType, playerShipEndStates

**Task 2 — `ScoreCalculator` + test (TDD)**
- Test first: `domain/scoring/ScoreCalculatorTest.kt` — 11 test cases covering all formula branches and boundary values
- File: `domain/scoring/ScoreCalculator.kt` (~80 lines)
- API: `object ScoreCalculator { fun calculate(stats: GameStats): Int }`

**Task 3 — `Rank` enum + test (TDD)**
- Test first: `domain/scoring/RankTest.kt` — boundary values at each threshold
- File: `domain/scoring/Rank.kt` (~30 lines)
- API: `enum class Rank(minScore, displayName) { companion object { fun fromScore(score: Int): Rank } }`

**Task 4 — `Badge` enum + test (TDD)**
- Test first: `domain/scoring/BadgeTest.kt` — one test per badge + boundary check per badge
- File: `domain/scoring/Badge.kt` (~130 lines)
- API: `enum class Badge { ...; fun matches(stats: GameStats, sessionWinStreak: Int): Boolean }`
- Also: `enum class Rarity { COMMON, RARE, EPIC, LEGENDARY }`

**Task 5 — `ScoreResult` data class**
- File: `domain/scoring/ScoreResult.kt` (~15 lines)
- Data: score, rank, earnedBadges, stats

---

### Wave 2 — Tracker + Session Infrastructure (parallel)

**Task 6 — `TrackerState` + `updateTrackers()` + test (TDD)**
- Test first: `presentation/game/GameTrackersTest.kt` — 9 test cases covering hit/miss streak logic and firstShotHit
- File: `presentation/game/GameTrackers.kt` (~60 lines)
- API: `data class TrackerState(...)`, `fun updateTrackers(current, cellState, newlySunkType): TrackerState`

**Task 7 — `SessionStats` singleton**
- File: `presentation/game/SessionStats.kt` (~35 lines)
- API: `object SessionStats { gamesPlayed, totalWins, currentWinStreak, longestWinStreak, bestScore; fun record(score, isWin); fun reset() }`

---

### Wave 3 — ViewModel Integration (sequential, depends on Wave 1+2)

**Task 8 — Update `GameUiState` + `GameViewModel`**
- Modify `GameUiState.kt`: add `trackers: TrackerState`, `scoreResult: ScoreResult?`
- Modify `GameViewModel.kt`:
  - Call `updateTrackers()` on every player shot
  - Add `buildGameStats()` helper that derives all GameStats from boards + trackers
  - At game-over: compute ScoreResult, store in state, call SessionStats.record()
  - `resetGame()` does NOT reset SessionStats
- Line watch: currently 153 lines → ~188. Extract `buildGameStats` to `GameTrackers.kt` if >195

---

### Wave 4 — Theme (parallel with Wave 2)

**Task 9 — Add `BronzeGold` to Color.kt**
- Add: `val BronzeGold = Color(0xFFFFD54F)` — Legendary rarity accent

---

### Wave 5 — UI Components (Tasks 10–13 parallel, then Task 14)

**Task 10 — `RankScorePanel`**
- File: `presentation/game/components/RankScorePanel.kt` (~70 lines)
- Rank label + score counter animated via `animateIntAsState` with 1200ms tween

**Task 11 — `StatsBreakdownPanel`**
- File: `presentation/game/components/StatsBreakdownPanel.kt` (~80 lines)
- Two-column list: Shots / Hits / Misses / Accuracy% / Ships Sunk

**Task 12 — `BadgeShowcase`**
- File: `presentation/game/components/BadgeShowcase.kt` (~80 lines)
- LazyRow of badge chips; rarity border: COMMON=TextSecondary, RARE=SonarCyan, EPIC=AmberWarning, LEGENDARY=BronzeGold
- "No badges earned" placeholder when empty

**Task 13 — `SessionFooter`**
- File: `presentation/game/components/SessionFooter.kt` (~40 lines)
- Single row: "Games: N | Wins: N | Streak: N | Best: N" in TextDim

**Task 14 — Refactor `GameOverScreen`**
- Change signature to accept `GameViewModel` (matching GameScreen pattern)
- Insert panels after existing hero block: RankScorePanel → StatsBreakdownPanel → BadgeShowcase → SessionFooter
- Keep Play Again + Main Menu buttons exactly as-is
- Update `BattleshipNavHost.kt` to pass viewModel

---

### Wave 6 — String Resources (parallel with Wave 5)

**Task 15 — Add strings to `res/values/strings.xml`**
- game_over_rank_label, game_over_score_label, game_over_stats_shots, game_over_stats_hits, game_over_stats_misses, game_over_stats_accuracy, game_over_stats_ships_sunk, game_over_badges_none, game_over_session_games, game_over_session_wins, game_over_session_streak, game_over_session_best

---

## New Files Summary

```
domain/scoring/
  GameStats.kt          Task 1
  ScoreCalculator.kt    Task 2
  Rank.kt               Task 3
  Badge.kt              Task 4  (includes Rarity enum)
  ScoreResult.kt        Task 5

presentation/game/
  GameTrackers.kt       Task 6
  SessionStats.kt       Task 7

presentation/game/components/
  RankScorePanel.kt     Task 10
  StatsBreakdownPanel.kt Task 11
  BadgeShowcase.kt      Task 12
  SessionFooter.kt      Task 13

test/domain/scoring/
  ScoreCalculatorTest.kt  Task 2
  RankTest.kt             Task 3
  BadgeTest.kt            Task 4

test/presentation/game/
  GameTrackersTest.kt     Task 6
```
