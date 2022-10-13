package net.bandithemepark.bandicore.park

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.npc.ThemeParkNPCManager
import net.bandithemepark.bandicore.park.npc.objectives.TestObjective
import net.bandithemepark.bandicore.park.npc.path.PathManager
import net.bandithemepark.bandicore.park.npc.path.PathPoint
import org.bukkit.Bukkit
import org.bukkit.World
import java.util.*

class ThemePark(val world: World) {
    val pathManager = PathManager()
    val themeParkNPCManager = ThemeParkNPCManager()
    lateinit var spawningPathPoints: MutableList<PathPoint>

    fun setup() {
        pathManager.loadPaths()

        TestObjective().register()

        spawningPathPoints = mutableListOf(
            pathManager.pathPoints.find { it.uuid == UUID.fromString("5433b419-5cb2-46b2-b240-13476efb21d8") }!!
        )

        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, Runnable {
            themeParkNPCManager.spawnAmount(50) // TODO Change amount later
        }, 40)
    }
}