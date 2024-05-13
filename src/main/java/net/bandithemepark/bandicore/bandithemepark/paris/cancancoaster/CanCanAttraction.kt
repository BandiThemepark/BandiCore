package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster

import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.park.attractions.AttractionAppearance
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

class CanCanAttraction: Attraction(
    "cancancoaster",
    AttractionAppearance("CanCan Coaster", listOf("Youri hasn't added a description yet"), Material.DIAMOND_HOE, 7),
    Location(Bukkit.getWorld("world")!!, -96.5, -3.0, -42.0, 180f, -10f)
    ) {

    override fun getPlayerPassengers(): List<Player> {
        return (rideOP as CanCanRideOP).getPlayerPassengers()
    }

    override fun onAttractionStart() {

    }
}