package net.bandithemepark.bandicore.park.attractions.rideop

abstract class RideOPPage(val iconSlot: Int, val iconCustomModelData: Int, val clickedIconCustomModelData: Int, val titleTranslationId: String, val descriptionTranslationId: List<String>) {
    abstract fun getButtons(): List<RideOPButton>

    lateinit var rideOP: RideOP
    var loadedButtons = listOf<RideOPButton>()
    fun loadButtons() {
        loadedButtons = getButtons()
        loadedButtons.forEach { it.rideOP = rideOP }
    }
}