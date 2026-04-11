package com.cocode.battleship.domain.scoring

import org.junit.Assert.assertEquals
import org.junit.Test

class ScoreCalculatorTest {

    /**
     * Neutral WIN stats: totalShots=50 (no speed bonus), misses=1 (no perfect game),
     * accuracy=0f (no accuracy kicker), all other fields zeroed.
     */
    private fun minStats(outcome: GameOutcome): GameStats = GameStats(
        outcome = outcome,
        totalShots = if (outcome == GameOutcome.WIN) 50 else 0,
        hits = 0,
        misses = if (outcome == GameOutcome.WIN) 1 else 0,
        survivingPlayerShips = 0,
        totalPlayerShipHp = 0,
        shipsSunkByPlayer = 0,
        longestHitStreak = 0,
        longestMissStreak = 0,
        firstShotHit = false,
        firstEnemyShipSunkType = null,
        playerShipEndStates = emptyMap()
    )

    // 1. WIN base is 1000
    @Test
    fun `WIN base score is 1000`() {
        val stats = minStats(GameOutcome.WIN)
        // base=1000, all other components=0, no speed bonus (shots=50), no perfect game (misses=1)
        assertEquals(1000, ScoreCalculator.calculate(stats))
    }

    // 2. LOSS base is 200 (verified via total = base - zero-hit penalty = 200 - 50 = 150)
    @Test
    fun `LOSS base score is 200`() {
        val stats = minStats(GameOutcome.LOSS)
        // totalShots=0 → accuracy=0f (computed), hits=0 → zero-hit LOSS penalty -50
        // 200 (base) - 50 (zero-hit LOSS) = 150
        assertEquals(150, ScoreCalculator.calculate(stats))
    }

    // 3. Victory bonus +300 per surviving ship (WIN, 3 ships alive -> +900)
    @Test
    fun `victory bonus adds 300 per surviving ship on WIN`() {
        val stats = minStats(GameOutcome.WIN).copy(survivingPlayerShips = 3)
        // 1000 (base) + 900 (victory) = 1900
        assertEquals(1900, ScoreCalculator.calculate(stats))
    }

    // 4. Victory bonus includes +5 per hp point (WIN, totalPlayerShipHp=10 -> +50)
    @Test
    fun `victory bonus adds 5 per hp point on WIN`() {
        val stats = minStats(GameOutcome.WIN).copy(totalPlayerShipHp = 10)
        // 1000 (base) + 50 (hp bonus) = 1050
        assertEquals(1050, ScoreCalculator.calculate(stats))
    }

    // 5. Accuracy bonus is rounded (hits=20, totalShots=50 -> accuracy=0.4; +round(200) = +200; no kicker since <0.5)
    @Test
    fun `accuracy bonus is rounded correctly below 0 5 threshold`() {
        val stats = minStats(GameOutcome.WIN).copy(
            hits = 20,
            misses = 1  // keep misses>0 so no perfect game
        )
        // 1000 (base) + round(0.4 * 500) = +200 = 1200
        assertEquals(1200, ScoreCalculator.calculate(stats))
    }

    // 6. Accuracy kicker +250 when accuracy >= 0.50
    @Test
    fun `accuracy kicker adds 250 when accuracy is at least 0 50`() {
        val stats = minStats(GameOutcome.WIN).copy(
            hits = 25,
            misses = 1  // keep misses>0 so no perfect game
        )
        // 1000 (base) + round(0.5 * 500)=250 + kicker 250 = 1500
        assertEquals(1500, ScoreCalculator.calculate(stats))
    }

    // 7. Perfect game bonus +500 when WIN and misses==0
    @Test
    fun `perfect game bonus adds 500 on WIN with zero misses`() {
        val stats = minStats(GameOutcome.WIN).copy(
            hits = 17,
            misses = 0,
            totalShots = 17
        )
        // base=1000, accuracyBonus=round(1.0*500)=500 + kicker=250 + perfect=500 = 1250
        // speedBonus=(50-17)*25=825
        // total = 1000 + 1250 + 825 = 3075
        assertEquals(3075, ScoreCalculator.calculate(stats))
    }

    // 8. Perfect game bonus NOT applied on LOSS even if misses==0
    @Test
    fun `perfect game bonus is not applied on LOSS even with zero misses`() {
        val stats = minStats(GameOutcome.LOSS).copy(
            hits = 5,
            misses = 0,
            totalShots = 5
        )
        // LOSS: 200 (base) + round(1.0 * 500)=500 + kicker 250 (no perfect, no speed)
        // hits=5 so no zero-hit LOSS penalty
        assertEquals(950, ScoreCalculator.calculate(stats))
    }

    // 9. Speed bonus for WIN under 50 shots (totalShots=30 -> +(50-30)*25=+500)
    @Test
    fun `speed bonus is applied on WIN with shots under 50`() {
        val stats = minStats(GameOutcome.WIN).copy(totalShots = 30)
        // 1000 (base) + (50-30)*25 = 500 = 1500
        assertEquals(1500, ScoreCalculator.calculate(stats))
    }

