package net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.DispatchButton
import org.bukkit.entity.Player

class RupsbaanDispatchButton: DispatchButton(5) {
    override fun onDispatch(player: Player) {
        (rideOP as RupsbaanRideOP).rideSchedule.start()
    }

    override fun isAvailable(): Boolean {
        if((rideOP as RupsbaanRideOP).rideSchedule.active) return false
        if((rideOP.loadedPages[0].loadedButtons.find { it is RupsbaanGatesButton } as RupsbaanGatesButton).open) return false
        if((rideOP as RupsbaanRideOP).ride.eStop) return false
        for(cart in (rideOP as RupsbaanRideOP).ride.carts) if(cart.harnessPosition != 0.0) return false

        return true
    }
}