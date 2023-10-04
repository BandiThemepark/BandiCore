package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop.transfer

import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.NormalButton
import org.bukkit.entity.Player

class CanCanStorageSendButton(slot: Int, val id: Int): NormalButton(slot, "rideop-cancan-storage-send-title-${id}", "rideop-cancan-storage-send-description-${id}") {
    override fun isAvailable(): Boolean {
        return (rideOP as CanCanRideOP).canSendIntoStorage(id)
    }

    override fun onClick(player: Player) {
        if(!isAvailable()) return
        (rideOP as CanCanRideOP).sendIntoStorage(id)
        rideOP.updateMenu()
    }
}