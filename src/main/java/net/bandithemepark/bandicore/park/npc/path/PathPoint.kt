package net.bandithemepark.bandicore.park.npc.path

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.npc.NPCPath
import net.bandithemepark.bandicore.util.npc.NPCPathfinding
import org.bukkit.Location
import java.util.UUID

class PathPoint(val uuid: UUID, val location: Location, var radius: Double, var type: PathPointType, val connectedTo: MutableList<PathPoint>) {
    val preCalculatedPaths = mutableListOf<NPCPath>()

    fun getRandomLocation(): Location {
        if(radius == 0.0) return location

        val radius = Math.random() * radius
        val angle = Math.random() * Math.PI * 2

        val x = location.x + Math.cos(angle) * radius
        val z = location.z + Math.sin(angle) * radius

        return Location(location.world, x, location.y, z)
    }

    fun preCalculatePath() {
        preCalculatedPaths.clear()

        connectedTo.forEach {
            val path = NPCPathfinding.getPath(getRandomLocation(), it.getRandomLocation())!!
            preCalculatedPaths.add(NPCPathfinding.toPath(path, BandiCore.instance.server.themePark.world))
        }
    }
}