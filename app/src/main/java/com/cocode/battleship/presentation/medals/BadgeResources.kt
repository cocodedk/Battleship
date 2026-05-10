package com.cocode.battleship.presentation.medals

import androidx.annotation.StringRes
import com.cocode.battleship.R
import com.cocode.battleship.domain.scoring.Badge

@StringRes
internal fun Badge.unlockHintResId(): Int = when (this) {
    Badge.FIRST_BLOOD           -> R.string.badge_hint_first_blood
    Badge.SHARPSHOOTER          -> R.string.badge_hint_sharpshooter
    Badge.DEAD_EYE              -> R.string.badge_hint_dead_eye
    Badge.HOT_STREAK            -> R.string.badge_hint_hot_streak
    Badge.UNSTOPPABLE           -> R.string.badge_hint_unstoppable
    Badge.FLAWLESS_VICTORY      -> R.string.badge_hint_flawless_victory
    Badge.PERFECT_GUNNER        -> R.string.badge_hint_perfect_gunner
    Badge.LEVIATHAN_SLAYER      -> R.string.badge_hint_leviathan_slayer
    Badge.SILENT_SERVICE        -> R.string.badge_hint_silent_service
    Badge.LAST_STAND            -> R.string.badge_hint_last_stand
    Badge.DESTROYER_LIVES       -> R.string.badge_hint_destroyer_lives
    Badge.SWIM_FOR_IT           -> R.string.badge_hint_swim_for_it
    Badge.FOG_OF_WAR            -> R.string.badge_hint_fog_of_war
    Badge.DEPTH_CHARGE_DIPLOMAT -> R.string.badge_hint_depth_charge_diplomat
    Badge.ON_FIRE               -> R.string.badge_hint_on_fire
    Badge.BLITZ                 -> R.string.badge_hint_blitz
    Badge.SEA_WOLF              -> R.string.badge_hint_sea_wolf
    Badge.LUCKY_DOG             -> R.string.badge_hint_lucky_dog
    Badge.COLD_OPENER           -> R.string.badge_hint_cold_opener
    Badge.IRON_HULL             -> R.string.badge_hint_iron_hull
    Badge.CRUISER_LIVES         -> R.string.badge_hint_cruiser_lives
    Badge.TORPEDO_ACE           -> R.string.badge_hint_torpedo_ace
    Badge.BATTLESHIP_HUNTER     -> R.string.badge_hint_battleship_hunter
    Badge.SMALL_GAME            -> R.string.badge_hint_small_game
    Badge.SPRAY_AND_PRAY        -> R.string.badge_hint_spray_and_pray
    Badge.NUCLEAR_OPTION        -> R.string.badge_hint_nuclear_option
    Badge.SCATTERSHOT           -> R.string.badge_hint_scattershot
    Badge.TACTICAL_RETREAT      -> R.string.badge_hint_tactical_retreat
    Badge.PHOENIX               -> R.string.badge_hint_phoenix
    Badge.SPITE                 -> R.string.badge_hint_spite
    Badge.FLEET_COMMANDER       -> R.string.badge_hint_fleet_commander
    Badge.SEA_VETERAN           -> R.string.badge_hint_sea_veteran
    Badge.IRON_ADMIRAL          -> R.string.badge_hint_iron_admiral
}
