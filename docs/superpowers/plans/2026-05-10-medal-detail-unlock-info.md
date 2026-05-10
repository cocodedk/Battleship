# Medal Detail — Unlock Info Bottom Sheet Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Tap any medal tile on the Medal Registry screen to open a bottom sheet showing the badge art, rarity, and a plain-English description of how to earn it.

**Architecture:** Add `unlockHint: String` to the `Badge` domain enum (pure Kotlin, no Android imports). Move sheet-open state into `MedalsUiState`/`MedalsViewModel` per the project's ViewModel-hoisting rule. A new `MedalDetailSheet` composable renders a `ModalBottomSheet` driven by that state.

**Tech Stack:** Kotlin, Jetpack Compose, Material3 `ModalBottomSheet` (`@OptIn(ExperimentalMaterial3Api::class)`), JUnit4, `StateFlow`.

---

## File Map

| File | Action | Responsibility |
|------|--------|----------------|
| `domain/scoring/Badge.kt` | Modify | Add `unlockHint: String` to all 15 entries |
| `presentation/medals/MedalsUiState.kt` | Modify | Add `selectedItem: MedalItem? = null` |
| `presentation/medals/MedalsViewModel.kt` | Modify | Add `fun selectItem(item: MedalItem?)` |
| `presentation/medals/MedalDetailSheet.kt` | Create | `ModalBottomSheet` composable for medal detail |
| `presentation/medals/MedalsScreen.kt` | Modify | Wire click handler + render sheet from state |
| `res/values/strings.xml` | Modify | Add section label, rarity labels, earned-count format |
| `test/.../domain/scoring/BadgeTest.kt` | Modify | Add unlock-hint coverage test |
| `test/.../presentation/medals/MedalsViewModelTest.kt` | Create | Test `selectItem` state transitions |

---

### Task 1: Add `unlockHint` to the `Badge` enum

**Files:**
- Modify: `app/src/main/java/com/cocode/battleship/domain/scoring/Badge.kt`
- Modify: `app/src/test/java/com/cocode/battleship/domain/scoring/BadgeTest.kt`

- [ ] **Step 1: Add a failing test to `BadgeTest.kt`**

Open `app/src/test/java/com/cocode/battleship/domain/scoring/BadgeTest.kt` and add at the bottom of the class:

```kotlin
@Test fun `all badges have a non-blank unlockHint`() {
    Badge.entries.forEach { badge ->
        assertTrue(
            "Badge ${badge.name} has blank unlockHint",
            badge.unlockHint.isNotBlank()
        )
    }
}
```

- [ ] **Step 2: Run the test to confirm it fails**

```bash
./gradlew test --tests "com.cocode.battleship.domain.scoring.BadgeTest.all badges have a non-blank unlockHint" 2>&1 | tail -20
```

Expected: compilation error — `unlockHint` does not exist yet.

- [ ] **Step 3: Add `unlockHint` to `Badge.kt`**

Replace the entire `Badge.kt` file with:

