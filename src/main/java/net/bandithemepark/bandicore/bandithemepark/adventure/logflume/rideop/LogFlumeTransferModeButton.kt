package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.SwitchButton
import org.bukkit.entity.Player

class LogFlumeTransferModeButton(): SwitchButton(0, "rideop-logflume-transfer-mode-title", "rideop-logflume-transfer-mode-description") {
    override fun isActivated(): Boolean {
        return (rideOP as LogFlumeRideOP).transferModeActive
    }

    override fun onClick(player: Player) {
        if((rideOP as LogFlumeRideOP).transferModeActive) {
            (rideOP as LogFlumeRideOP).transferModeActive = false
            rideOP.updateMenu()
        } else {
            if((rideOP as LogFlumeRideOP).getPlayerPassengers().isNotEmpty()) return

            (rideOP as LogFlumeRideOP).transferModeActive = true
            rideOP.updateMenu()
        }
    }
}