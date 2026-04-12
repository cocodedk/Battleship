# Medals Screen Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a dedicated Medals screen showing all 15 badges as custom Canvas-drawn hexagonal art with per-badge earn counts, backed by a migrated SharedPreferences data layer.

**Architecture:** `MedalsStorage` (interface + SharedPreferences impl) stores badge counts as an encoded string; `SessionStats.record()` calls `medalsStorage.increment()` at game-over; `MedalsViewModel` exposes `StateFlow<MedalsUiState>`; `MedalsScreen` renders a `LazyVerticalGrid` of `MedalCanvas` composables drawn on Canvas. `MedalSymbols.kt` contains one `DrawScope` extension function per badge.

**Tech Stack:** Kotlin 2.2.10, Jetpack Compose, Canvas DrawScope, AndroidX Navigation, Material3, JUnit4

---

## File Map

| Action | File | Responsibility |
|--------|------|----------------|
| Create | `presentation/medals/MedalsStorage.kt` | interface: `load()`, `increment()` |
| Create | `presentation/medals/SharedPreferencesMedalsStorage.kt` | impl + `Set<Badge>` migration |
| Create | `presentation/medals/MedalsUiState.kt` | `MedalItem`, `MedalsUiState` |
| Create | `presentation/medals/MedalsViewModel.kt` | `StateFlow<MedalsUiState>` + factory |
| Create | `presentation/medals/MedalSymbols.kt` | 15 badge symbol draw functions |
| Create | `presentation/medals/MedalCanvas.kt` | hex frame composable + lock/count overlay |
| Create | `presentation/medals/MedalsScreen.kt` | grid screen composable |
| Create | `test/.../medals/MedalsStorageTest.kt` | contract tests via `FakeMedalsStorage` |
| Modify | `presentation/navigation/Screen.kt` | add `Screen.Medals` |
| Modify | `presentation/navigation/BattleshipNavHost.kt` | add medals route + storage |
| Modify | `presentation/menu/MenuScreen.kt` | add `onViewMedals` param + button |
| Modify | `presentation/game/SessionStats.kt` | add `medalsStorage` param + call in `record()` |
| Modify | `MainActivity.kt` | pass `SharedPreferencesMedalsStorage` to `SessionStats.initialize` |
| Modify | `presentation/stats/StatsScreen.kt` | remove `BadgeShowcase` |
| Modify | `res/values/strings.xml` | add medals screen strings |

All source files live under `app/src/main/java/com/cocode/battleship/`.
Test files live under `app/src/test/java/com/cocode/battleship/`.

---

## Task 1: MedalsStorage interface + contract tests

**Files:**
- Create: `presentation/medals/MedalsStorage.kt`
- Create: `test/presentation/medals/MedalsStorageTest.kt`

- [ ] **Step 1: Create MedalsStorage.kt**

```kotlin
// app/src/main/java/com/cocode/battleship/presentation/medals/MedalsStorage.kt
package com.cocode.battleship.presentation.medals

import com.cocode.battleship.domain.scoring.Badge

interface MedalsStorage {
    /** Returns a map of badge → total times earned (absent = 0). */
    fun load(): Map<Badge, Int>
    /** Increments the count for each badge in the list. */
    fun increment(badges: List<Badge>)
}
```

- [ ] **Step 2: Write failing tests**

```kotlin
// app/src/test/java/com/cocode/battleship/presentation/medals/MedalsStorageTest.kt
package com.cocode.battleship.presentation.medals

import com.cocode.battleship.domain.scoring.Badge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class MedalsStorageTest {

    private lateinit var storage: FakeMedalsStorage

    @Before
    fun setUp() {
        storage = FakeMedalsStorage()
    }

    @Test
    fun `load returns empty map when nothing incremented`() {
        assertEquals(emptyMap<Badge, Int>(), storage.load())
    }

    @Test
    fun `increment once sets count to 1`() {
        storage.increment(listOf(Badge.FIRST_BLOOD))
        assertEquals(1, storage.load()[Badge.FIRST_BLOOD])
    }

    @Test
    fun `increment twice accumulates to 2`() {
        storage.increment(listOf(Badge.FIRST_BLOOD))
        storage.increment(listOf(Badge.FIRST_BLOOD))
        assertEquals(2, storage.load()[Badge.FIRST_BLOOD])
    }

    @Test
    fun `increment multiple badges in one call counts each`() {
        storage.increment(listOf(Badge.FIRST_BLOOD, Badge.FIRST_BLOOD, Badge.ON_FIRE))
        assertEquals(2, storage.load()[Badge.FIRST_BLOOD])
        assertEquals(1, storage.load()[Badge.ON_FIRE])
    }

    @Test
    fun `unearned badge is absent from load result`() {
        storage.increment(listOf(Badge.FIRST_BLOOD))
        assertNull(storage.load()[Badge.SHARPSHOOTER])
    }

    @Test
    fun `empty increment list has no effect`() {
        storage.increment(emptyList())
        assertEquals(emptyMap<Badge, Int>(), storage.load())
    }
}

private class FakeMedalsStorage : MedalsStorage {
    private val counts: MutableMap<Badge, Int> = mutableMapOf()
    override fun load(): Map<Badge, Int> = counts.toMap()
    override fun increment(badges: List<Badge>) {
        badges.forEach { counts[it] = (counts[it] ?: 0) + 1 }
    }
}
```

- [ ] **Step 3: Run tests to verify they fail**

```bash
./gradlew test --tests "*.MedalsStorageTest" 2>&1 | tail -20
```

