package net.bandithemepark.bandicore.park.attractions

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.mode.AttractionMode
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.util.FileManager
import org.bukkit.Location
import org.bukkit.entity.Player

abstract class Attraction(val id: String, val appearance: AttractionAppearance, val exitingLocation: Location) {
    var mode: AttractionMode
    val rideOP = RideOP.get(id)

    fun updateModeInConfig() {
        val fm = FileManager()
        fm.getConfig("config.yml").get().set("attraction-mode.$id", mode.id)
        fm.saveConfig("config.yml")
    }

    init {
        val fm = FileManager()
        mode = if(fm.getConfig("config.yml").get().contains("attraction-mode.$id")) {
            AttractionMode.getMode(fm.getConfig("config.yml").get().getString("attraction-mode.$id")!!)!!
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