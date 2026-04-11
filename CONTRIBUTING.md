# Contributing to Battleship

## Local Setup

1. Install Android Studio (latest stable) or use the Gradle wrapper directly.
2. Ensure Java 17 (temurin) is available on your PATH.
3. Open the project root in Android Studio and let Gradle sync.

## Install Git Hooks

Run this once after cloning:

    ./scripts/install-hooks.sh

The pre-commit hook runs the smoke check before every commit.

## Build and Test Commands

    ./gradlew assembleDebug         # Debug APK
    ./gradlew assembleRelease       # Release APK (requires signing env vars)
    ./gradlew test                  # Unit tests (domain layer)
    ./gradlew lint                  # Lint checks
    ./gradlew buildSmoke            # Debug + tests + lint (CI smoke check)

## Coding Style

- Kotlin idiomatic style — use data classes, `copy()`, sealed classes
- Domain layer (`domain/`) must have zero Android dependencies
- Composables receive state and lambdas only — no direct ViewModel access in leaf composables
- Keep files under 200 lines — extract classes or composables when approaching the limit
- Follow DRY, SOLID, KISS, YAGNI principles (see CLAUDE.md for details)

## PR Checklist

- [ ] `./gradlew buildSmoke --no-daemon` passes locally
- [ ] New domain logic has unit tests
- [ ] Composables reviewed against the naval command aesthetic
- [ ] No files exceed 200 lines
- [ ] Strings added to `res/values/strings.xml` (no hardcoded strings in composables)