Expected: FAILED — `MedalsStorage` does not exist yet.

- [ ] **Step 4: Run tests now that the interface exists**

```bash
./gradlew test --tests "*.MedalsStorageTest" 2>&1 | tail -20
```

Expected: All 6 tests PASS (tests use `FakeMedalsStorage` which is defined in the test file itself).

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/cocode/battleship/presentation/medals/MedalsStorage.kt \
        app/src/test/java/com/cocode/battleship/presentation/medals/MedalsStorageTest.kt
git commit -m "feat: add MedalsStorage interface and contract tests"
```

---

## Task 2: SharedPreferencesMedalsStorage with migration

**Files:**
- Create: `presentation/medals/SharedPreferencesMedalsStorage.kt`

SharedPreferences file name is `"career_stats"` (same file as `SharedPreferencesSessionStatsStorage` — different keys, no conflict). Migration reads old `"earned_badges"` StringSet (count=1 per badge), writes to `"badge_counts"`, then removes the old key.

- [ ] **Step 1: Create SharedPreferencesMedalsStorage.kt**

```kotlin
// app/src/main/java/com/cocode/battleship/presentation/medals/SharedPreferencesMedalsStorage.kt
package com.cocode.battleship.presentation.medals

import android.content.Context
import com.cocode.battleship.domain.scoring.Badge

class SharedPreferencesMedalsStorage(context: Context) : MedalsStorage {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun load(): Map<Badge, Int> {
        migrateLegacyIfNeeded()
        val raw = prefs.getString(KEY_BADGE_COUNTS, "") ?: ""
        if (raw.isBlank()) return emptyMap()
        return raw.split(",").mapNotNull { entry ->
            val parts = entry.split(":")
            if (parts.size != 2) return@mapNotNull null
            val badge = Badge.entries.find { it.name == parts[0] } ?: return@mapNotNull null
            val count = parts[1].toIntOrNull() ?: return@mapNotNull null
            badge to count
        }.toMap()
    }

    override fun increment(badges: List<Badge>) {
        if (badges.isEmpty()) return
        val current = load().toMutableMap()
        badges.forEach { current[it] = (current[it] ?: 0) + 1 }
        prefs.edit().putString(KEY_BADGE_COUNTS, encode(current)).commit()
    }

    private fun migrateLegacyIfNeeded() {
        if (!prefs.contains(KEY_EARNED_BADGES)) return
        val legacy = prefs.getStringSet(KEY_EARNED_BADGES, emptySet()) ?: emptySet()
        val counts = legacy.mapNotNull { name ->
            Badge.entries.find { it.name == name }?.let { it to 1 }
        }.toMap()
        prefs.edit()
            .putString(KEY_BADGE_COUNTS, encode(counts))
            .remove(KEY_EARNED_BADGES)
            .commit()
    }

    private fun encode(counts: Map<Badge, Int>): String =
        counts.entries.joinToString(",") { "${it.key.name}:${it.value}" }

    companion object {
        const val PREFS_NAME = "career_stats"
        const val KEY_BADGE_COUNTS = "badge_counts"
        private const val KEY_EARNED_BADGES = "earned_badges"
    }
}
```

- [ ] **Step 2: Build check**

```bash
./gradlew assembleDebug 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/cocode/battleship/presentation/medals/SharedPreferencesMedalsStorage.kt
git commit -m "feat: add SharedPreferencesMedalsStorage with Set<Badge> migration"
```

---

## Task 3: MedalsUiState and MedalsViewModel

**Files:**
- Create: `presentation/medals/MedalsUiState.kt`
- Create: `presentation/medals/MedalsViewModel.kt`

- [ ] **Step 1: Create MedalsUiState.kt**

```kotlin
// app/src/main/java/com/cocode/battleship/presentation/medals/MedalsUiState.kt
package com.cocode.battleship.presentation.medals

import com.cocode.battleship.domain.scoring.Badge

data class MedalItem(val badge: Badge, val count: Int) {
    val isEarned: Boolean get() = count > 0
}

data class MedalsUiState(
    val items: List<MedalItem>,
    val earnedCount: Int
)
```

- [ ] **Step 2: Create MedalsViewModel.kt**

```kotlin
// app/src/main/java/com/cocode/battleship/presentation/medals/MedalsViewModel.kt
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

- [ ] **Step 3: Build and run all tests**

```bash
./gradlew test 2>&1 | tail -15
```

Expected: `BUILD SUCCESSFUL`, all existing tests PASS.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/cocode/battleship/presentation/medals/MedalsUiState.kt \
        app/src/main/java/com/cocode/battleship/presentation/medals/MedalsViewModel.kt
git commit -m "feat: add MedalsUiState and MedalsViewModel"
```

---

## Task 4: Wire badge-count increment into SessionStats

**Files:**
- Modify: `presentation/game/SessionStats.kt`

Add an optional `medalsStorage` parameter to `initialize()`. Call `medalsStorage?.increment(earnedBadges)` in `record()`. The default value `null` keeps all existing call sites and tests unbroken.

- [ ] **Step 1: Run existing SessionStats tests to confirm baseline**

```bash
./gradlew test --tests "*.SessionStatsTest" 2>&1 | tail -10
```

Expected: 4 tests PASS.

- [ ] **Step 2: Modify SessionStats.kt**

Replace the `initialize` function and add the `medalsStorage` field:

```kotlin
// app/src/main/java/com/cocode/battleship/presentation/game/SessionStats.kt
package com.cocode.battleship.presentation.game