```kotlin
package com.cocode.battleship.domain.scoring

import com.cocode.battleship.domain.model.ShipType

enum class Rarity { COMMON, RARE, EPIC, LEGENDARY }

enum class Badge(
    val displayName: String,
    val rarity: Rarity,
    val icon: String,
    val unlockHint: String
) {
    FIRST_BLOOD(
        "First Blood", Rarity.RARE, "🎯",
        "Hit the enemy on your very first shot"
    ),
    SHARPSHOOTER(
        "Sharpshooter", Rarity.RARE, "🏹",
        "Finish with 60% accuracy or better (minimum 10 shots)"
    ),
    DEAD_EYE(
        "Dead-Eye", Rarity.EPIC, "🎯🎯",
        "Finish with 80% accuracy or better (minimum 10 shots)"
    ),
    HOT_STREAK(
        "Hot Streak", Rarity.RARE, "🔥",
        "Land at least 5 hits in a row without missing"
    ),
    UNSTOPPABLE(
        "Unstoppable", Rarity.EPIC, "⚡",
        "Land at least 8 hits in a row without missing"
    ),
    FLAWLESS_VICTORY(
        "Flawless Victory", Rarity.EPIC, "👑",
        "Win with all 5 of your ships still afloat"
    ),
    PERFECT_GUNNER(
        "Perfect Gunner", Rarity.LEGENDARY, "💎",
        "Win without missing a single shot"
    ),
    LEVIATHAN_SLAYER(
        "Leviathan Slayer", Rarity.RARE, "🐋",
        "Sink the enemy Carrier as your first kill"
    ),
    SILENT_SERVICE(
        "Silent Service", Rarity.RARE, "🤫",
        "Keep your Submarine untouched for the entire game"
    ),
    LAST_STAND(
        "Last Stand", Rarity.RARE, "🛡️",
        "Win with only 1 ship surviving"
    ),
    DESTROYER_LIVES(
        "Destroyer Lives", Rarity.COMMON, "🚤",
        "Keep your Destroyer untouched for the entire game"
    ),
    SWIM_FOR_IT(
        "Swim for It", Rarity.RARE, "🏊",
        "Lose without landing a single hit on the enemy"
    ),
    FOG_OF_WAR(
        "Fog of War", Rarity.COMMON, "🌫️",
        "Miss at least 10 shots in a row"
    ),
    DEPTH_CHARGE_DIPLOMAT(
        "Depth Charge Diplomat", Rarity.COMMON, "💣",
        "Fire 100 or more shots in a single game"
    ),
    ON_FIRE(
        "On Fire", Rarity.EPIC, "🔥🔥",
        "Win at least 3 games in a row in the same session"
    );

    fun matches(stats: GameStats, sessionWinStreak: Int = 0): Boolean = when (this) {
        FIRST_BLOOD -> stats.firstShotHit
        SHARPSHOOTER -> stats.accuracy >= 0.60f && stats.totalShots >= 10
        DEAD_EYE -> stats.accuracy >= 0.80f && stats.totalShots >= 10
        HOT_STREAK -> stats.longestHitStreak >= 5
        UNSTOPPABLE -> stats.longestHitStreak >= 8
        FLAWLESS_VICTORY -> stats.outcome == GameOutcome.WIN && stats.survivingPlayerShips == 5
        PERFECT_GUNNER -> stats.outcome == GameOutcome.WIN && stats.misses == 0
        LEVIATHAN_SLAYER -> stats.firstEnemyShipSunkType == ShipType.CARRIER
        SILENT_SERVICE -> stats.playerShipEndStates[ShipType.SUBMARINE] == ShipEndState.UNTOUCHED
        LAST_STAND -> stats.outcome == GameOutcome.WIN && stats.survivingPlayerShips == 1
        DESTROYER_LIVES -> stats.playerShipEndStates[ShipType.DESTROYER] == ShipEndState.UNTOUCHED
        SWIM_FOR_IT -> stats.outcome == GameOutcome.LOSS && stats.hits == 0
        FOG_OF_WAR -> stats.longestMissStreak >= 10
        DEPTH_CHARGE_DIPLOMAT -> stats.totalShots >= 100
        ON_FIRE -> stats.outcome == GameOutcome.WIN && sessionWinStreak >= 3
    }
}
```

- [ ] **Step 4: Run the full BadgeTest suite**

```bash
./gradlew test --tests "com.cocode.battleship.domain.scoring.BadgeTest" 2>&1 | tail -20
```

Expected: all tests PASS (existing tests still pass, new hint test passes).

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/cocode/battleship/domain/scoring/Badge.kt \
        app/src/test/java/com/cocode/battleship/domain/scoring/BadgeTest.kt
git commit -m "feat: add unlockHint to Badge enum"
```

---

### Task 2: Add `selectedItem` state to `MedalsUiState` and `MedalsViewModel`

**Files:**
- Modify: `app/src/main/java/com/cocode/battleship/presentation/medals/MedalsUiState.kt`
- Modify: `app/src/main/java/com/cocode/battleship/presentation/medals/MedalsViewModel.kt`
- Create: `app/src/test/java/com/cocode/battleship/presentation/medals/MedalsViewModelTest.kt`

- [ ] **Step 1: Write a failing test for `selectItem` in a new test file**

Create `app/src/test/java/com/cocode/battleship/presentation/medals/MedalsViewModelTest.kt`:

```kotlin
package com.cocode.battleship.presentation.medals

import com.cocode.battleship.domain.scoring.Badge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MedalsViewModelTest {

    private fun makeViewModel(): MedalsViewModel {
        val storage = FakeMedalsStorageForVm()
        return MedalsViewModel(storage)
    }

    @Test fun `selectedItem is null on init`() {
        val vm = makeViewModel()
        assertNull(vm.state.value.selectedItem)
    }

    @Test fun `selectItem sets selectedItem in state`() {
        val vm = makeViewModel()
        val item = vm.state.value.items.first { it.badge == Badge.FIRST_BLOOD }
        vm.selectItem(item)
        assertEquals(item, vm.state.value.selectedItem)
    }

    @Test fun `selectItem with null clears selectedItem`() {
        val vm = makeViewModel()
        val item = vm.state.value.items.first { it.badge == Badge.FIRST_BLOOD }
        vm.selectItem(item)
        vm.selectItem(null)
        assertNull(vm.state.value.selectedItem)
    }
}

