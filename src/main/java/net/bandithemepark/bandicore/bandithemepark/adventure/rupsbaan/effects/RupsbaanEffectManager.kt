package net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.effects

import net.bandithemepark.bandicore.park.effect.Fountain
import net.bandithemepark.bandicore.park.effect.Waterfall
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.Vector

class RupsbaanEffectManager {
    init {
        Waterfall(Location(Bukkit.getWorld("world"), -29.5, 5.0, -195.5)).register()

        Fountain(Location(Bukkit.getWorld("world"), -27.5, 4.0, -192.5), Vector(0.0, 0.35, 0.0)).register()
        Fountain(Location(Bukkit.getWorld("world"), -26.8, 4.0, -193.5), Vector(0.0, 0.35, 0.0)).register()
        Fountain(Location(Bukkit.getWorld("world"), -26.5, 4.0, -194.5), Vector(0.0, 0.35, 0.0)).register()
        Fountain(Location(Bukkit.getWorld("world"), -26.2, 4.0, -195.5), Vector(0.0, 0.35, 0.0)).register()
        Fountain(Location(Bukkit.getWorld("world"), -26.5, 4.0, -196.5), Vector(0.0, 0.35, 0.0)).register()
        Fountain(Location(Bukkit.getWorld("world"), -26.8, 4.0, -197.5), Vector(0.0, 0.35, 0.0)).register()
        Fountain(Location(Bukkit.getWorld("world"), -27.5, 4.0, -198.5), Vector(0.0, 0.35, 0.0)).register()
    }
}