package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop

import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.EStopButton

class CanCanEStop: EStopButton(6) {
    override fun setActive(active: Boolean) {
        (rideOP as CanCanRideOP).track.eStop = !isActive()
    }

    override fun isActive(): Boolean {
        return (rideOP as CanCanRideOP).track.eStop
    }
}