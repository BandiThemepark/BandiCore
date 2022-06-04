package net.bandithemepark.bandicore.bandithemepark.adventure.logflume

import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.park.attractions.AttractionAppearance
import org.bukkit.Material
import org.bukkit.entity.Player

class LogFlumeAttraction: Attraction("logflume", AttractionAppearance("Logflume", listOf("Blame Youri for not having", "added a description yet"), Material.DIAMOND_HOE, 1)) {
    override fun getPlayerPassengers(): List<Player> {
        return emptyList()
    }

    override fun onAttractionStart() {

    }
}