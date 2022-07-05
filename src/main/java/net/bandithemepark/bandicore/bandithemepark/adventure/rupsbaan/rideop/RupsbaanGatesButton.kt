package net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.GatesButton
import org.bukkit.Bukkit
import org.bukkit.Location

class RupsbaanGatesButton: GatesButton(0, listOf(Location(Bukkit.getWorld("world"), -41.0, 3.0, -184.0))) {
    override fun check(): Boolean {
        if((rideOP as RupsbaanRideOP).rideSchedule.active) return false

        return true
    }
}