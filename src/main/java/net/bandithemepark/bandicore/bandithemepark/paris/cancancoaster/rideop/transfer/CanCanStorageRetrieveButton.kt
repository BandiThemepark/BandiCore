package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop.transfer

import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.NormalButton
import org.bukkit.entity.Player

class CanCanStorageRetrieveButton(slot: Int, val id: Int): NormalButton(slot, "rideop-cancan-storage-retrieve-title-${id}", "rideop-cancan-storage-retrieve-description-${id}") {
    override fun isAvailable(): Boolean {
        return (rideOP as CanCanRideOP).canRetrieveFromStorage(id)
    }

    override fun onClick(player: Player) {
        if(!isAvailable()) return
        (rideOP as CanCanRideOP).retrieveFromStorage(id)
        rideOP.updateMenu()
    }
}