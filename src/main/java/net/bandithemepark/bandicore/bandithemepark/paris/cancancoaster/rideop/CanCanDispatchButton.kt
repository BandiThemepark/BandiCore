package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop

import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.DispatchButton
import org.bukkit.entity.Player

class CanCanDispatchButton: DispatchButton(4) {

    override fun onDispatch(player: Player) {
        (rideOP as CanCanRideOP).dispatch()
    }

    override fun isAvailable(): Boolean {
        return (rideOP as CanCanRideOP).canDispatch()
    }
}