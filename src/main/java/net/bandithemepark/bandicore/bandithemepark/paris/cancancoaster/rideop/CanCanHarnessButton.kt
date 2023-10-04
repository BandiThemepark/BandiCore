package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop

import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.SimpleHarnessButton
import net.bandithemepark.bandicore.util.TrackUtil

class CanCanHarnessButton: SimpleHarnessButton(1) {
    override fun setOpen() {
        TrackUtil.setHarnessOpen((rideOP as CanCanRideOP).stationSegment.vehicles[0], true)
    }

    override fun setClosed() {
        TrackUtil.setHarnessOpen((rideOP as CanCanRideOP).stationSegment.vehicles[0], false)
    }

    override fun canOpen(): Boolean {
        val rideOP = rideOP as CanCanRideOP

        if(!rideOP.isTrainInStation()) return false
        if(rideOP.transferMode) return false

        return true
    }
}