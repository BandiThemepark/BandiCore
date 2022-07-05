package net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.IndicatorButton

class RupsbaanStatusButton: IndicatorButton(6, "rideop-rupsbaan-status-title", "rideop-rupsbaan-status-description") {
    override fun isAvailable(): Boolean {
        return (rideOP as RupsbaanRideOP).rideSchedule.active
    }
}