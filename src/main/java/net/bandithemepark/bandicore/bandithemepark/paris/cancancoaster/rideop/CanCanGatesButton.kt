package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop

import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.GatesButton
import org.bukkit.Bukkit
import org.bukkit.Location

class CanCanGatesButton: GatesButton(0, listOf(
    Location(Bukkit.getWorld("world"), -103.0, -3.0, -38.0),
    Location(Bukkit.getWorld("world"), -103.0, -3.0, -36.0),
    Location(Bukkit.getWorld("world"), -103.0, -3.0, -34.0),
    Location(Bukkit.getWorld("world"), -103.0, -3.0, -32.0),
    Location(Bukkit.getWorld("world"), -103.0, -3.0, -30.0),
)) {
    override fun check(): Boolean {
        return (rideOP as CanCanRideOP).isTrainInStation()
    }
}