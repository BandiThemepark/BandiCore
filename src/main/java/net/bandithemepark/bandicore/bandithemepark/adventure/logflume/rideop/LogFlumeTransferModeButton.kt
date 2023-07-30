package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.SwitchButton
import net.bandithemepark.bandicore.util.TrackUtil
import org.bukkit.entity.Player

class LogFlumeTransferModeButton(): SwitchButton(0, "rideop-logflume-transfer-mode-title", "rideop-logflume-transfer-mode-description") {
    override fun isActivated(): Boolean {
        return (rideOP as LogFlumeRideOP).transferModeActive
    }

    override fun onClick(player: Player) {
        if((rideOP as LogFlumeRideOP).storageState != LogFlumeRideOP.StorageState.NONE) return

        if((rideOP as LogFlumeRideOP).transferModeActive) {
            (rideOP as LogFlumeRideOP).transferModeActive = false
            rideOP.updateMenu()
        } else {
            if((rideOP as LogFlumeRideOP).getPlayerPassengers().isNotEmpty()) return

            if((rideOP as LogFlumeRideOP).station.currentStopped != null) {
                (rideOP as LogFlumeRideOP).harnessButton.setClosed()
                (rideOP as LogFlumeRideOP).harnessButton.open = false
            }

            (rideOP as LogFlumeRideOP).transferModeActive = true
            rideOP.updateMenu()
        }
    }
}