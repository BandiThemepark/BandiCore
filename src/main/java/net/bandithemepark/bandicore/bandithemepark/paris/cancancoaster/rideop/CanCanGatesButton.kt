package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop

import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.GatesButton
import org.bukkit.Bukkit
import org.bukkit.Location

class CanCanGatesButton: GatesButton(0, listOf(
    Location(Bukkit.getWorld("world"), -166.0, -11.0, -41.0),
    Location(Bukkit.getWorld("world"), -166.0, -11.0, -39.0),
    Location(Bukkit.getWorld("world"), -166.0, -11.0, -37.0),
    Location(Bukkit.getWorld("world"), -166.0, -11.0, -35.0),
    Location(Bukkit.getWorld("world"), -166.0, -11.0, -33.0),
)) {
    override fun check(): Boolean {
        return (rideOP as CanCanRideOP).isTrainInStation()
    }
}