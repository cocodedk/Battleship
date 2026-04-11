# CLAUDE.md — Battleship Android Project

## Project Overview

A local Battleship game for Android. No server, no network connection required.

- **Package**: `com.cocode.battleship`
- **Min SDK**: 24 (Android 7.0) | **Target SDK**: 36
- **Kotlin**: 2.2.10 | **AGP**: 9.1.0 | **Gradle**: 9.3.1 (Kotlin DSL)
- **UI**: Jetpack Compose (BOM 2026.02.01) + Material3
- **Architecture**: Clean Architecture + MVVM

### Game Modes

- Single player (player vs AI)
- Two players on the same device (pass-and-play, optional)

---

## Required Skills — ALWAYS Invoke These

These skills are installed and **must** be invoked using the Skill tool when the relevant situation arises. Never skip them.

| Situation | Skill to invoke |
|-----------|----------------|
| Before implementing any new feature or screen | `superpowers:brainstorming` |
| Planning multi-step changes | `superpowers:writing-plans` |
| Writing or fixing logic in the domain layer | `superpowers:test-driven-development` |
| First sign of a bug or build failure | `superpowers:systematic-debugging` |
| Before completing a feature branch | `superpowers:requesting-code-review` |
| Before claiming any task is done | `superpowers:verification-before-completion` |
| Working on UI screens or components | `frontend-design:frontend-design` |
| After implementing — reviewing code quality | `simplify` |

---

## Architecture

Clean Architecture with two layers. The domain layer has **zero Android dependencies**.

```
app/src/main/java/com/cocode/battleship/
│
├── domain/                   ← Pure Kotlin, no Android deps, fully testable
│   ├── model/                ← Data classes: Ship, Board, GameState, ShipType, CellState
│   └── ai/                   ← AI opponent logic (BattleshipAI)
│
├── presentation/             ← Android/Compose code
│   ├── navigation/           ← Screen.kt (sealed class routes), BattleshipNavHost.kt
│   ├── menu/                 ← MenuScreen.kt
│   ├── placement/            ← PlacementScreen.kt
│   ├── game/                 ← GameScreen.kt, GameViewModel.kt, GameOverScreen.kt, GameUiState.kt
│   └── components/           ← Reusable composables (BattleGrid, etc.)
│
└── ui/theme/                 ← Color.kt, Theme.kt, Type.kt (existing)
```

### Layer Rules

- `domain/` must never import anything from `android.*`, `androidx.*`, or `presentation/`
- `presentation/` may import `domain/` freely
- **Single `GameViewModel`** is shared across all screens. It is created at `BattleshipNavHost` level (outside `NavHost`) so all routes share the same instance. Do NOT call `viewModel()` inside a `composable { }` block for this ViewModel.
- ViewModels live in `presentation/` and depend on domain models/AI directly (no repository layer needed for a local game)

---

## Coding Conventions

### Domain Layer

- [ ] All domain models are **immutable data classes** — use `copy()` for all updates
- [ ] Domain functions are **pure** — no side effects, no coroutines, no suspend functions
- [ ] `ShipType` and grid size (10×10) are **fixed constants** — never magic numbers in logic

### Presentation / ViewModel Layer

- [ ] ViewModel exposes state as `StateFlow` only — `MutableStateFlow` is **never** public
- [ ] State is a single sealed class or data class per screen — single source of truth
- [ ] AI computation runs on `viewModelScope` + `Dispatchers.Default`
- [ ] All state mutations go through `copy()` on the state data class

### Compose / UI Layer

- [ ] State is **hoisted to ViewModel** — composables receive state + lambdas only
- [ ] No hardcoded strings in composables — use `stringResource()` or top-level constants
- [ ] Reusable board/grid components go in `presentation/components/`
- [ ] Invoke `frontend-design:frontend-design` skill before building any new screen

### General

- [ ] Kotlin DSL everywhere — no Groovy `.gradle` files
- [ ] No network, no persistence needed (in-memory game state only)

---

## Testing

- Unit tests cover the **domain layer only** — no Android framework needed
- Test location: `app/src/test/java/com/cocode/battleship/` (not `androidTest/`)
- Naming convention: `XxxTest.kt` mirroring the source file (e.g., `BattleshipAITest.kt`)
- Test runner: JUnit4
- Invoke `superpowers:test-driven-development` before writing any domain logic

```bash
./gradlew test          # Run all unit tests
```

---

## Build Commands

```bash
./gradlew assembleDebug      # Build debug APK
./gradlew assembleRelease    # Build release APK
./gradlew test               # Run unit tests
./gradlew lint               # Run lint checks
```

---

## Key Files

| File | Purpose |
|------|---------|
| `app/build.gradle.kts` | App-level build config, dependencies |
| `build.gradle.kts` | Root build config |
| `settings.gradle.kts` | Project name, module list |
| `gradle.properties` | JVM args, Kotlin/Android flags |
| `ui/theme/Color.kt` | App color palette |
| `ui/theme/Theme.kt` | Material3 theme setup |
| `ui/theme/Type.kt` | Typography definitions |

---

## Starting a New Session Quickly

1. Read this file
2. Run `./gradlew test` to verify the project builds and tests pass
3. Invoke `superpowers:brainstorming` before touching any feature
4. Follow the skills table above — every relevant skill is mandatory, not optional
