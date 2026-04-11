package com.cocode.battleship.domain.model

enum class SuperWeapon(
    val displayName: String,
    val icon: String,
    val displayNameKey: String,
    val descriptionKey: String,
    val unlockShip: ShipType,
    val offsets: List<Pair<Int, Int>>
) {
    CARPET_BOMB(
        displayName = "Carpet Bomb",
        icon = "💣",
        displayNameKey = "weapon_carpet_bomb_name",
        descriptionKey = "weapon_carpet_bomb_desc",
        unlockShip = ShipType.CARRIER,
        offsets = listOf(
            -1 to -1, -1 to 0, -1 to 1,
             0 to -1,  0 to 0,  0 to 1,
             1 to -1,  1 to 0,  1 to 1
        )
    ),
    BATTLESHIP_BARRAGE(
        displayName = "Battleship Barrage",
        icon = "🎯",
        displayNameKey = "weapon_barrage_name",
        descriptionKey = "weapon_barrage_desc",
        unlockShip = ShipType.BATTLESHIP,
        offsets = listOf(
            -2 to 0, -1 to 0,
             0 to -2,  0 to -1, 0 to 0, 0 to 1, 0 to 2,
             1 to 0,   2 to 0
        )
    ),
    SONAR_SWEEP(
        displayName = "Sonar Sweep",
        icon = "📡",
        displayNameKey = "weapon_sonar_name",
        descriptionKey = "weapon_sonar_desc",
        unlockShip = ShipType.CRUISER,
        offsets = listOf(0 to -2, 0 to -1, 0 to 0, 0 to 1, 0 to 2)
    ),
    TORPEDO_SPREAD(
        displayName = "Torpedo Spread",
        icon = "🚀",
        displayNameKey = "weapon_torpedo_name",
        descriptionKey = "weapon_torpedo_desc",
        unlockShip = ShipType.SUBMARINE,
        offsets = listOf(-2 to 0, -1 to 0, 0 to 0, 1 to 0, 2 to 0)
    ),
    PRECISION_STRIKE(
        displayName = "Precision Strike",
        icon = "✖",
        displayNameKey = "weapon_precision_name",
        descriptionKey = "weapon_precision_desc",
        unlockShip = ShipType.DESTROYER,
        offsets = listOf(-1 to -1, -1 to 1, 0 to 0, 1 to -1, 1 to 1)
    );

    companion object {
        fun forShipType(type: ShipType): SuperWeapon =
            entries.first { it.unlockShip == type }
    }
}
