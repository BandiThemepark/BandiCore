package net.bandithemepark.bandicore.park.npc.path

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.npc.path.editor.PathPointEditor
import net.bandithemepark.bandicore.util.math.MathUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import kotlin.math.floor

class PathVisualizer {
    var nodeParticleSize = 5.0F
    var pathOptions = Particle.DustOptions(Color.RED, 1.0F)
    var radiusOptions = Particle.DustOptions(Color.PURPLE, 3.0F)

    fun onUpdate() {
        if(PathPointEditor.activeSessions.isEmpty()) return

        for(pathPoint in BandiCore.instance.server.themePark.pathManager.pathPoints) {
            // Showing the particle itself
            sendParticle(pathPoint.location, Particle.DustOptions(pathPoint.type.color, nodeParticleSize))

            // Showing the connections
            if(pathPoint.connectedTo.isNotEmpty()) {
                for(connectedTo in pathPoint.connectedTo) {
                    val distance = MathUtil.getDistanceBetween(pathPoint.location.toVector(), connectedTo.location.toVector())
                    val amountOfParticles = floor(distance * 3.0).toInt()

                    for(i in 0 until amountOfParticles) {
                        val t = i / amountOfParticles.toDouble()
                        
                        val location = Location(pathPoint.location.world,
                            MathUtil.lerp(pathPoint.location.x, connectedTo.location.x, t),
                            MathUtil.lerp(pathPoint.location.y, connectedTo.location.y, t),
                            MathUtil.lerp(pathPoint.location.z, connectedTo.location.z, t)
                        )

                        sendParticle(location, pathOptions)
                    }
                }
            }

            // Showing the radius
            if(pathPoint.radius > 0.0) {
                val amountOfParticles = floor(pathPoint.radius * 4.0).toInt()

                for(i in 0 until amountOfParticles) {
                    val offset = MathUtil.getPointOnCircleXZ(pathPoint.radius, i*(360.0/amountOfParticles))
                    sendParticle(pathPoint.location.clone().add(offset), radiusOptions)
                }
            }
        }
    }

    fun sendParticle(location: Location, options: Particle.DustOptions) {
        PathPointEditor.activeSessions.forEach {
            it.player.spawnParticle(Particle.DUST, location, 1, options)
        }
    }
}