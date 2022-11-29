package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.effects

import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import kotlin.random.Random

class MonkeyBlowEffect(val location: Location, val direction: Direction, val amountPerTick: Int = 1) {
    private val colors = mutableListOf(
        Color.fromRGB(246, 247, 248),
        Color.fromRGB(228, 234, 241),
        Color.fromRGB(210, 219, 229),
        Color.fromRGB(192, 203, 214)
    )

    private fun getRandomDustOptions(): DustOptions {
        val color = colors.random()
        return DustOptions(color, 3.0F)
    }

    fun update() {
        for(i in 1..amountPerTick) {
            val yOffset = Random.nextDouble(-0.2, 0.2)
            val otherAxisOffset = Random.nextDouble(-0.2, 0.2)
            val mainAxisOffset = 0.3

            val xOffset = when(direction) {
                Direction.POS_X -> mainAxisOffset
                Direction.NEG_X -> -mainAxisOffset
                Direction.POS_Z -> otherAxisOffset
                Direction.NEG_Z -> otherAxisOffset
            }

            val zOffset = when(direction) {
                Direction.POS_X -> otherAxisOffset
                Direction.NEG_X -> otherAxisOffset
                Direction.POS_Z -> mainAxisOffset
                Direction.NEG_Z -> -mainAxisOffset
            }

            location.world.spawnParticle(
                Particle.REDSTONE,
                location,
                0,
                xOffset,
                yOffset,
                zOffset,
                getRandomDustOptions()
            )

            location.world.spawnParticle(
                Particle.CLOUD,
                location,
                0,
                xOffset,
                yOffset,
                zOffset,
            )
        }
    }

    enum class Direction {
        POS_X, NEG_X, POS_Z, NEG_Z
    }
}