package net.bandithemepark.bandicore.bandithemepark.kaliba

import net.bandithemepark.bandicore.park.effect.Waterfall
import org.bukkit.Bukkit
import org.bukkit.Location

class KalibaEffects {
    init {
        Waterfall(Location(Bukkit.getWorld("world"), 40.5, -2.0, -117.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 45.5, -6.0, -128.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 33.5, -7.0, -128.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 42.5, -6.0, -122.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 41.5, -6.0, -121.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 40.5, -6.0, -121.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 43.5, -2.0, -121.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 42.5, -2.0, -118.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 41.5, -2.0, -117.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 39.5, -2.0, -116.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 33.5, 7.0, -112.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 32.5, 7.0, -113.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 39.5, 5.0, -112.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 41.5, 5.0, -113.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 41.5, 5.0, -114.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 42.5, 5.0, -115.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 44.5, 10.0, -112.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 45.5, 10.0, -113.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 46.5, 10.0, -115.5)).register()
        Waterfall(Location(Bukkit.getWorld("world"), 48.5, 14.0, -113.5)).register()
    }
}