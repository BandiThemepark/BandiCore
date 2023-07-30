package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.NormalButton
import org.bukkit.entity.Player

class LogFlumeTransferSendButton: NormalButton(2, "rideop-logflume-transfer-send-title", "rideop-logflume-transfer-send-description") {
    override fun isAvailable(): Boolean {
        return (rideOP as LogFlumeRideOP).canSendIntoStorage()
    }

    override fun onClick(player: Player) {
        (rideOP as LogFlumeRideOP).sendIntoStorage()
    }
}