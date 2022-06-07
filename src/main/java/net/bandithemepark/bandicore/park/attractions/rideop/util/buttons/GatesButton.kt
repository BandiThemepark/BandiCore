package net.bandithemepark.bandicore.park.attractions.rideop.util.buttons

import org.bukkit.Location
import org.bukkit.block.data.type.Gate
import org.bukkit.entity.Player

abstract class GatesButton(slot: Int, val locations: List<Location>): SwitchButton(slot, "rideop-button-gates-title", "rideop-button-gates-description") {
    var open = false

    init {
        updateGates()
    }

    private fun areGatesOpen(): Boolean {
        return (locations[0].block.state.blockData as Gate).isOpen
    }

    fun updateGates() {
        for(location in locations) {
            val gate = location.block.blockData as Gate
            gate.isOpen = open
            location.block.blockData = gate
        }
    }

    override fun isActivated(): Boolean {
        return !open
    }

    override fun onClick(player: Player) {
        if(check()) {
            open = !open
            updateGates()
            rideOP.updateMenu()
        }
    }

    abstract fun check(): Boolean
}