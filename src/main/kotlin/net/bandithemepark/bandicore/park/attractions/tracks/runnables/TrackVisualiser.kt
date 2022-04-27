package net.bandithemepark.bandicore.park.attractions.tracks.runnables

import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle

class TrackVisualiser {
    var delay = 0

    var nodeOptions = Particle.DustOptions(Color.RED, 5.0F)
    var pathOptions = Particle.DustOptions(Color.fromRGB(71, 71, 71), 1.0F)
    var rollOptions = Particle.DustOptions(Color.LIME, 4.0F)
    var segmentOptions = Particle.DustOptions(Color.AQUA, 4.0F)
    var triggerOptions = Particle.DustOptions(Color.YELLOW, 4.0F)

    fun onTick() {
        delay++

        if (delay >= 9) {
            delay = 0

            for(session in BandiCore.instance.trackManager.editor.activeSessions) {
                val origin = session.layout.origin

                for(node in session.layout.nodes) {
                    val nodeLocation = Location(session.layout.world, node.x, node.y, node.z)
                    nodeLocation.add(origin)
                    session.player.spawnParticle(Particle.REDSTONE, nodeLocation, 1, nodeOptions)

                    var curvePause = 0
                    for(curvePoint in node.curve) {
                        curvePause++
                        if(curvePause == 3) {
                            curvePause = 0
                            val curveLocation = Location(session.layout.world, curvePoint.x, curvePoint.y, curvePoint.z)
                            curveLocation.add(origin)
                            session.player.spawnParticle(Particle.REDSTONE, curveLocation, 1, pathOptions)
                        }
                    }
                }

                for(rollNode in session.layout.rollNodes) {
                    val node = rollNode.position.getPathPoint().asVector()
                    node.add(origin)
                    session.player.spawnParticle(Particle.REDSTONE, node.toLocation(session.layout.world), 1, rollOptions)
                }

                // TODO Show all others like segment separators and triggers
            }
        }
    }
}