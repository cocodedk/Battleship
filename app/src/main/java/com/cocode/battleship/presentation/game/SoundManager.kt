package com.cocode.battleship.presentation.game

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundManager {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun playHit() = scope.launch { sweep(600f, 1000f, 200, decay = true) }
    fun playMiss() = scope.launch { tone(260f, 140, decay = true) }
    fun playSunk() = scope.launch { explosion(450) }
    fun playWin() = scope.launch {
        tone(440f, 180); delay(50); tone(660f, 180); delay(50); tone(880f, 300)
    }
    fun playLose() = scope.launch { sweep(700f, 160f, 900, decay = false) }

    fun release() { scope.cancel() }

    // --- PCM helpers ---

    private suspend fun tone(hz: Float, ms: Int, decay: Boolean = false) {
        val numSamples = SAMPLE_RATE * ms / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val amplitude = if (decay) 1.0 - i.toDouble() / numSamples else 1.0
            val sample = amplitude * sin(2.0 * PI * hz * i / SAMPLE_RATE)
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        playBuffer(buffer, ms)
    }

    private suspend fun sweep(startHz: Float, endHz: Float, ms: Int, decay: Boolean) {
        val numSamples = SAMPLE_RATE * ms / 1000
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        for (i in 0 until numSamples) {
            val fraction = i.toDouble() / numSamples
            val currentHz = startHz + (endHz - startHz) * fraction
            val amplitude = if (decay) 1.0 - fraction else 1.0
            val sample = amplitude * sin(phase)
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            phase += 2.0 * PI * currentHz / SAMPLE_RATE
        }
        playBuffer(buffer, ms)
    }

    private suspend fun explosion(ms: Int) {
        val numSamples = SAMPLE_RATE * ms / 1000
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        for (i in 0 until numSamples) {
            val decay = 1.0 - i.toDouble() / numSamples
            val noise = Math.random() * 2.0 - 1.0
            val bass = 0.4 * sin(phase)
            val combined = (noise + bass).coerceIn(-1.0, 1.0)
            buffer[i] = (decay * combined * Short.MAX_VALUE).toInt().toShort()
            phase += 2.0 * PI * 80.0 / SAMPLE_RATE
        }
        playBuffer(buffer, ms)
    }

    private suspend fun playBuffer(buffer: ShortArray, durationMs: Int) {
        val track = buildAudioTrack(buffer.size)
        try {
            if (track.state != AudioTrack.STATE_INITIALIZED) return
            val written = track.write(buffer, 0, buffer.size)
            if (written < 0) return
            track.play()
            delay(durationMs.toLong() + 30)
        } finally {
            runCatching { track.stop() }
            runCatching { track.release() }
        }
    }

    private fun buildAudioTrack(numSamples: Int): AudioTrack {
        return AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(numSamples * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
    }

    companion object {
        private const val SAMPLE_RATE = 44100
    }
}
