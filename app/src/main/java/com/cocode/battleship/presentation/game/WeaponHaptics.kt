package com.cocode.battleship.presentation.game

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import com.cocode.battleship.domain.model.SuperWeapon

class WeaponHaptics(private val context: Context) {
    fun perform(weapon: SuperWeapon) {
        val vibrator = vibrator() ?: return
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(effectFor(weapon))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(120)
        }
    }

    private fun vibrator(): Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(VibratorManager::class.java)
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun effectFor(weapon: SuperWeapon): VibrationEffect = when (weapon) {
        SuperWeapon.CARPET_BOMB ->
            VibrationEffect.createWaveform(
                longArrayOf(0, 35, 25, 55, 25, 75),
                intArrayOf(0, 180, 0, 220, 0, 255),
                -1
            )
        SuperWeapon.BATTLESHIP_BARRAGE ->
            VibrationEffect.createWaveform(
                longArrayOf(0, 18, 18, 18, 18, 18, 18, 18, 18),
                intArrayOf(0, 170, 0, 200, 0, 220, 0, 240, 0),
                -1
            )
        SuperWeapon.SONAR_SWEEP ->
            VibrationEffect.createWaveform(
                longArrayOf(0, 22, 16, 32, 16, 42),
                intArrayOf(0, 80, 0, 140, 0, 200),
                -1
            )
        SuperWeapon.TORPEDO_SPREAD ->
            VibrationEffect.createWaveform(
                longArrayOf(0, 24, 20, 32, 20, 42),
                intArrayOf(0, 255, 0, 200, 0, 150),
                -1
            )
        SuperWeapon.PRECISION_STRIKE ->
            VibrationEffect.createWaveform(
                longArrayOf(0, 16, 18, 24),
                intArrayOf(0, 255, 0, 190),
                -1
            )
    }
}