import com.cocode.battleship.domain.scoring.Badge
import com.cocode.battleship.presentation.medals.MedalsStorage

data class SessionStatsSnapshot(
    val gamesPlayed: Int = 0,
    val totalWins: Int = 0,
    val currentWinStreak: Int = 0,
    val longestWinStreak: Int = 0,
    val bestScore: Int = 0,
    val earnedBadges: Set<Badge> = emptySet()
)

interface SessionStatsStorage {
    fun load(): SessionStatsSnapshot
    fun save(snapshot: SessionStatsSnapshot)
}

object SessionStats {
    private var storage: SessionStatsStorage? = null
    private var medalsStorage: MedalsStorage? = null

    var gamesPlayed: Int = 0
        private set
    var totalWins: Int = 0
        private set
    var currentWinStreak: Int = 0
        private set
    var longestWinStreak: Int = 0
        private set
    var bestScore: Int = 0
        private set
    private val _allEarnedBadges: MutableSet<Badge> = mutableSetOf()
    val allEarnedBadges: Set<Badge> get() = _allEarnedBadges

    fun initialize(storage: SessionStatsStorage, medalsStorage: MedalsStorage? = null) {
        this.storage = storage
        this.medalsStorage = medalsStorage
        restore(storage.load())
    }

    fun record(score: Int, isWin: Boolean, earnedBadges: List<Badge> = emptyList()) {
        gamesPlayed++
        if (isWin) {
            totalWins++
            currentWinStreak++
            if (currentWinStreak > longestWinStreak) longestWinStreak = currentWinStreak
        } else {
            currentWinStreak = 0
        }
        if (score > bestScore) bestScore = score
        _allEarnedBadges.addAll(earnedBadges)
        if (earnedBadges.isNotEmpty()) medalsStorage?.increment(earnedBadges)
        persist()
    }

    fun reset() {
        restore(SessionStatsSnapshot())
        persist()
    }

    internal fun snapshot(): SessionStatsSnapshot = SessionStatsSnapshot(
        gamesPlayed = gamesPlayed,
        totalWins = totalWins,
        currentWinStreak = currentWinStreak,
        longestWinStreak = longestWinStreak,
        bestScore = bestScore,
        earnedBadges = _allEarnedBadges.toSet()
    )

    private fun restore(snapshot: SessionStatsSnapshot) {
        gamesPlayed = snapshot.gamesPlayed
        totalWins = snapshot.totalWins
        currentWinStreak = snapshot.currentWinStreak
        longestWinStreak = snapshot.longestWinStreak
        bestScore = snapshot.bestScore
        _allEarnedBadges.clear()
        _allEarnedBadges.addAll(snapshot.earnedBadges)
    }

    private fun persist() {
        storage?.save(snapshot())
    }
}
```

- [ ] **Step 3: Run SessionStats tests — must still pass**

```bash
./gradlew test --tests "*.SessionStatsTest" 2>&1 | tail -10
```

Expected: 4 tests PASS (default `null` medalsStorage keeps them unchanged).

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/cocode/battleship/presentation/game/SessionStats.kt
git commit -m "feat: wire MedalsStorage into SessionStats.record() for badge count tracking"
```

---

## Task 5: Pass MedalsStorage from MainActivity

**Files:**
- Modify: `MainActivity.kt`

- [ ] **Step 1: Modify MainActivity.kt**

```kotlin
// app/src/main/java/com/cocode/battleship/MainActivity.kt
package com.cocode.battleship

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.cocode.battleship.presentation.medals.SharedPreferencesMedalsStorage
import com.cocode.battleship.presentation.navigation.BattleshipNavHost
import com.cocode.battleship.presentation.game.SessionStats
import com.cocode.battleship.presentation.game.SharedPreferencesSessionStatsStorage
import com.cocode.battleship.ui.theme.BattleshipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionStats.initialize(
            storage = SharedPreferencesSessionStatsStorage(applicationContext),
            medalsStorage = SharedPreferencesMedalsStorage(applicationContext)
        )
        enableEdgeToEdge()
        setContent {
            BattleshipTheme {
                BattleshipNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
```

- [ ] **Step 2: Build and run all tests**

```bash
./gradlew test 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`, all tests PASS.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/cocode/battleship/MainActivity.kt
git commit -m "feat: initialize MedalsStorage in MainActivity"
```

---

## Task 6: MedalSymbols — Canvas draw functions for all 15 badges

**Files:**
- Create: `presentation/medals/MedalSymbols.kt`

One `internal fun DrawScope.drawBadgeSymbol(...)` dispatches to 15 private draw functions. Symbol coordinates use `r` (inner radius supplied by caller) so symbols scale correctly regardless of badge size.

- [ ] **Step 1: Create MedalSymbols.kt**

```kotlin
// app/src/main/java/com/cocode/battleship/presentation/medals/MedalSymbols.kt
package com.cocode.battleship.presentation.medals

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.cocode.battleship.domain.scoring.Badge

