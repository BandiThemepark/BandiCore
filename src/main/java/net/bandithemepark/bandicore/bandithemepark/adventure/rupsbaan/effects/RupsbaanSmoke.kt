package net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.effects

import net.bandithemepark.bandicore.park.effect.AmbientEffect
import org.bukkit.Location
import org.bukkit.Particle

class RupsbaanSmoke(val location: Location): AmbientEffect() {
    val spreadRadius = 5.0
    val offsetY = -0.8
    val amountToSpawnPerTick = 1

    override fun onTick() {
        for(i in 0 until amountToSpawnPerTick) {
            spawnParticle()
        }
    }

    fun spawnParticle() {
        location.world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location.clone().add(Math.random()*spreadRadius-(spreadRadius/2.0), offsetY, Math.random()*spreadRadius-(spreadRadius/2.0)), 0, 0.0, 0.01, 0.0)
    }
}