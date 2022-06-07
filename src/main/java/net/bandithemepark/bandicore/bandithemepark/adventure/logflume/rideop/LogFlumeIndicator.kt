package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.IndicatorButton

class LogFlumeIndicator: IndicatorButton(3, "rideop-button-block-clear-title", "rideop-button-block-clear-description") {
    override fun isAvailable(): Boolean {
        val rideOP = RideOP.get("logflume") as LogFlumeRideOP
        return rideOP.dispatchDelay == 0 && !rideOP.layout.eStop && rideOP.station.currentStopped != null
    }
}