internal fun DrawScope.drawBadgeSymbol(badge: Badge, cx: Float, cy: Float, r: Float, color: Color) {
    val stroke = Stroke(1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    when (badge) {
        Badge.FIRST_BLOOD           -> drawCrosshair(cx, cy, r, color, stroke)
        Badge.SHARPSHOOTER          -> drawArrow(cx, cy, r, color, stroke)
        Badge.DEAD_EYE              -> drawDoubleTarget(cx, cy, r, color, stroke)
        Badge.HOT_STREAK            -> drawLightning(cx, cy, r, color)
        Badge.UNSTOPPABLE           -> drawDoubleLightning(cx, cy, r, color)
        Badge.FLAWLESS_VICTORY      -> drawCrown(cx, cy, r, color, stroke)
        Badge.PERFECT_GUNNER        -> drawDiamond(cx, cy, r, color, stroke)
        Badge.LEVIATHAN_SLAYER      -> drawWaveArc(cx, cy, r, color, stroke)
        Badge.SILENT_SERVICE        -> drawSubmarine(cx, cy, r, color, stroke)
        Badge.LAST_STAND            -> drawShield(cx, cy, r, color, stroke)
        Badge.DESTROYER_LIVES       -> drawShipSilhouette(cx, cy, r, color, stroke)
        Badge.SWIM_FOR_IT           -> drawSwimWaves(cx, cy, r, color, stroke)
        Badge.FOG_OF_WAR            -> drawScatterDots(cx, cy, r, color)
        Badge.DEPTH_CHARGE_DIPLOMAT -> drawBomb(cx, cy, r, color, stroke)
        Badge.ON_FIRE               -> drawFlames(cx, cy, r, color, stroke)
    }
}

private fun DrawScope.drawCrosshair(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    val gap = r * 0.36f
    drawCircle(color, r * 0.3f, Offset(cx, cy), style = stroke)
    drawLine(color, Offset(cx, cy - r), Offset(cx, cy - gap), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx, cy + gap), Offset(cx, cy + r), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx - r, cy), Offset(cx - gap, cy), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx + gap, cy), Offset(cx + r, cy), stroke.width, StrokeCap.Round)
    drawCircle(color, r * 0.08f, Offset(cx, cy))
}

private fun DrawScope.drawArrow(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawLine(color, Offset(cx - r, cy), Offset(cx + r * 0.55f, cy), stroke.width, StrokeCap.Round)
    val head = Path().apply {
        moveTo(cx + r, cy); lineTo(cx + r * 0.5f, cy - r * 0.42f); lineTo(cx + r * 0.5f, cy + r * 0.42f); close()
    }
    drawPath(head, color)
    drawLine(color, Offset(cx - r, cy), Offset(cx - r * 0.58f, cy - r * 0.32f), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx - r, cy), Offset(cx - r * 0.58f, cy + r * 0.32f), stroke.width, StrokeCap.Round)
}

private fun DrawScope.drawDoubleTarget(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawCircle(color, r * 0.52f, Offset(cx, cy), style = stroke)
    drawCircle(color, r * 0.22f, Offset(cx, cy), style = stroke)
    drawCircle(color, r * 0.07f, Offset(cx, cy))
}

private fun DrawScope.drawLightning(cx: Float, cy: Float, r: Float, color: Color) {
    val bolt = Path().apply {
        moveTo(cx + r * 0.2f, cy - r)
        lineTo(cx - r * 0.15f, cy - r * 0.05f)
        lineTo(cx + r * 0.18f, cy - r * 0.05f)
        lineTo(cx - r * 0.2f, cy + r)
        lineTo(cx + r * 0.1f, cy + r * 0.05f)
        lineTo(cx - r * 0.12f, cy + r * 0.05f)
        close()
    }
    drawPath(bolt, color)
}

private fun DrawScope.drawDoubleLightning(cx: Float, cy: Float, r: Float, color: Color) {
    val bolt1 = Path().apply {
        moveTo(cx - r * 0.1f, cy - r)
        lineTo(cx - r * 0.4f, cy - r * 0.05f)
        lineTo(cx - r * 0.12f, cy - r * 0.05f)
        lineTo(cx - r * 0.42f, cy + r)
        lineTo(cx - r * 0.2f, cy + r * 0.05f)
        lineTo(cx - r * 0.4f, cy + r * 0.05f)
        close()
    }
    val bolt2 = Path().apply {
        moveTo(cx + r * 0.32f, cy - r)
        lineTo(cx + r * 0.02f, cy - r * 0.05f)
        lineTo(cx + r * 0.3f, cy - r * 0.05f)
        lineTo(cx + r * 0.0f, cy + r)
        lineTo(cx + r * 0.22f, cy + r * 0.05f)
        lineTo(cx + r * 0.02f, cy + r * 0.05f)
        close()
    }
    drawPath(bolt1, color.copy(alpha = color.alpha * 0.65f))
    drawPath(bolt2, color)
}

private fun DrawScope.drawCrown(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    val base = cy + r * 0.55f
    val crownPath = Path().apply {
        moveTo(cx - r, base)
        lineTo(cx - r, cy - r * 0.15f)
        lineTo(cx - r * 0.32f, cy + r * 0.15f)
        lineTo(cx, cy - r)
        lineTo(cx + r * 0.32f, cy + r * 0.15f)
        lineTo(cx + r, cy - r * 0.15f)
        lineTo(cx + r, base)
    }
    drawPath(crownPath, color, style = stroke)
    drawLine(color, Offset(cx - r, base), Offset(cx + r, base), stroke.width, StrokeCap.Round)
    drawCircle(color, r * 0.1f, Offset(cx, cy - r))
    drawCircle(color, r * 0.08f, Offset(cx - r, cy - r * 0.15f))
    drawCircle(color, r * 0.08f, Offset(cx + r, cy - r * 0.15f))
}

