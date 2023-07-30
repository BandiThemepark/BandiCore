package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.SimpleHarnessButton
import net.bandithemepark.bandicore.util.TrackUtil

class LogFlumeHarnessButton: SimpleHarnessButton(1) {
    override fun setOpen() {
        val rideOP = RideOP.get("logflume") as LogFlumeRideOP
        TrackUtil.setHarnessOpen(rideOP.station.currentStopped!!, true)
    }

    override fun setClosed() {
        val rideOP = RideOP.get("logflume") as LogFlumeRideOP
        TrackUtil.setHarnessOpen(rideOP.station.currentStopped!!, false)
    }

    override fun canOpen(): Boolean {
        val rideOP = RideOP.get("logflume") as LogFlumeRideOP

        if(rideOP.transferModeActive) return false
        if(rideOP.station.currentStopped == null) return false

        return true
    }
}