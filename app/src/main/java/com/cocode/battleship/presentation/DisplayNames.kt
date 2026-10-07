package com.cocode.battleship.presentation

import androidx.annotation.StringRes
import com.cocode.battleship.R
import com.cocode.battleship.domain.model.ShipType
import com.cocode.battleship.domain.model.SuperWeapon
import com.cocode.battleship.domain.scoring.Rank

/** The player-facing names of domain values. The domain layer holds no text. */
@StringRes
fun ShipType.nameRes(): Int = when (this) {
    ShipType.CARRIER    -> R.string.ship_carrier
    ShipType.BATTLESHIP -> R.string.ship_battleship
    ShipType.CRUISER    -> R.string.ship_cruiser
    ShipType.SUBMARINE  -> R.string.ship_submarine
    ShipType.DESTROYER  -> R.string.ship_destroyer
}

@StringRes
fun Rank.nameRes(): Int = when (this) {
    Rank.FLEET_ADMIRAL -> R.string.rank_fleet_admiral
    Rank.ADMIRAL       -> R.string.rank_admiral
    Rank.VICE_ADMIRAL  -> R.string.rank_vice_admiral
    Rank.COMMODORE     -> R.string.rank_commodore
    Rank.CAPTAIN       -> R.string.rank_captain
    Rank.LIEUTENANT    -> R.string.rank_lieutenant
    Rank.ENSIGN        -> R.string.rank_ensign
    Rank.CADET         -> R.string.rank_cadet
}

/** The title-case weapon name used in battle messages (the chip label is `weapon_*_name`). */
@StringRes
fun SuperWeapon.titleRes(): Int = when (this) {
    SuperWeapon.CARPET_BOMB        -> R.string.weapon_carpet_bomb_title
    SuperWeapon.BATTLESHIP_BARRAGE -> R.string.weapon_barrage_title
    SuperWeapon.SONAR_SWEEP        -> R.string.weapon_sonar_title
    SuperWeapon.TORPEDO_SPREAD     -> R.string.weapon_torpedo_title
    SuperWeapon.PRECISION_STRIKE   -> R.string.weapon_precision_title
}
