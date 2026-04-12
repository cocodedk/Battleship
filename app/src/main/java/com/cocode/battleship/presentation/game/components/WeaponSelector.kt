package com.cocode.battleship.presentation.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocode.battleship.R
import com.cocode.battleship.domain.model.SuperWeapon
import com.cocode.battleship.ui.theme.NavyBorder
import com.cocode.battleship.ui.theme.NavyCard
import com.cocode.battleship.ui.theme.SonarCyan
import com.cocode.battleship.ui.theme.TextSecondary

@Composable
fun WeaponSelector(
    available: List<SuperWeapon>,
    selected: SuperWeapon?,
    onSelect: (SuperWeapon) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.weapons_available_label),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            available.forEach { weapon ->
                WeaponChip(
                    weapon = weapon,
                    isSelected = weapon == selected,
                    enabled = enabled,
                    onClick = { onSelect(weapon) }
                )
            }
        }
    }
}

@Composable
private fun WeaponChip(
    weapon: SuperWeapon,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) SonarCyan.copy(alpha = 0.25f) else NavyCard
    val border = if (isSelected) SonarCyan else NavyBorder
    val textColor = if (isSelected) SonarCyan else TextSecondary
    Row(
        modifier = Modifier
            .alpha(if (enabled) 1f else 0.4f)
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(4.dp))
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = weapon.icon, fontSize = 16.sp)
        Spacer(Modifier.width(6.dp))
        Text(
            text = weaponDisplayName(weapon),
            color = textColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun weaponDisplayName(weapon: SuperWeapon): String = when (weapon) {
    SuperWeapon.CARPET_BOMB        -> stringResource(R.string.weapon_carpet_bomb_name)
    SuperWeapon.BATTLESHIP_BARRAGE -> stringResource(R.string.weapon_barrage_name)
    SuperWeapon.SONAR_SWEEP        -> stringResource(R.string.weapon_sonar_name)
    SuperWeapon.TORPEDO_SPREAD     -> stringResource(R.string.weapon_torpedo_name)
    SuperWeapon.PRECISION_STRIKE   -> stringResource(R.string.weapon_precision_name)
}
