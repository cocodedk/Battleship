package com.cocode.battleship.presentation.game

import com.cocode.battleship.domain.model.Board
import com.cocode.battleship.domain.model.CellState
import com.cocode.battleship.domain.model.Ship
import com.cocode.battleship.domain.model.ShipType
import com.cocode.battleship.domain.model.SuperWeapon
import org.junit.Assert.assertEquals
import org.junit.Test

class GameMessageTest {
    private val emptyBoard = Board()

    @Test
    fun `a miss with no weapon is a miss`() {
        assertEquals(
            GameMessage.PlayerMiss,
            buildPlayerHitMessage(null, CellState.MISS, emptySet(), emptyBoard)
        )
    }

    @Test
    fun `a hit with no weapon is a hit`() {
        assertEquals(
            GameMessage.PlayerHit,
            buildPlayerHitMessage(null, CellState.HIT, emptySet(), emptyBoard)
        )
    }

    @Test
    fun `a weapon that sinks nothing is fired`() {
        assertEquals(
            GameMessage.WeaponFired(SuperWeapon.SONAR_SWEEP),
            buildPlayerHitMessage(SuperWeapon.SONAR_SWEEP, CellState.MISS, emptySet(), emptyBoard)
        )
    }

    @Test
    fun `a weapon that sinks ships names them`() {
        val sunk = linkedSetOf(ShipType.DESTROYER, ShipType.CRUISER)
        assertEquals(
            GameMessage.WeaponSunk(SuperWeapon.CARPET_BOMB, listOf(ShipType.DESTROYER, ShipType.CRUISER)),
            buildPlayerHitMessage(SuperWeapon.CARPET_BOMB, CellState.SUNK, sunk, emptyBoard)
        )
    }

    @Test
    fun `a sunk cell names the ship that went down`() {
        val ship = Ship(ShipType.DESTROYER, 0, 0, true)
        val board = Board().placeShip(ship).receiveAttack(0, 0).receiveAttack(0, 1)
        assertEquals(
            GameMessage.PlayerSunk(ShipType.DESTROYER),
            buildPlayerHitMessage(null, CellState.SUNK, setOf(ShipType.DESTROYER), board)
        )
    }
}