private fun DrawScope.drawDiamond(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    val gem = Path().apply {
        moveTo(cx, cy - r); lineTo(cx + r, cy); lineTo(cx, cy + r); lineTo(cx - r, cy); close()
    }
    val topFace = Path().apply {
        moveTo(cx, cy - r); lineTo(cx + r, cy); lineTo(cx, cy - r * 0.1f); lineTo(cx - r, cy); close()
    }
    drawPath(topFace, color.copy(alpha = color.alpha * 0.22f))
    drawPath(gem, color, style = stroke)
    drawLine(color.copy(alpha = color.alpha * 0.75f), Offset(cx - r, cy), Offset(cx + r, cy), 0.8.dp.toPx())
}

private fun DrawScope.drawWaveArc(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawLine(color, Offset(cx - r, cy + r * 0.25f), Offset(cx + r, cy + r * 0.25f), stroke.width, StrokeCap.Round)
    val arc = Path().apply {
        moveTo(cx - r * 0.72f, cy + r * 0.25f)
        cubicTo(cx - r * 0.3f, cy - r * 0.85f, cx + r * 0.3f, cy - r * 0.85f, cx + r * 0.72f, cy + r * 0.25f)
    }
    drawPath(arc, color, style = stroke)
    drawCircle(color, r * 0.07f, Offset(cx - r * 0.78f, cy + r * 0.05f))
    drawCircle(color, r * 0.07f, Offset(cx + r * 0.78f, cy + r * 0.05f))
}

private fun DrawScope.drawSubmarine(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawOval(color, topLeft = Offset(cx - r, cy - r * 0.28f), size = Size(r * 2f, r * 0.56f), style = stroke)
    drawRect(color, topLeft = Offset(cx - r * 0.22f, cy - r * 0.78f), size = Size(r * 0.44f, r * 0.52f), style = stroke)
    drawLine(color, Offset(cx + r * 0.12f, cy - r * 0.78f), Offset(cx + r * 0.12f, cy - r * 1.05f), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(cx + r * 0.12f, cy - r * 1.05f), Offset(cx + r * 0.38f, cy - r * 1.05f), stroke.width, StrokeCap.Round)
}

private fun DrawScope.drawShield(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    val shield = Path().apply {
        moveTo(cx - r, cy - r * 0.65f)
        lineTo(cx + r, cy - r * 0.65f)
        lineTo(cx + r, cy + r * 0.15f)
        cubicTo(cx + r, cy + r * 0.85f, cx, cy + r, cx, cy + r)
        cubicTo(cx, cy + r, cx - r, cy + r * 0.85f, cx - r, cy + r * 0.15f)
        close()
    }
    drawPath(shield, color, style = stroke)
    drawLine(color, Offset(cx, cy - r * 0.65f), Offset(cx, cy + r * 0.65f), stroke.width * 0.75f, StrokeCap.Round)
}

private fun DrawScope.drawShipSilhouette(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawRect(color, topLeft = Offset(cx - r, cy - r * 0.22f), size = Size(r * 2f, r * 0.44f), style = stroke)
    val bow = Path().apply {
        moveTo(cx + r, cy - r * 0.22f); lineTo(cx + r, cy + r * 0.22f); lineTo(cx + r * 1.38f, cy); close()
    }
    drawPath(bow, color)
    drawRect(color, topLeft = Offset(cx - r * 0.48f, cy - r * 0.68f), size = Size(r * 0.96f, r * 0.46f), style = stroke)
    drawLine(color, Offset(cx, cy - r * 0.68f), Offset(cx, cy - r), stroke.width * 0.75f, StrokeCap.Round)
}

private fun DrawScope.drawSwimWaves(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    for (dy in listOf(-r * 0.28f, r * 0.28f)) {
        val wave = Path().apply {
            moveTo(cx - r, cy + dy)
            cubicTo(cx - r * 0.5f, cy + dy - r * 0.32f, cx, cy + dy + r * 0.32f, cx + r * 0.5f, cy + dy - r * 0.32f)
            cubicTo(cx + r * 0.5f, cy + dy - r * 0.32f, cx + r * 0.75f, cy + dy, cx + r, cy + dy)
        }
        drawPath(wave, color, style = stroke)
    }
}

private fun DrawScope.drawScatterDots(cx: Float, cy: Float, r: Float, color: Color) {
    val positions = listOf(
        Offset(cx, cy) to r * 0.13f,
        Offset(cx - r * 0.5f, cy - r * 0.5f) to r * 0.09f,
        Offset(cx + r * 0.52f, cy - r * 0.42f) to r * 0.11f,
        Offset(cx - r * 0.42f, cy + r * 0.52f) to r * 0.09f,
        Offset(cx + r * 0.55f, cy + r * 0.46f) to r * 0.08f,
    )
    positions.forEach { (pos, rad) -> drawCircle(color, rad, pos) }
}

private fun DrawScope.drawBomb(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    drawCircle(color, r * 0.52f, Offset(cx, cy + r * 0.22f), style = stroke)
    val fuse = Path().apply {
        moveTo(cx, cy - r * 0.3f)
        cubicTo(cx + r * 0.28f, cy - r * 0.7f, cx + r * 0.58f, cy - r * 0.52f, cx + r * 0.5f, cy - r)
    }
    drawPath(fuse, color, style = stroke)
    drawCircle(color, r * 0.09f, Offset(cx + r * 0.5f, cy - r))
}

