package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop

import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.segments.CanCanLiftSegment
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.IndicatorButton

class CanCanIndicator: IndicatorButton(3, "rideop-button-block-clear-title", "rideop-button-block-clear-description") {
    override fun isAvailable(): Boolean {
        val rideOP = rideOP as CanCanRideOP
        return ((rideOP.liftSegment.type as CanCanLiftSegment).available) && rideOP.isTrainInStation() && !rideOP.track.eStop
    }
}