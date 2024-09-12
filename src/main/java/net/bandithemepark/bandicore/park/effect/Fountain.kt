package net.bandithemepark.bandicore.park.effect

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.random.Random

class Fountain(val location: Location, var direction: Vector): AmbientEffect() {
    var active = true

    override fun onTick() {
        if(!active) return

        location.world!!.spawnParticle(Particle.ITEM, location, 0, direction.x, direction.y, direction.z, getParticleBlock())
    }

    fun getParticleBlock(): ItemStack {
        return when(Random.nextInt(2)) {
            0 -> ItemStack(Material.CYAN_STAINED_GLASS)
            1 -> ItemStack(Material.BLUE_STAINED_GLASS)
            else -> ItemStack(Material.WATER)
        }
    }
}