    // 10. Speed bonus is 0 when totalShots >= 50 (no penalty for slow)
    @Test
    fun `speed bonus is zero when totalShots is 50 or more`() {
        val stats = minStats(GameOutcome.WIN)
        // totalShots=50, base=1000, speed=0
        assertEquals(1000, ScoreCalculator.calculate(stats))
    }

    // 11. Streak bonus: longestHitStreak=4 -> +4*40=+160 only (no kicker)
    @Test
    fun `streak bonus is 160 for streak of 4 with no kicker`() {
        val stats = minStats(GameOutcome.WIN).copy(longestHitStreak = 4)
        // 1000 (base) + 160 (streak) = 1160
        assertEquals(1160, ScoreCalculator.calculate(stats))
    }

    // 12. Streak bonus: longestHitStreak=5 -> +5*40+200=+400
    @Test
    fun `streak bonus is 400 for streak of 5 with 200 kicker`() {
        val stats = minStats(GameOutcome.WIN).copy(longestHitStreak = 5)
        // 1000 (base) + 200 + 200 kicker = 1400
        assertEquals(1400, ScoreCalculator.calculate(stats))
    }

    // 13. Streak bonus: longestHitStreak=8 -> +8*40+200+500=+1020
    @Test
    fun `streak bonus is 1020 for streak of 8 with both kickers`() {
        val stats = minStats(GameOutcome.WIN).copy(longestHitStreak = 8)
        // 1000 (base) + 320 + 200 + 500 = 2020
        assertEquals(2020, ScoreCalculator.calculate(stats))
    }

    // 14. Sink bonus: +100 per ship (3 ships sunk -> +300)
    @Test
    fun `sink bonus adds 100 per ship sunk`() {
        val stats = minStats(GameOutcome.WIN).copy(shipsSunkByPlayer = 3)
        // 1000 (base) + 300 (sink) = 1300
        assertEquals(1300, ScoreCalculator.calculate(stats))
    }

    // 15. Flourish bonus: +150 if firstShotHit
    @Test
    fun `flourish bonus adds 150 when first shot was a hit`() {
        val stats = minStats(GameOutcome.WIN).copy(firstShotHit = true)
        // 1000 (base) + 150 (flourish) = 1150
        assertEquals(1150, ScoreCalculator.calculate(stats))
    }

    // 16. Overshot penalty: -10 per shot over 80 (totalShots=90 -> -100)
    @Test
    fun `overshot penalty deducts 10 per shot beyond 80`() {
        val stats = minStats(GameOutcome.WIN).copy(totalShots = 90)
        // 1000 (base) - (90-80)*10 = -100 = 900
        assertEquals(900, ScoreCalculator.calculate(stats))
    }

    // 17. Zero-hit LOSS penalty: -50 if LOSS && hits==0
    @Test
    fun `zero hit LOSS penalty deducts 50 on LOSS with no hits`() {
        val stats = minStats(GameOutcome.LOSS)
        // minStats for LOSS has hits=0, misses=0, totalShots=0
        // 200 (base) - 50 (zero-hit LOSS) = 150
        assertEquals(150, ScoreCalculator.calculate(stats))
    }

    // 18. Final score clamped to 0 (all penalties, no bonuses -> 0)
    @Test
    fun `final score is clamped to 0 when all penalties exceed bonuses`() {
        val stats = minStats(GameOutcome.LOSS).copy(
            totalShots = 1000,
            hits = 0,
            misses = 1000
        )
        // 200 (base) - (1000-80)*10 (overshot) - 50 (zero-hit LOSS) => very negative -> clamp to 0
        assertEquals(0, ScoreCalculator.calculate(stats))
    }

    // 19. A realistic WIN scenario produces expected total (test the sum with known inputs)
    @Test
    fun `realistic WIN scenario produces correct total`() {
        val stats = GameStats(
            outcome = GameOutcome.WIN,
            totalShots = 40,
            hits = 17,
            misses = 23,
            survivingPlayerShips = 2,
            totalPlayerShipHp = 8,
            shipsSunkByPlayer = 5,
            longestHitStreak = 4,
            longestMissStreak = 3,
            firstShotHit = true,
            firstEnemyShipSunkType = null,
            playerShipEndStates = emptyMap()
        )
        // base:          1000
        // victoryBonus:  300*2 + 5*8 = 600 + 40 = 640
        // accuracyBonus: round(0.425f * 500) = 213 (no kicker: <0.5; no perfect: misses=23)
        // speedBonus:    (50-40)*25 = 250
        // streakBonus:   4*40 = 160
        // sinkBonus:     5*100 = 500
        // flourishBonus: 150
        // penalties:     0 (totalShots=40 < 80)
        // total:         1000 + 640 + 213 + 250 + 160 + 500 + 150 = 2913
        assertEquals(2913, ScoreCalculator.calculate(stats))
    }
}
