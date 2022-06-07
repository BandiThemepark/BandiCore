package net.bandithemepark.bandicore.park.attractions.rideop.util.pages

import net.bandithemepark.bandicore.park.attractions.rideop.RideOPButton
import net.bandithemepark.bandicore.park.attractions.rideop.RideOPPage

class RideOPStoragePage(val rideOPButtons: List<RideOPButton>): RideOPPage(
    "storage",
    26,
    1010,
    1011,
    "rideop-page-storage-title",
    "rideop-page-storage-description"
) {
    override fun getButtons(): List<RideOPButton> {
        return rideOPButtons
    }
}