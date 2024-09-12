package net.bandithemepark.bandicore.park.npc

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.npc.path.PathPoint
import net.bandithemepark.bandicore.util.npc.NPC
import net.bandithemepark.bandicore.util.npc.NPCPath
import net.kyori.adventure.text.Component
import net.minecraft.server.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer

class ThemeParkNPC(val skin: ThemeParkNPCSkin) {
    val npc = NPC("TPNPC", skin.textureProperty, NPC.NPCVisibilityType.BLACKLIST, getServer())
    var currentObjective: ThemeParkNPCObjective? = null
    val name = ThemeParkNPCNamer().getName()

    var currentRoute: List<PathPoint>? = null
    lateinit var currentPoint: PathPoint

    /**
     * Generates a new objective for the NPC
     */
    private fun getNewObjective() {
        currentObjective = ThemeParkNPCObjective.getNewObjective(this)
    }

    /**
     * Updates the NPC (gives them an objective if they have none, and updates that objective)
     */
    fun update() {
        updateRoute()

        if(currentObjective == null) getNewObjective()
        currentObjective?.onUpdate()
    }

    /**
     * Makes the NPC walk its route
     */
    private fun updateRoute() {
        if(currentRoute == null) return
        if(currentRoute!!.isEmpty()) {
            currentRoute = null
            return
        }

        if(currentPoint == currentRoute!![0]) {
            currentRoute = currentRoute!!.drop(1)

            if(currentRoute!!.isNotEmpty()) {
                walkingTo = currentRoute!![0]
                deBuff = true
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { npc.walkTo(currentRoute!![0].getRandomLocation()); deBuff = false })
            }
        }

        // Once the NPC has finished walking, update the current point
        if(npc.pathfinder.path == null && !deBuff) {
            if(walkingTo != null) {
                currentPoint = walkingTo!!
                walkingTo = null
            }
        }
    }
    var deBuff = false
    var walkingTo: PathPoint? = null

    var spawned = false

    /**
     * Spawns the ThemeParkNPC at a random spawn location
     */
    fun spawn() {
        val spawnLocation = BandiCore.instance.server.themePark.spawningPathPoints.random()
        currentPoint = spawnLocation
        npc.spawn(spawnLocation.getRandomLocation())

        spawned = true
    }

    /**
     * Despawns the ThemeParkNPC
     */
    fun deSpawn() {
        npc.deSpawn()
        spawned = false
    }

    /**
     * Gets the NMS instance of the server
     * @return The NMS instance of the server
     */
    private fun getServer(): MinecraftServer {
        return (Bukkit.getServer() as CraftServer).server
    }
}