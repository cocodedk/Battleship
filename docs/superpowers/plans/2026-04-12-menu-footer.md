# Menu Footer Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a static author-attribution footer to the MenuScreen splash page with the developer's name, website link, and APK download link.

**Architecture:** A new private composable `MenuFooter` is extracted to its own file (`MenuFooter.kt`) to keep `MenuScreen.kt` under the 200-line project limit. The footer renders at the bottom of the existing `Column`, using `LocalUriHandler` to open external URLs in the system browser. No ViewModel changes, no navigation changes.

**Tech Stack:** Kotlin 2.2.10, Jetpack Compose (Material3 BOM 2026.02.01), `LocalUriHandler` from `androidx.compose.ui.platform`

---

## File Map

| Action | Path | Responsibility |
|--------|------|----------------|
| Modify | `app/src/main/res/values/strings.xml` | Add 3 new localised strings |
| Create | `app/src/main/java/com/cocode/battleship/presentation/menu/MenuFooter.kt` | Self-contained footer composable |
| Modify | `app/src/main/java/com/cocode/battleship/presentation/menu/MenuScreen.kt` | Call `MenuFooter()` + add 1 import |

---

## Task 1 — Add strings

**Files:**
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1.1: Add the three new strings**

Open `app/src/main/res/values/strings.xml`. After the existing `<string name="menu_view_stats">` line, insert:

```xml
    <string name="menu_developed_by">DEVELOPED BY BABAK BANDPEY</string>
    <string name="menu_website_link">[ cocode.dk ]</string>
    <string name="menu_apk_link">[ GET APK ↗ ]</string>
```

- [ ] **Step 1.2: Verify XML parses**

```bash
cd /home/cocodedk/0-projects/Battleship
./gradlew :app:mergeDebugResources 2>&1 | tail -5
```

Expected: `BUILD SUCCESSFUL`

---

## Task 2 — Create MenuFooter composable

**Files:**
- Create: `app/src/main/java/com/cocode/battleship/presentation/menu/MenuFooter.kt`

No unit test needed — this is a pure rendering composable with no logic. Correctness is verified by compilation and visual inspection on device.

- [ ] **Step 2.1: Create the file**

```kotlin
package com.cocode.battleship.presentation.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TextDim

private const val URL_WEBSITE = "https://cocode.dk"
private const val URL_APK = "https://github.com/cocodedk/Battleship/releases/latest"

@Composable
internal fun MenuFooter() {
    val uriHandler = LocalUriHandler.current

    Spacer(Modifier.height(12.dp))
    HorizontalDivider(
        color = SonarCyan.copy(alpha = 0.10f),
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.menu_developed_by),
        style = MaterialTheme.typography.labelSmall,
        color = TextDim,
        letterSpacing = 1.sp,
    )
    Spacer(Modifier.height(4.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.menu_website_link),
            style = MaterialTheme.typography.labelSmall,
            color = SonarCyan.copy(alpha = 0.55f),
            modifier = Modifier.clickable { uriHandler.openUri(URL_WEBSITE) }
        )
        Text(
            text = "·",
            style = MaterialTheme.typography.labelSmall,
            color = TextDim,
        )
        Text(
            text = stringResource(R.string.menu_apk_link),
            style = MaterialTheme.typography.labelSmall,
            color = SonarCyan.copy(alpha = 0.55f),
            modifier = Modifier.clickable { uriHandler.openUri(URL_APK) }
        )
    }
    Spacer(Modifier.height(16.dp))
}
```

- [ ] **Step 2.2: Verify compilation**

```bash
cd /home/cocodedk/0-projects/Battleship
./gradlew :app:compileDebugKotlin 2>&1 | tail -5
```

Expected: `BUILD SUCCESSFUL`

---

## Task 3 — Wire MenuFooter into MenuScreen

**Files:**
- Modify: `app/src/main/java/com/cocode/battleship/presentation/menu/MenuScreen.kt`

`MenuFooter.kt` is in the same package (`com.cocode.battleship.presentation.menu`), so no import needed.

- [ ] **Step 3.1: Add `MenuFooter()` call at the bottom of the Column**

In `MenuScreen.kt`, find the end of the `Column` block. The current last two lines inside the Column are:

```kotlin
            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.menu_game_mode),
                style = MaterialTheme.typography.labelSmall,
                color = TextDim,
                letterSpacing = 1.sp,
            )
        }
    }
}
```

Replace with:

```kotlin
            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.menu_game_mode),
                style = MaterialTheme.typography.labelSmall,
                color = TextDim,
                letterSpacing = 1.sp,
            )

            MenuFooter()
        }
    }
}
```

- [ ] **Step 3.2: Verify file stays under 200 lines**

```bash
wc -l app/src/main/java/com/cocode/battleship/presentation/menu/MenuScreen.kt
```

Expected: ≤ 200

- [ ] **Step 3.3: Compile**

```bash
./gradlew :app:compileDebugKotlin 2>&1 | tail -5
```

Expected: `BUILD SUCCESSFUL`

---

## Task 4 — Verify and commit

- [ ] **Step 4.1: Run full test suite**

```bash
cd /home/cocodedk/0-projects/Battleship
./gradlew test 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL` with 128 tests passing, 0 failures

- [ ] **Step 4.2: Commit**

```bash
git add app/src/main/res/values/strings.xml \
        app/src/main/java/com/cocode/battleship/presentation/menu/MenuFooter.kt \
        app/src/main/java/com/cocode/battleship/presentation/menu/MenuScreen.kt
git commit -m "feat: add author attribution footer to menu screen

Static footer below game-mode label: developer name, cocode.dk website
link, and GitHub Releases APK link. Uses LocalUriHandler — both links
open the system browser. APK link lands on the Releases page (not a
direct download) to prevent accidental installs.

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
