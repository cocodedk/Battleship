# Battleship

A local naval combat game for Android. No network connection required — play against an AI opponent or pass the device for two-player mode.

## Website

- [English](https://cocodedk.github.io/Battleship/)
- [فارسی (Persian)](https://cocodedk.github.io/Battleship/fa/)

---

## Features

### Core Gameplay

- **Fully offline** — no network, no account, no tracking
- **10×10 grid** — 5-ship fleet (Carrier, Battleship, Cruiser, Submarine, Destroyer)
- **AI opponent** — hunt/target algorithm with checkerboard parity
- **Ship placement** — manual drag-and-drop or one-tap auto-deployment
- **Two-player mode** — pass-and-play on a single device
- **Naval Sonar Command aesthetic** — dark navy theme on Material3

### Super Weapons

Unlock special attack abilities by sinking enemy ships (5 total):

- **Carpet Bomb** — 3×3 area bombardment (unlock: sink Carrier)
- **Battleship Barrage** — 9-cell cross saturation fire (unlock: sink Battleship)
- **Sonar Sweep** — 5-cell horizontal ping (unlock: sink Cruiser)
- **Torpedo Spread** — 5-cell vertical salvo (unlock: sink Submarine)
- **Precision Strike** — 5-cell X-pattern surgical strike (unlock: sink Destroyer)

### Medals & Ranking System

- **15 medals** across 4 rarities (Common, Rare, Epic, Legendary)
  - Examples: Perfect Gunner (win with 0 misses), Flawless Victory (all 5 ships intact), Dead-Eye (80%+ accuracy)
- **8 career ranks**: Cadet → Ensign → Lieutenant → Captain → Commodore → Vice Admiral → Admiral → Fleet Admiral
- **Career stats screen** — track accumulated medals, best rank, and win streaks across games and app restarts

### Visual & Audio Effects

- **PCM-synthesized audio** — hit impacts, miss sweeps, ship sunk explosions, victory fanfare (no audio files needed)
- **Visual effects** — hit flash animations, sunk ship glow pulse, animated sonar UI

---

## Download

Grab the latest stable APK directly:

[**Download Battleship.apk**](https://github.com/cocodedk/Battleship/releases/latest/download/Battleship.apk)

Minimum Android version: **7.0 (API 24)**

---

## Build from Source

**Prerequisites:** Android SDK, JDK 17+

```bash
git clone https://github.com/cocodedk/Battleship.git
cd Battleship
```

### Debug build

```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

### Release build (requires signing)

```bash
export KEYSTORE_PATH=release.keystore
export KEYSTORE_PASSWORD=<password>
export KEY_ALIAS=<alias>
export KEY_PASSWORD=<password>
./gradlew assembleRelease
```

APK output: `app/build/outputs/apk/release/app-release.apk`

---

## Tests

```bash
./gradlew test
```

Unit tests cover the domain layer (pure Kotlin, no Android dependencies).

---

## Architecture

Clean Architecture with two layers. The domain layer has zero Android dependencies.

```text
app/src/main/java/com/cocode/battleship/
│
├── domain/                   ← Pure Kotlin — fully testable
│   ├── model/                ← Ship, Board, GameState, ShipType, CellState
│   └── ai/                   ← BattleshipAI (hunt/target algorithm)
│
├── presentation/             ← Android + Jetpack Compose
│   ├── navigation/           ← Screen routes, BattleshipNavHost
│   ├── menu/                 ← MenuScreen
│   ├── placement/            ← PlacementScreen
│   ├── game/                 ← GameScreen, GameViewModel, GameOverScreen
│   ├── stats/                ← StatsScreen (career stats, medals, ranks)
│   └── components/           ← Reusable composables (BattleGrid, etc.)
│
└── ui/theme/                 ← Material3 theme, colors, typography
```

**Key decisions:**

- Single `GameViewModel` created at nav-host level; shared across all screens
- `StateFlow` only for exposed state; mutations via `copy()` on immutable data classes
- AI runs on `Dispatchers.Default` via `viewModelScope`

### Tech stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.2.10 |
| UI | Jetpack Compose BOM 2026.02.01 + Material3 |
| Architecture | Clean Architecture + MVVM |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |
| Build | Gradle 9.3.1 (Kotlin DSL), AGP 9.1.0 |

---

## Author

**Babak Bandpey** — [cocode.dk](https://cocode.dk) | [LinkedIn](https://linkedin.com/in/babakbandpey) | [GitHub](https://github.com/cocodedk)

## License

Apache-2.0 | © 2026 [Cocode](https://cocode.dk) | Created by [Babak Bandpey](https://linkedin.com/in/babakbandpey)
