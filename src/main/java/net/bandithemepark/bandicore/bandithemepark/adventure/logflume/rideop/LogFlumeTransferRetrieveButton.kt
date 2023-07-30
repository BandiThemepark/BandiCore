package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.NormalButton
import org.bukkit.entity.Player

class LogFlumeTransferRetrieveButton: NormalButton(3, "rideop-logflume-transfer-retrieve-title", "rideop-logflume-transfer-retrieve-description") {
    override fun isAvailable(): Boolean {
        return (rideOP as LogFlumeRideOP).canRetrieveFromStorage()
    }

    override fun onClick(player: Player) {
        (rideOP as LogFlumeRideOP).retrieveFromStorage()
    }
}