private class FakeMedalsStorageForVm : MedalsStorage {
    override fun load(): Map<Badge, Int> = emptyMap()
    override fun increment(badges: List<Badge>) = Unit
}
```

- [ ] **Step 2: Run the test to confirm it fails**

```bash
./gradlew test --tests "com.cocode.battleship.presentation.medals.MedalsViewModelTest" 2>&1 | tail -20
```

Expected: compilation error — `selectedItem` and `selectItem` do not exist yet.

- [ ] **Step 3: Add `selectedItem` to `MedalsUiState.kt`**

Replace `MedalsUiState.kt` with:

```kotlin
package com.cocode.battleship.presentation.medals

import com.cocode.battleship.domain.scoring.Badge

data class MedalItem(val badge: Badge, val count: Int) {
    val isEarned: Boolean get() = count > 0
}

data class MedalsUiState(
    val items: List<MedalItem>,
    val earnedCount: Int,
    val selectedItem: MedalItem? = null
)
```

- [ ] **Step 4: Add `selectItem()` to `MedalsViewModel.kt`**

Replace `MedalsViewModel.kt` with:

```kotlin
package com.cocode.battleship.presentation.medals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cocode.battleship.domain.scoring.Badge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MedalsViewModel(storage: MedalsStorage) : ViewModel() {

    private val _state = MutableStateFlow(buildState(storage.load()))
    val state: StateFlow<MedalsUiState> = _state.asStateFlow()

    fun selectItem(item: MedalItem?) {
        _state.value = _state.value.copy(selectedItem = item)
    }

    companion object {
        fun factory(storage: MedalsStorage): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    MedalsViewModel(storage) as T
            }
    }
}

private fun buildState(counts: Map<Badge, Int>): MedalsUiState {
    val items = Badge.entries.map { badge ->
        MedalItem(badge = badge, count = counts[badge] ?: 0)
    }
    return MedalsUiState(items = items, earnedCount = items.count { it.isEarned })
}
```

- [ ] **Step 5: Run the ViewModel tests**

```bash
./gradlew test --tests "com.cocode.battleship.presentation.medals.MedalsViewModelTest" 2>&1 | tail -20
```

Expected: all 3 tests PASS.

- [ ] **Step 6: Run the full test suite to catch regressions**

```bash
./gradlew test 2>&1 | tail -20
```

Expected: BUILD SUCCESSFUL, all tests pass.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/cocode/battleship/presentation/medals/MedalsUiState.kt \
        app/src/main/java/com/cocode/battleship/presentation/medals/MedalsViewModel.kt \
        app/src/test/java/com/cocode/battleship/presentation/medals/MedalsViewModelTest.kt
git commit -m "feat: add selectedItem state and selectItem() to medals ViewModel"
```

---

### Task 3: Add string resources for the bottom sheet

**Files:**
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Add new strings inside the `<!-- Medals Screen -->` section**

In `app/src/main/res/values/strings.xml`, locate the existing `<!-- Medals Screen -->` block (currently ends at `medals_back`) and add the following entries immediately after `medals_back`:

```xml
    <string name="medal_detail_how_to_earn">HOW TO EARN</string>
    <string name="medal_detail_rarity_common">◆ COMMON</string>
    <string name="medal_detail_rarity_rare">◆ RARE</string>
    <string name="medal_detail_rarity_epic">◆ EPIC</string>
    <string name="medal_detail_rarity_legendary">◆ LEGENDARY</string>
    <string name="medal_detail_earned_count">× %1$d earned</string>
```

- [ ] **Step 2: Build to verify no XML errors**

```bash
./gradlew assembleDebug 2>&1 | tail -20
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/res/values/strings.xml
git commit -m "feat: add string resources for medal detail bottom sheet"
```

---

### Task 4: Create `MedalDetailSheet.kt`

**Files:**
- Create: `app/src/main/java/com/cocode/battleship/presentation/medals/MedalDetailSheet.kt`

- [ ] **Step 1: Create the file**

