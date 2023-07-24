package net.bandithemepark.bandicore.park.npc.path

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.npc.path.finding.DijkstraPathfinding
import net.bandithemepark.bandicore.util.FileManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.util.*

class PathManager {
    val pathPoints = mutableListOf<PathPoint>()

    init {
        startVisualizer()
    }

    fun startVisualizer() {
        val visualizer = PathVisualizer()

        object: BukkitRunnable() {
            override fun run() {
                visualizer.onUpdate()
            }
        }.runTaskTimerAsynchronously(BandiCore.instance, 0, 10)
    }

    // A function that calculates the fastest route from one path point to another
    fun getFastestRoute(start: PathPoint, end: PathPoint): List<PathPoint> {
        return DijkstraPathfinding(this).run(start, end)
    }

    // Function to get the path point closest to a location
    fun getClosestPathPoint(location: Location): PathPoint? {
        var closestPathPoint: PathPoint? = null
        var closestDistance = Double.MAX_VALUE
        for (pathPoint in pathPoints) {
            val distance = pathPoint.location.distance(location)
            if (distance < closestDistance) {
                closestDistance = distance
                closestPathPoint = pathPoint
            }
        }
        return closestPathPoint
    }

    private fun preCalculatePaths() {
        pathPoints.filter { it.type.precalculate }.forEach {
            //it.preCalculatePath()
        }
    }

    fun loadPaths() {
        pathPoints.clear()
        val fm = FileManager()

        if(!File("plugins/BandiCore/paths.yml").exists()) return
        if(!fm.getConfig("paths.yml").get().contains("paths")) return

        val newPathPoints = hashMapOf<PathPoint, List<String>>()
        val world = BandiCore.instance.server.themePark.world

        for(id in fm.getConfig("paths.yml").get().getConfigurationSection("paths")!!.getKeys(false)) {
            // Getting the location
            val x = fm.getConfig("paths.yml").get().getDouble("paths.$id.x")
            val y = fm.getConfig("paths.yml").get().getDouble("paths.$id.y")
            val z = fm.getConfig("paths.yml").get().getDouble("paths.$id.z")
            val location = Location(world, x, y, z)

            // Getting radius and type
            val radius = fm.getConfig("paths.yml").get().getDouble("paths.$id.radius")
            val typeId = fm.getConfig("paths.yml").get().getInt("paths.$id.type")
            val type = PathPointType.values()[typeId]

            // Create instance with empty list for connections (those will be applied later)
            val pathPoint = PathPoint(UUID.fromString(id), location, radius, type, mutableListOf())

            // Putting the point in the map with its connections
            newPathPoints[pathPoint] = fm.getConfig("paths.yml").get().getStringList("paths.$id.connections")
        }

        // Resolve connections
        for((pathPoint, connections) in newPathPoints.entries) {
            for(connection in connections) {
                val connectionPoint = newPathPoints.keys.find { it.uuid.toString() == connection }
                if(connectionPoint != null) {
                    pathPoint.connectedTo.add(connectionPoint)
                }
            }
        }

        // Add all points to the list
        pathPoints.addAll(newPathPoints.keys)

        // Precalculating the paths where needed
        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, { preCalculatePaths() }, 20)
    }

    fun savePaths() {
        val fm = FileManager()

        if(!File("plugins/BandiCore/paths.yml").exists()) {
            File("plugins/BandiCore/paths.yml").createNewFile()
        }

        // Clearing the old file
        fm.getConfig("paths.yml").get().set("paths", null)

        // Adding all points to the file
        for(point in pathPoints) {
            fm.getConfig("paths.yml").get().set("paths." + point.uuid.toString() + ".x", point.location.x)
            fm.getConfig("paths.yml").get().set("paths." + point.uuid.toString() + ".y", point.location.y)
            fm.getConfig("paths.yml").get().set("paths." + point.uuid.toString() + ".z", point.location.z)
            fm.getConfig("paths.yml").get().set("paths." + point.uuid.toString() + ".radius", point.radius)
            fm.getConfig("paths.yml").get().set("paths." + point.uuid.toString() + ".type", point.type.ordinal)
            fm.getConfig("paths.yml").get().set("paths." + point.uuid.toString() + ".connections", point.connectedTo.map { it.uuid.toString() })
        }

        // Saving the file
        fm.saveConfig("paths.yml")
    }
}