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
            val badge = Badge.byName[parts[0]] ?: return@mapNotNull null
            val count = parts[1].toIntOrNull() ?: return@mapNotNull null
            badge to count
        }.toMap()
    }

    override fun increment(badges: List<Badge>) {
        if (badges.isEmpty()) return
        val current = load().toMutableMap()
        badges.forEach { current[it] = (current[it] ?: 0) + 1 }
        prefs.edit().putString(KEY_BADGE_COUNTS, encode(current)).apply()
    }

    private fun migrateLegacyIfNeeded() {
        if (!prefs.contains(KEY_EARNED_BADGES)) return
        val legacy = prefs.getStringSet(KEY_EARNED_BADGES, emptySet()) ?: emptySet()
        val counts = legacy.mapNotNull { name ->
            Badge.byName[name]?.let { it to 1 }
        }.toMap()
        prefs.edit()
            .putString(KEY_BADGE_COUNTS, encode(counts))
            .remove(KEY_EARNED_BADGES)
            .apply()
    }

    private fun encode(counts: Map<Badge, Int>): String =
        counts.entries.joinToString(",") { "${it.key.name}:${it.value}" }

    companion object {
        const val PREFS_NAME = "career_stats"
        const val KEY_BADGE_COUNTS = "badge_counts"
        private const val KEY_EARNED_BADGES = "earned_badges"
    }
}
