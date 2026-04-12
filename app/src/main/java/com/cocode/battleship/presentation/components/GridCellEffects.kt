package com.cocode.battleship.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.domain.model.SuperWeapon
import com.cocode.battleship.ui.theme.NeonOrange
import com.cocode.battleship.ui.theme.NeonViolet
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TorpedoRed

private const val WEAPON_BURST_GLYPH = "✹"

@Composable
internal fun WeaponEffectOverlay(weapon: SuperWeapon, alpha: Float, scale: Float) {
    val tint = weaponTint(weapon)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha },
        contentAlignment = Alignment.Center
    ) {
        when (weapon) {
            SuperWeapon.CARPET_BOMB -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(tint.copy(alpha = 0.28f), RoundedCornerShape(2.dp))
                        .border(1.dp, tint.copy(alpha = 0.9f), RoundedCornerShape(2.dp))
                )
                Text(text = WEAPON_BURST_GLYPH, color = tint, fontSize = 12.sp)
            }
            SuperWeapon.BATTLESHIP_BARRAGE -> {
                Box(Modifier.fillMaxWidth().height(3.dp).background(tint.copy(alpha = 0.95f)))
                Box(Modifier.width(3.dp).fillMaxSize().background(tint.copy(alpha = 0.95f)))
            }
            SuperWeapon.SONAR_SWEEP -> {
                Box(Modifier.fillMaxWidth().height(4.dp).background(tint.copy(alpha = 0.95f)))
                Box(Modifier.fillMaxWidth().height(10.dp).background(tint.copy(alpha = 0.20f)))
            }
            SuperWeapon.TORPEDO_SPREAD -> {
                Box(Modifier.width(4.dp).fillMaxSize().background(tint.copy(alpha = 0.95f)))
                Box(Modifier.width(10.dp).fillMaxSize().background(tint.copy(alpha = 0.20f)))
            }
            SuperWeapon.PRECISION_STRIKE -> {
                Box(
                    Modifier.width(3.dp).fillMaxSize()
                        .graphicsLayer { rotationZ = 45f }
                        .background(tint.copy(alpha = 0.95f))
                )
                Box(
                    Modifier.width(3.dp).fillMaxSize()
                        .graphicsLayer { rotationZ = -45f }
                        .background(tint.copy(alpha = 0.95f))
                )
            }
        }
    }
}

private fun weaponTint(weapon: SuperWeapon): Color = when (weapon) {
    SuperWeapon.CARPET_BOMB -> NeonOrange
    SuperWeapon.BATTLESHIP_BARRAGE -> SonarCyan
    SuperWeapon.SONAR_SWEEP -> SonarCyan
    SuperWeapon.TORPEDO_SPREAD -> TorpedoRed
    SuperWeapon.PRECISION_STRIKE -> NeonViolet
}
