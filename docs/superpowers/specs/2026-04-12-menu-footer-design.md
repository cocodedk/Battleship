# Menu Footer — Author Attribution & Links

**Date:** 2026-04-12
**Branch:** fix/persist-career-stats
**Status:** Approved

---

## Goal

Add author attribution (Babak Bandpey), a website link (cocode.dk), and an APK download link to the MenuScreen splash page, in a way that is always visible, thematically consistent with the naval HUD aesthetic, and guards against accidental APK downloads.

---

## Approach

Static footer at the bottom of the existing `Column` in `MenuScreen.kt`. No new screens, no navigation changes, no expandable menus.

---

## Layout

The bottom of the `Column` currently ends with:

```
Spacer(16dp)
Text("SINGLE PLAYER · vs AI · 10×10 GRID")   ← TextDim, labelSmall
```

After this change it becomes:

```
Spacer(16dp)
Text("SINGLE PLAYER · vs AI · 10×10 GRID")   ← unchanged

Spacer(12dp)

── thin horizontal divider (SonarCyan @ 10% alpha, fillMaxWidth) ──

Spacer(8dp)

Text("DEVELOPED BY BABAK BANDPEY")            ← TextDim, labelSmall, letterSpacing 1sp, centered

Spacer(4dp)

Row (centered) {
  ClickableText("[ cocode.dk ]")              ← SonarCyan @ 55% alpha
  Text("  ·  ")                               ← TextDim
  ClickableText("[ GET APK ↗ ]")              ← SonarCyan @ 55% alpha
}

Spacer(16dp)
```

---

## Interaction

Both links use `LocalUriHandler.current.openUri(url)` — opens the system browser. No ViewModel involvement.

| Link | URL |
|------|-----|
| `[ cocode.dk ]` | `https://cocode.dk` |
| `[ GET APK ↗ ]` | `https://github.com/cocodedk/Battleship/releases/latest` |

The APK link opens the GitHub Releases page (not a direct `.apk` download). The user must explicitly tap the asset on GitHub, which prevents accidental on-device downloads from a stray touch.

---

## Strings

Three new entries in `app/src/main/res/values/strings.xml`:

```xml
<string name="menu_developed_by">DEVELOPED BY BABAK BANDPEY</string>
<string name="menu_website_link">[ cocode.dk ]</string>
<string name="menu_apk_link">[ GET APK ↗ ]</string>
```

---

## File Scope

| File | Change |
|------|--------|
| `app/src/main/java/com/cocode/battleship/presentation/menu/MenuScreen.kt` | Add footer composable and wiring |
| `app/src/main/res/values/strings.xml` | Add 3 strings |

No other files change. `MenuLogo.kt`, `BattleshipNavHost.kt`, `GameViewModel`, and all domain code are untouched.

---

## Constraints

- Footer must use existing theme colors only (`SonarCyan`, `TextDim`)
- No hardcoded strings in composables — use `stringResource()`
- `MenuScreen.kt` must stay under the 200-line file limit
- No new files created
