package net.bandithemepark.bandicore.park.attractions.rideop.util.pages

import net.bandithemepark.bandicore.park.attractions.rideop.RideOPButton
import net.bandithemepark.bandicore.park.attractions.rideop.RideOPPage

class RideOPCameraPage(val rideOPButtons: List<RideOPButton>): RideOPPage(
    "cameras",
    17,
    1006,
    1007,
    "rideop-page-cameras-title",
    "rideop-page-cameras-description"
) {
    override fun getButtons(): List<RideOPButton> {
        return rideOPButtons
    }
}