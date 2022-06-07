package net.bandithemepark.bandicore.park.attractions.rideop.util.buttons

import org.bukkit.entity.Player

abstract class SimpleHarnessButton(slot: Int): SwitchButton(slot, "rideop-button-harness-title", "rideop-button-harness-description") {
    var open = false

    override fun isActivated(): Boolean {
        return !open
    }

    override fun onClick(player: Player) {
        if(canOpen()) {
            open = !open
            if(open) setOpen() else setClosed()
            rideOP.updateMenu()
        }
    }

    abstract fun setOpen()
    abstract fun setClosed()
    abstract fun canOpen(): Boolean
}