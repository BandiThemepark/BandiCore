package net.bandithemepark.bandicore.park.effect

import org.bukkit.Location
import org.bukkit.Particle

class Waterfall(val location: Location): AmbientEffect() {
    override fun onTick() {
        location.world.spawnParticle(Particle.WATER_SPLASH, location.clone().add(Math.random()*1.3-0.65, -0.2, Math.random()*1.3-0.65), 5)
        location.world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location.clone().add(Math.random()*1.3-0.65, -0.2, Math.random()*1.3-0.65), 0, 0.0, 0.01, 0.0)
    }
}