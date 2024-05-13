package net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan

import net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop.RupsbaanRideOP
import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.park.attractions.AttractionAppearance
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

class RupsbaanAttraction: Attraction("rupsbaan",
    AttractionAppearance("Rupsbaan", listOf("The Galaxy Express 999"), Material.DIAMOND_HOE, 2),
    Location(Bukkit.getWorld("world"), -41.3, 3.0, -204.5, -115.0F, 0.0F)
    ) {

    override fun getPlayerPassengers(): List<Player> {
        val rideOP = RideOP.get("rupsbaan")!! as RupsbaanRideOP
        val players = mutableListOf<Player>()
        rideOP.ride.carts.forEach { players.addAll(it.getPlayers()) }
        return players
    }

    override fun onAttractionStart() {

    }
}