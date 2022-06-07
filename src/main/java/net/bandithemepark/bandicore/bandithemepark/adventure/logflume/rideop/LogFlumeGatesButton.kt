package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.GatesButton
import org.bukkit.Bukkit
import org.bukkit.Location

class LogFlumeGatesButton: GatesButton(0, listOf(Location(Bukkit.getWorld("world")!!, 58.0, 1.0, -166.0), Location(Bukkit.getWorld("world")!!, 58.0, 1.0, -164.0))) {
    override fun check(): Boolean {
        val rideOP = RideOP.get("logflume") as LogFlumeRideOP
        return rideOP.station.currentStopped != null
    }
}