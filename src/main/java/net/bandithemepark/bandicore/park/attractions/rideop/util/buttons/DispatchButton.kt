package net.bandithemepark.bandicore.park.attractions.rideop.util.buttons

import org.bukkit.entity.Player

abstract class DispatchButton(slot: Int): NormalButton(slot, "rideop-button-dispatch-title", "rideop-button-dispatch-description") {
    override fun onClick(player: Player) {
        if(isAvailable()) {
            onDispatch(player)
            rideOP.updateMenu()
        }
    }

    abstract fun onDispatch(player: Player)
}