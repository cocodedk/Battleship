package com.cocode.battleship.domain.model

enum class ShipType(val size: Int, val displayName: String) {
    CARRIER(5, "Carrier"),
    BATTLESHIP(4, "Battleship"),
    CRUISER(3, "Cruiser"),
    SUBMARINE(3, "Submarine"),
    DESTROYER(2, "Destroyer")
}
