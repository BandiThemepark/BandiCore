package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.EStopButton

class LogFlumeEStop: EStopButton(6) {
    override fun isActive(): Boolean {
        return (rideOP as LogFlumeRideOP).layout.eStop
    }

    override fun setActive(active: Boolean) {
        (rideOP as LogFlumeRideOP).layout.eStop = !isActive()
    }
}