private fun DrawScope.drawFlames(cx: Float, cy: Float, r: Float, color: Color, stroke: Stroke) {
    val flame1 = Path().apply {
        moveTo(cx + r * 0.28f, cy + r)
        cubicTo(cx + r * 0.82f, cy + r * 0.38f, cx + r, cy - r * 0.22f, cx + r * 0.38f, cy - r)
        cubicTo(cx + r * 0.28f, cy - r * 0.32f, cx + r * 0.14f, cy + r * 0.32f, cx + r * 0.28f, cy + r)
    }
    val flame2 = Path().apply {
        moveTo(cx - r * 0.28f, cy + r)
        cubicTo(cx - r * 0.82f, cy + r * 0.38f, cx - r, cy - r * 0.22f, cx - r * 0.38f, cy - r)
        cubicTo(cx - r * 0.28f, cy - r * 0.32f, cx - r * 0.14f, cy + r * 0.32f, cx - r * 0.28f, cy + r)
    }
    drawPath(flame2, color.copy(alpha = color.alpha * 0.55f), style = stroke)
    drawPath(flame1, color, style = stroke)
    drawPath(flame1, color.copy(alpha = color.alpha * 0.14f))
}
```

- [ ] **Step 2: Build check**

```bash
./gradlew assembleDebug 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/cocode/battleship/presentation/medals/MedalSymbols.kt
git commit -m "feat: add Canvas draw functions for all 15 badge symbols"
```

---

## Task 7: MedalCanvas composable

**Files:**
- Create: `presentation/medals/MedalCanvas.kt`

Draws the hex frame (outer ring, inner ring, vertex accents, optional glow for LEGENDARY), then either the badge symbol or a lock icon. An amber `×N` count bubble is composited on top using a `Box` overlay.

- [ ] **Step 1: Create MedalCanvas.kt**

```kotlin
// app/src/main/java/com/cocode/battleship/presentation/medals/MedalCanvas.kt
package com.cocode.battleship.presentation.medals

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.domain.scoring.Badge
import com.cocode.battleship.domain.scoring.Rarity
import kotlin.math.cos
import kotlin.math.sin

private val RarityCommon    = Color(0xFFB87333)
private val RarityRare      = Color(0xFF7EB8D4)
private val RarityEpic      = Color(0xFFFFD700)
private val RarityLegendary = Color(0xFF00D4FF)

internal fun rarityColor(rarity: Rarity): Color = when (rarity) {
    Rarity.COMMON    -> RarityCommon
    Rarity.RARE      -> RarityRare
    Rarity.EPIC      -> RarityEpic
    Rarity.LEGENDARY -> RarityLegendary
}

@Composable
fun MedalCanvas(badge: Badge, count: Int, modifier: Modifier = Modifier) {
    val isEarned = count > 0
    val color = rarityColor(badge.rarity)
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val outerR = size.minDimension * 0.46f
            val drawAlpha = if (isEarned) 1f else 0.28f
            val c = color.copy(alpha = drawAlpha)
            val strokeOuter = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

            drawPath(hexPath(cx, cy, outerR), Color(0xFF071828).copy(alpha = drawAlpha))
            drawPath(hexPath(cx, cy, outerR), c, style = strokeOuter)
            drawPath(hexPath(cx, cy, outerR * 0.82f), c.copy(alpha = c.alpha * 0.45f), style = Stroke(1.dp.toPx()))
            drawVertexAccents(cx, cy, outerR, badge.rarity, c)
            if (badge.rarity == Rarity.LEGENDARY && isEarned) {
                drawCircle(c.copy(alpha = 0.12f), outerR * 1.15f, Offset(cx, cy))
                drawCircle(c.copy(alpha = 0.06f), outerR * 1.3f, Offset(cx, cy))
            }
            if (isEarned) drawBadgeSymbol(badge, cx, cy, outerR * 0.42f, c)
            else drawLockSymbol(cx, cy, outerR * 0.42f, c)
        }
        if (isEarned) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 1.dp, end = 1.dp)
                    .background(Color(0xFF5C2800), RoundedCornerShape(6.dp))
                    .border(1.dp, Color(0xFFFF8C00), RoundedCornerShape(6.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "×$count",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFAA44),
                    fontFamily = FontFamily.Monospace,
                )
            }
        }
    }
}

