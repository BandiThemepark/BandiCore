package net.bandithemepark.bandicore.park.attractions.rideop

abstract class RideOPPage(val id: String, val iconSlot: Int, val iconCustomModelData: Int, val clickedIconCustomModelData: Int, val titleTranslationId: String, val descriptionTranslationId: String) {
    abstract fun getButtons(): List<RideOPButton>

    lateinit var rideOP: RideOP
    var loadedButtons = listOf<RideOPButton>()
    fun loadButtons() {
        loadedButtons = getButtons()
        loadedButtons.forEach { it.rideOP = rideOP }
    }

    companion object {
    fun convertRideOPSlotToBukkitSlot(slot: Int): Int {
        when(slot) {
            0 -> return 1
            1 -> return 2
            2 -> return 3
            3 -> return 4
            4 -> return 5
            5 -> return 6
            6 -> return 7

            7 -> return 10
            8 -> return 11
            9 -> return 12
            10 -> return 13
            11 -> return 14
            12 -> return 15
            13 -> return 16

            14 -> return 19
            15 -> return 20
            16 -> return 21
            17 -> return 22
            18 -> return 23
            19 -> return 24
            20 -> return 25

            21 -> return 28
            22 -> return 29
            23 -> return 30
            24 -> return 31
            25 -> return 32
            26 -> return 33
            27 -> return 34

            else -> return -1
        }
    }

    fun convertBukkitSlotToRideOPSlot(slot: Int): Int {
        when (slot) {
            1 -> return 0
            2 -> return 1
            3 -> return 2
            4 -> return 3
            5 -> return 4
            6 -> return 5
            7 -> return 6

            10 -> return 7
            11 -> return 8
            12 -> return 9
            13 -> return 10
            14 -> return 11
            15 -> return 12
            16 -> return 13

            19 -> return 14
            20 -> return 15
            21 -> return 16
            22 -> return 17
            23 -> return 18
            24 -> return 19
            25 -> return 20

            28 -> return 21
            29 -> return 22
            30 -> return 23
            31 -> return 24
            32 -> return 25
            33 -> return 26
            34 -> return 27

            else -> return -1
        }
    }
    }
}