package net.bandithemepark.bandicore.bandithemepark.adventure.logflume

import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.park.attractions.AttractionAppearance
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

class LogFlumeAttraction: Attraction("logflume",
    AttractionAppearance("Logflume", listOf("Join Bandiana Jones on his adventure", "and uncover the secrets of a newly", "discovered temple. Be prepared for", "things to get out of hand!"), Material.DIAMOND_HOE, 1),
    Location(Bukkit.getWorld("world"), 63.5, 1.0, -171.5, -45.0F, 0.0F)
    ) {
    override fun getPlayerPassengers(): List<Player> {
        return emptyList()
    }

    override fun onAttractionStart() {

    }
}