```kotlin
package com.cocode.battleship.presentation.medals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.domain.scoring.Rarity
import com.cocode.battleship.ui.theme.AmberWarning
import com.cocode.battleship.ui.theme.NavyCard
import com.cocode.battleship.ui.theme.TextPrimary
import com.cocode.battleship.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedalDetailSheet(item: MedalItem, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val rarityColor = rarityColor(item.badge.rarity)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NavyCard,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            MedalCanvas(
                badge = item.badge,
                count = item.count,
                modifier = Modifier.size(120.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = item.badge.displayName.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = rarityColor,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = rarityLabel(item.badge.rarity),
                style = MaterialTheme.typography.labelSmall,
                color = rarityColor.copy(alpha = 0.6f),
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.medal_detail_how_to_earn),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                letterSpacing = 3.sp,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = item.badge.unlockHint,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth(),
            )
            if (item.isEarned) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.medal_detail_earned_count, item.count),
                    style = MaterialTheme.typography.labelMedium,
                    color = AmberWarning,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
            }
        }
    }
}

@Composable
private fun rarityLabel(rarity: Rarity): String = when (rarity) {
    Rarity.COMMON    -> stringResource(R.string.medal_detail_rarity_common)
    Rarity.RARE      -> stringResource(R.string.medal_detail_rarity_rare)
    Rarity.EPIC      -> stringResource(R.string.medal_detail_rarity_epic)
    Rarity.LEGENDARY -> stringResource(R.string.medal_detail_rarity_legendary)
}
```

- [ ] **Step 2: Build to verify it compiles**

```bash
./gradlew assembleDebug 2>&1 | tail -20
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/cocode/battleship/presentation/medals/MedalDetailSheet.kt
git commit -m "feat: add MedalDetailSheet bottom sheet composable"
```

---

### Task 5: Wire click handler and sheet into `MedalsScreen.kt`

**Files:**
- Modify: `app/src/main/java/com/cocode/battleship/presentation/medals/MedalsScreen.kt`

- [ ] **Step 1: Replace `MedalsScreen.kt` with the wired-up version**

```kotlin
package com.cocode.battleship.presentation.medals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.ui.theme.DeepNavy
import com.cocode.battleship.ui.theme.NavySurface
import com.cocode.battleship.ui.theme.PhosphorGreen
import com.cocode.battleship.ui.theme.SonarCyan

private const val SYM_SECTION = "◆"
private const val SYM_ARROW = "▶"

@Composable
fun MedalsScreen(viewModel: MedalsViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepNavy, NavySurface, DeepNavy)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MedalsHeader(earnedCount = state.earnedCount)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.items) { item ->
                    MedalCell(item = item, onClick = { viewModel.selectItem(item) })
                }
            }
            Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, SonarCyan.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SonarCyan),
                ) {
                    Text(
                        text = stringResource(R.string.medals_back),
                        letterSpacing = 2.sp,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }

    state.selectedItem?.let { item ->
        MedalDetailSheet(item = item, onDismiss = { viewModel.selectItem(null) })
    }
}

@Composable
private fun MedalsHeader(earnedCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "$SYM_SECTION  ${stringResource(R.string.medals_title)}",
            style = MaterialTheme.typography.titleLarge,
            color = SonarCyan,
            letterSpacing = 3.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "$SYM_ARROW  ${stringResource(R.string.medals_earned_format, earnedCount)}",
            style = MaterialTheme.typography.labelMedium,
            color = PhosphorGreen,
            letterSpacing = 1.5.sp,
        )
    }
}

@Composable
private fun MedalCell(item: MedalItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        MedalCanvas(
            badge = item.badge,
            count = item.count,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = item.badge.displayName.uppercase(),
            fontSize = 8.sp,
            letterSpacing = 0.4.sp,
            color = rarityColor(item.badge.rarity).copy(alpha = if (item.isEarned) 0.88f else 0.28f),
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 10.sp,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
```

- [ ] **Step 2: Build and run all tests**

```bash
./gradlew test 2>&1 | tail -20
```

Expected: BUILD SUCCESSFUL, all tests pass.

- [ ] **Step 3: Build debug APK**

```bash
./gradlew assembleDebug 2>&1 | tail -10
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/cocode/battleship/presentation/medals/MedalsScreen.kt
git commit -m "feat: tap medal tile to open detail bottom sheet"
```

---

### Task 6: Final smoke check

**Files:** none

- [ ] **Step 1: Run the full build smoke**

```bash
./gradlew buildSmoke 2>&1 | tail -30
```

Expected: BUILD SUCCESSFUL — debug APK built, all unit tests pass, lint clean.

- [ ] **Step 2: Manual verify on device or emulator**

Install the debug APK and verify:
1. Medal Registry grid loads correctly (all 15 tiles visible)
2. Tapping any locked medal opens the bottom sheet with lock art, name, rarity chip, and hint text
3. Tapping any earned medal shows the hex art, name, rarity chip, hint, and earned count
4. Swiping down or tapping outside the sheet dismisses it
5. Tapping rapidly between tiles does not crash or show stale data
6. Rotation while sheet is open: sheet re-opens correctly (ViewModel survives rotation)
