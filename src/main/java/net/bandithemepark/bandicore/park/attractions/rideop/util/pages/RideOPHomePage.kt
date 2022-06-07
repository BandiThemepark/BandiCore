package net.bandithemepark.bandicore.park.attractions.rideop.util.pages

import net.bandithemepark.bandicore.park.attractions.rideop.RideOPButton
import net.bandithemepark.bandicore.park.attractions.rideop.RideOPPage

class RideOPHomePage(val rideOPButtons: List<RideOPButton>): RideOPPage(
    "home",
    8,
    1008,
    1009,
    "rideop-page-home-title",
    "rideop-page-home-description"
) {
    override fun getButtons(): List<RideOPButton> {
        return rideOPButtons
    }
}