private fun hexPath(cx: Float, cy: Float, radius: Float): Path {
    val path = Path()
    for (i in 0..5) {
        val angle = Math.toRadians(-90.0 + 60.0 * i).toFloat()
        val x = cx + radius * cos(angle)
        val y = cy + radius * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun DrawScope.drawVertexAccents(cx: Float, cy: Float, r: Float, rarity: Rarity, color: Color) {
    if (rarity == Rarity.COMMON) return
    for (i in 0..5) {
        val angle = Math.toRadians(-90.0 + 60.0 * i).toFloat()
        val vx = cx + r * cos(angle)
        val vy = cy + r * sin(angle)
        when (rarity) {
            Rarity.RARE -> drawCircle(color, 2.5.dp.toPx(), Offset(vx, vy))
            Rarity.EPIC, Rarity.LEGENDARY -> {
                val tl = 4.dp.toPx()
                val ax = cos(angle); val ay = sin(angle)
                drawLine(color, Offset(vx - ax * tl, vy - ay * tl), Offset(vx + ax * tl, vy + ay * tl), 1.5.dp.toPx(), StrokeCap.Round)
            }
            else -> {}
        }
    }
}

private fun DrawScope.drawLockSymbol(cx: Float, cy: Float, r: Float, color: Color) {
    val stroke = Stroke(1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    drawRect(color, topLeft = Offset(cx - r * 0.55f, cy - r * 0.08f), size = Size(r * 1.1f, r * 0.88f), style = stroke)
    val bow = Path().apply {
        moveTo(cx - r * 0.35f, cy - r * 0.08f)
        lineTo(cx - r * 0.35f, cy - r * 0.52f)
        cubicTo(cx - r * 0.35f, cy - r, cx + r * 0.35f, cy - r, cx + r * 0.35f, cy - r * 0.52f)
        lineTo(cx + r * 0.35f, cy - r * 0.08f)
    }
    drawPath(bow, color, style = stroke)
    drawCircle(color, r * 0.14f, Offset(cx, cy + r * 0.28f))
}
```

- [ ] **Step 2: Build check**

```bash
./gradlew assembleDebug 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/cocode/battleship/presentation/medals/MedalCanvas.kt
git commit -m "feat: add MedalCanvas hex composable with lock state and count bubble"
```

---

## Task 8: Add strings for medals screen

**Files:**
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Add strings to strings.xml**

Inside `<resources>`, add after the Stats Screen block:

```xml
    <!-- Medals Screen -->
    <string name="menu_view_medals">MEDAL REGISTRY</string>
    <string name="medals_title">MEDAL REGISTRY</string>
    <string name="medals_earned_format">%1$d / 15 MEDALS EARNED</string>
    <string name="medals_back">RETURN TO BASE</string>
```

- [ ] **Step 2: Build check**

```bash
./gradlew assembleDebug 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add app/src/main/res/values/strings.xml
git commit -m "feat: add medals screen strings"
```

---

## Task 9: MedalsScreen composable

**Files:**
- Create: `presentation/medals/MedalsScreen.kt`

- [ ] **Step 1: Create MedalsScreen.kt**

```kotlin
// app/src/main/java/com/cocode/battleship/presentation/medals/MedalsScreen.kt
package com.cocode.battleship.presentation.medals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.cocode.battleship.ui.theme.TextSecondary

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
                    MedalCell(item)
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
private fun MedalCell(item: MedalItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
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

- [ ] **Step 2: Build check**

```bash
./gradlew assembleDebug 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/cocode/battleship/presentation/medals/MedalsScreen.kt
git commit -m "feat: add MedalsScreen with hex grid and locked/earned states"
```

---

## Task 10: Navigation wiring

**Files:**
- Modify: `presentation/navigation/Screen.kt`
- Modify: `presentation/navigation/BattleshipNavHost.kt`

- [ ] **Step 1: Add Screen.Medals to Screen.kt**

```kotlin
// app/src/main/java/com/cocode/battleship/presentation/navigation/Screen.kt
package com.cocode.battleship.presentation.navigation

sealed class Screen(val route: String) {
    data object Menu     : Screen("menu")
    data object Placement: Screen("placement")
    data object Game     : Screen("game")
    data object GameOver : Screen("game_over")
    data object Stats    : Screen("stats")
    data object Medals   : Screen("medals")
}
```

- [ ] **Step 2: Update BattleshipNavHost.kt**

```kotlin
// app/src/main/java/com/cocode/battleship/presentation/navigation/BattleshipNavHost.kt
package com.cocode.battleship.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cocode.battleship.presentation.game.GameOverScreen
import com.cocode.battleship.presentation.game.GameScreen
import com.cocode.battleship.presentation.game.GameViewModel
import com.cocode.battleship.presentation.medals.MedalsScreen
import com.cocode.battleship.presentation.medals.MedalsViewModel
import com.cocode.battleship.presentation.medals.SharedPreferencesMedalsStorage
import com.cocode.battleship.presentation.menu.MenuScreen
import com.cocode.battleship.presentation.placement.PlacementScreen
import com.cocode.battleship.presentation.stats.StatsScreen

@Composable
fun BattleshipNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val gameViewModel: GameViewModel = viewModel()
    val context = LocalContext.current
    val medalsStorage = remember { SharedPreferencesMedalsStorage(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Menu.route,
            modifier = modifier.systemBarsPadding()
        ) {
            composable(Screen.Menu.route) {
                MenuScreen(
                    onStartGame = {
                        gameViewModel.resetGame()
                        navController.navigate(Screen.Placement.route)
                    },
                    onViewStats = { navController.navigate(Screen.Stats.route) },
                    onViewMedals = { navController.navigate(Screen.Medals.route) }
                )
            }
            composable(Screen.Stats.route) {
                StatsScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Medals.route) {
                val medalsViewModel: MedalsViewModel = viewModel(
                    factory = MedalsViewModel.factory(medalsStorage)
                )
                MedalsScreen(
                    viewModel = medalsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Placement.route) {
                PlacementScreen(
                    viewModel = gameViewModel,
                    onPlacementComplete = {
                        navController.navigate(Screen.Game.route) {
                            popUpTo(Screen.Placement.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Game.route) {
                GameScreen(
                    viewModel = gameViewModel,
                    onGameOver = {
                        navController.navigate(Screen.GameOver.route) {
                            popUpTo(Screen.Game.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.GameOver.route) {
                GameOverScreen(
                    viewModel = gameViewModel,
                    onPlayAgain = {
                        gameViewModel.resetGame()
                        navController.navigate(Screen.Placement.route) {
                            popUpTo(Screen.Menu.route)
                        }
                    },
                    onMainMenu = {
                        navController.navigate(Screen.Menu.route) {
                            popUpTo(Screen.Menu.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
```

- [ ] **Step 3: Build check (MenuScreen won't compile yet — that's expected)**

```bash
./gradlew assembleDebug 2>&1 | grep -E "error:|BUILD" | head -5
```

Expected: Error on `MenuScreen` — `onViewMedals` parameter not yet added. This is expected.

- [ ] **Step 4: Commit navigation skeleton**

```bash
git add app/src/main/java/com/cocode/battleship/presentation/navigation/Screen.kt \
        app/src/main/java/com/cocode/battleship/presentation/navigation/BattleshipNavHost.kt
git commit -m "feat: add Screen.Medals route and wire MedalsViewModel in BattleshipNavHost"
```

---

## Task 11: Add VIEW MEDALS button to MenuScreen

**Files:**
- Modify: `presentation/menu/MenuScreen.kt`

Add `onViewMedals: () -> Unit = {}` parameter and a second `OutlinedButton` below the existing VIEW STATS button.

- [ ] **Step 1: Update MenuScreen.kt**

Change the function signature (line 54):
```kotlin
@Composable
fun MenuScreen(onStartGame: () -> Unit, onViewStats: () -> Unit = {}, onViewMedals: () -> Unit = {}) {
```

Add the new button after the existing `OutlinedButton` (after the VIEW STATS button, around line 183):

```kotlin
            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onViewMedals,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .alpha(entry.secondaryButtonAlpha)
                    .offset(x = entry.secondaryButtonOffsetX),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, SonarCyan.copy(alpha = 0.35f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SonarCyan.copy(alpha = 0.8f)),
            ) {
                Text(
                    text = stringResource(R.string.menu_view_medals),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
            }
```

The full updated `MenuScreen.kt` (showing the relevant region around the two secondary buttons):

```kotlin
            // ... existing VIEW STATS button (onViewStats) ...

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onViewMedals,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .alpha(entry.secondaryButtonAlpha)
                    .offset(x = entry.secondaryButtonOffsetX),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, SonarCyan.copy(alpha = 0.35f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SonarCyan.copy(alpha = 0.8f)),
            ) {
                Text(
                    text = stringResource(R.string.menu_view_medals),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
            }

            Spacer(Modifier.height(16.dp))
            // ... game mode label and footer ...
```

- [ ] **Step 2: Build check — must succeed now**

```bash
./gradlew assembleDebug 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Run all tests**

```bash
./gradlew test 2>&1 | tail -10
```

Expected: All tests PASS.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/cocode/battleship/presentation/menu/MenuScreen.kt
git commit -m "feat: add VIEW MEDALS button to MenuScreen"
```

---

## Task 12: Remove BadgeShowcase from StatsScreen

**Files:**
- Modify: `presentation/stats/StatsScreen.kt`

`BadgeShowcase` (line 99) is now redundant — medals have their own dedicated screen. Remove the call and the import.

- [ ] **Step 1: Remove BadgeShowcase from StatsScreen.kt**

Remove line 34:
```kotlin
import com.cocode.battleship.presentation.game.components.BadgeShowcase
```

Remove lines 98–100 (inside the `else` block):
```kotlin
                Spacer(Modifier.height(12.dp))
                BadgeShowcase(badges = SessionStats.allEarnedBadges.toList())
```

The `else` block should now end at:
```kotlin
            } else {
                RankBanner(rank = rank)
                Spacer(Modifier.height(12.dp))
                SessionStatsPanel()
            }
```

- [ ] **Step 2: Build and run all tests**

```bash
./gradlew test 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`, all tests PASS.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/cocode/battleship/presentation/stats/StatsScreen.kt
git commit -m "feat: remove BadgeShowcase from StatsScreen — medals screen replaces it"
```

---

## Self-Review Checklist

**Spec coverage:**
- ✅ All 15 medals always visible (locked at 28% alpha) — `MedalsScreen.kt` via `MedalCanvas(count=0)`
- ✅ Amber corner bubble, 12sp bold — `MedalCanvas.kt` Text overlay
- ✅ Hex tactical art — `MedalCanvas.kt` + `MedalSymbols.kt`
- ✅ Rarity tier colors (bronze/silver-blue/gold/cyan) — `rarityColor()` in `MedalCanvas.kt`
- ✅ Vertex accents (none/dots/diamonds) — `drawVertexAccents()`
- ✅ Legendary soft glow — double concentric circles in `MedalCanvas`
- ✅ Lock symbol — `drawLockSymbol()`
- ✅ `SharedPreferencesMedalsStorage` with `Set<Badge>` migration
- ✅ `SessionStats.record()` calls `medalsStorage?.increment()`
- ✅ `Screen.Medals` route
- ✅ VIEW MEDALS button in MenuScreen
- ✅ BadgeShowcase removed from StatsScreen
- ✅ Strings added to strings.xml
- ✅ All files ≤ 200 lines

**Placeholder scan:** None found.

**Type consistency:**
- `MedalsStorage.increment(badges: List<Badge>)` — used correctly in `SessionStats.record()` (passes `earnedBadges: List<Badge>`)
- `MedalsViewModel.factory(storage: MedalsStorage)` — called in `BattleshipNavHost` with `SharedPreferencesMedalsStorage`
- `rarityColor(rarity: Rarity): Color` — defined in `MedalCanvas.kt`, used in `MedalsScreen.kt` (same package, `internal` visibility is sufficient)
- `drawBadgeSymbol(badge, cx, cy, r, color)` — defined `internal` in `MedalSymbols.kt`, called from `MedalCanvas.kt` (same package) ✅

---

**Plan complete and saved to `docs/superpowers/plans/2026-04-12-medals-screen.md`. Two execution options:**

**1. Subagent-Driven (recommended)** — dispatch a fresh subagent per task, review between tasks

**2. Inline Execution** — execute tasks in this session using executing-plans

**Which approach?**
