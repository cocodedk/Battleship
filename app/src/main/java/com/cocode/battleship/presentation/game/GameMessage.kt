package com.cocode.battleship.presentation.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.cocode.battleship.R
import com.cocode.battleship.domain.model.ShipType
import com.cocode.battleship.domain.model.SuperWeapon
import com.cocode.battleship.presentation.nameRes
import com.cocode.battleship.presentation.titleRes

/**
 * The status line of a battle, as a value. The view model picks which message it is; the screen
 * turns it into text from the string resources, so the language is the screen's business.
 */
sealed interface GameMessage {
    data object PlaceShips : GameMessage
    data object YourTurn : GameMessage
    data object PlayerHit : GameMessage
    data object PlayerMiss : GameMessage
    data class PlayerSunk(val ship: ShipType?) : GameMessage
    data class WeaponFired(val weapon: SuperWeapon) : GameMessage
    data class WeaponSunk(val weapon: SuperWeapon, val ships: List<ShipType>) : GameMessage
    data object PlayerWon : GameMessage
    data object AiHit : GameMessage
    data class AiSunk(val ship: ShipType?) : GameMessage
    data object AiMissed : GameMessage
    data object AiWon : GameMessage
}

@Composable
fun GameMessage.asText(): String = when (this) {
    GameMessage.PlaceShips -> stringResource(R.string.game_msg_place_ships)
    GameMessage.YourTurn -> stringResource(R.string.game_msg_your_turn)
    GameMessage.PlayerHit -> stringResource(R.string.game_msg_hit)
    GameMessage.PlayerMiss -> stringResource(R.string.game_msg_miss)
    is GameMessage.PlayerSunk ->
        if (ship != null) stringResource(R.string.game_msg_you_sunk, stringResource(ship.nameRes()))
        else stringResource(R.string.game_msg_you_sunk_unknown)
    is GameMessage.WeaponFired ->
        stringResource(R.string.game_msg_weapon_fired, stringResource(weapon.titleRes()))
    is GameMessage.WeaponSunk -> {
        val names = ships.map { stringResource(it.nameRes()) }
        pluralStringResource(
            R.plurals.game_msg_weapon_sunk,
            names.size,
            stringResource(weapon.titleRes()),
            names.joinToString()
        )
    }
    GameMessage.PlayerWon -> stringResource(R.string.game_msg_you_win)
    GameMessage.AiHit -> stringResource(R.string.game_msg_ai_hit)
    is GameMessage.AiSunk ->
        if (ship != null) stringResource(R.string.game_msg_ai_sunk, stringResource(ship.nameRes()))
        else stringResource(R.string.game_msg_ai_sunk_unknown)
    GameMessage.AiMissed -> stringResource(R.string.game_msg_ai_missed)
    GameMessage.AiWon -> stringResource(R.string.game_msg_you_lose)
}
