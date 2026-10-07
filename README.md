# Battleship

A naval strategy game for Android. Place your fleet, then take turns firing at the computer's ships. Sink all five to win. It works offline and needs no account. The computer searches alternating squares and fires at the squares around a hit.

## Website

- [English](https://battleship.cocode.dk/)
- [Dansk (Danish)](https://battleship.cocode.dk/da/)
- [فارسی (Persian)](https://battleship.cocode.dk/fa/)

---

## Download

<!-- cocode-apps:install:start -->
[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/packages/com.cocode.battleship/)
- [Download the Android installation file (APK) from GitHub](https://github.com/cocodedk/Battleship/releases/latest/download/Battleship.apk)
- [Add the app to Obtainium, an app that keeps it up to date](https://apps.obtainium.imranr.dev/redirect?r=obtainium://add/https://github.com/cocodedk/Battleship)
<!-- cocode-apps:install:end -->

Minimum Android version: **7.0 (API 24)**

---

## Features

### Core Gameplay

- **Fully offline** — no network, no account, no tracking
- **10×10 grid** — 5-ship fleet (Carrier, Battleship, Cruiser, Submarine, Destroyer)
- **Computer opponent** — searches alternating squares of the grid, then targets the squares around a hit
- **Ship placement** — tap a cell to place, a separate button switches orientation, or deploy the whole fleet with one tap
- **Naval Sonar Command aesthetic** — dark navy theme on Material3
- **Languages** — English and Danish; the app follows your phone's language

### Super Weapons

Unlock special attack abilities by sinking enemy ships (5 total):

- **Carpet Bomb** — 3×3 area bombardment (unlock: sink Carrier)
- **Battleship Barrage** — 9-cell cross saturation fire (unlock: sink Battleship)
- **Sonar Sweep** — 5-cell horizontal ping (unlock: sink Cruiser)
- **Torpedo Spread** — 5-cell vertical salvo (unlock: sink Submarine)
- **Precision Strike** — 5-cell X-pattern surgical strike (unlock: sink Destroyer)

### Medals & Ranking System

- **33 medals** across 4 rarities (Common, Rare, Epic, Legendary)
  - Examples: Perfect Gunner (win with 0 misses), Flawless Victory (all 5 ships still afloat), Dead-Eye (80%+ accuracy)
- **8 career ranks**: Cadet → Ensign → Lieutenant → Captain → Commodore → Vice Admiral → Admiral → Fleet Admiral
- **Career Stats** shows your statistics (games played, victories, win rate, win streaks) and your highest rank. **Medal Registry** shows your accumulated medals. Both keep their records across games and app restarts.

### Visual & Audio Effects

- **Sound effects** for hits, misses, sunken ships and victories
- **Visual effects** — hit flash animations, sunk ship glow pulse, animated sonar UI

---

## Privacy

Battleship does not collect, send or share any personal data. It has no internet permission and uses no analytics, crash reporting or advertising. Your game record (career stats, ranks and medals) is stored privately on your device, and the app does not transmit it. If you use Android backup, Android may copy it to your own Google backup. The About screen and the main menu open web pages (for example this policy) in your browser, but only when you tap them. Read the full policy at <https://battleship.cocode.dk/privacy/>.

---

## Build

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

### Tests

```bash
./gradlew test
```

Unit tests cover the domain logic (pure Kotlin, no Android dependencies) and the presentation logic that can be tested without a device.

---

## Architecture

Clean Architecture with two layers. The domain layer has zero Android dependencies.

```text
app/src/main/java/com/cocode/battleship/
│
├── domain/                   ← Pure Kotlin — fully testable
│   ├── model/                ← Ship, Board, ShipType, CellState, GamePhase, SuperWeapon
│   └── ai/                   ← BattleshipAI (hunt/target algorithm)
│
├── presentation/             ← Android + Jetpack Compose
│   ├── navigation/           ← Screen routes, BattleshipNavHost
│   ├── menu/                 ← MenuScreen
│   ├── placement/            ← PlacementScreen
│   ├── game/                 ← GameScreen, GameViewModel, GameOverScreen
│   ├── stats/                ← StatsScreen (career stats, highest rank)
│   ├── medals/               ← MedalsScreen (medal registry)
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
| Language | Kotlin 2.3.21 |
| UI | Jetpack Compose BOM 2026.06.01 + Material3 |
| Architecture | Clean Architecture + MVVM |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |
| Build | Gradle 9.3.1 (Kotlin DSL), AGP 9.1.1 |

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for local setup, the git hooks, the build and test commands and the coding style.

---

## Author

**Babak Bandpey** — [cocode.dk](https://cocode.dk) | [LinkedIn](https://linkedin.com/in/babakbandpey) | [GitHub](https://github.com/cocodedk)

## License

Apache-2.0 | © 2026 [Cocode](https://cocode.dk) | Created by [Babak Bandpey](https://linkedin.com/in/babakbandpey)
