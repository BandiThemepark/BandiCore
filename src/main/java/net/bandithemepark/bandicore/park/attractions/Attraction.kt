package net.bandithemepark.bandicore.park.attractions

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.mode.AttractionMode
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import org.bukkit.Location
import org.bukkit.entity.Player

abstract class Attraction(val id: String, val appearance: AttractionAppearance, val exitingLocation: Location) {
    var mode: AttractionMode
    val rideOP = RideOP.get(id)

    fun updateModeInConfig() {
        BandiCore.instance.config.json.getAsJsonObject("attraction-mode").addProperty(id, mode.id)
        BandiCore.instance.config.save()
    }

    init {
        mode = if(BandiCore.instance.config.json.getAsJsonObject("attraction-mode").has(id)) {
            AttractionMode.getMode(BandiCore.instance.config.json.getAsJsonObject("attraction-mode").get(id).asString)!!
        } else {
            AttractionMode.getMode("closed")!!
        }
    }

    abstract fun getPlayerPassengers(): List<Player>
    abstract fun onAttractionStart()

    fun register() {
        attractions.add(this)
        BandiCore.instance.server.ridecounterManager.setupOfRide(id)
    }

    companion object {
        val attractions = mutableListOf<Attraction>()

        fun get(id: String): Attraction? {
            return attractions.find { it.id == id }
        }
    }
}