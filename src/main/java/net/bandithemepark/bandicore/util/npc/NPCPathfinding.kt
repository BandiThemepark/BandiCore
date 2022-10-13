package net.bandithemepark.bandicore.util.npc

import com.destroystokyo.paper.entity.Pathfinder
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import net.bandithemepark.bandicore.park.npc.ThemeParkNPCSkin
import net.bandithemepark.bandicore.util.Util
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.util.Vector

class NPCPathfinding(val npc: NPC, var speed: Double) {
    var path: NPCPath? = null
    var goal: Location? = null

    // Function that makes the NPC calculate a path and then follow it to a location
    /**
     * Makes the NPC walk to a given location
     * @param location The location to walk to
     */
    fun walkTo(location: Location) {
        val paperPath = getPath(npc.location!!, location)
        if(paperPath != null) {
            path = toPath(paperPath, npc.location!!.world)
        } else {
            // TODO Manage the situation when no path is found (or the pathfinding zombie is dead?)
        }
    }

    // The updating function that handles moving the NPC along the path
    fun update() {
        if(npc.spawned) {
            if (goal != null) {
                val distanceToMovePerTick = speed / 20.0
                val distanceToGoal = Util.getLengthBetween(npc.location!!, goal!!)

                // If check to see if the NPC isn't already at the goal
                if(distanceToGoal > distanceToMovePerTick) {
                    // Moving the NPC towards it's next goal on the path
                    val newLocation = goal!!.clone().subtract(npc.location!!.clone()).toVector().normalize().multiply(distanceToMovePerTick).add(npc.location!!.toVector())

                    val directionLoc = goal!!.clone()
                    directionLoc.direction = newLocation.clone().subtract(npc.location!!.clone().toVector())

                    npc.moveHead(directionLoc.pitch, directionLoc.yaw)
                    npc.teleport(newLocation.toLocation(npc.location!!.world, directionLoc.yaw, directionLoc.pitch))
                } else {
                    // Moving the NPC to the final goal
                    val directionLoc = goal!!.clone()
                    directionLoc.direction = goal!!.clone().subtract(npc.location!!.clone()).toVector()

                    npc.moveHead(directionLoc.pitch, directionLoc.yaw)

                    val newLocation = goal!!.clone()
                    newLocation.pitch = directionLoc.pitch
                    newLocation.yaw = directionLoc.yaw
                    npc.teleport(newLocation)

                    goal = null
                }
            }

            if(goal == null && path != null) {
                val nextPoint = path!!.points[0]
                goal = Location(path!!.world, nextPoint.x, nextPoint.y, nextPoint.z)
                path!!.points.removeFirst()

                if(path!!.points.isEmpty()) path = null
            }
        }
    }

    companion object {
        lateinit var testEntity: Zombie
        lateinit var holdingLocation: Location

        fun setup(holdingLocation: Location) {
            this.holdingLocation = holdingLocation
            testEntity = holdingLocation.world.spawnEntity(holdingLocation, EntityType.ZOMBIE) as Zombie
            testEntity.isInvisible = true
            testEntity.setAdult()
            testEntity.isSilent = true
            testEntity.isInvulnerable = true
            testEntity.equipment.setItemInMainHand(null)
            testEntity.isPersistent = true
            testEntity.customName(Component.text("PathfindingZombie"))
            testEntity.removeWhenFarAway = false
        }

        /**
         * Function that gets the path from one location to another. Uses zombie pathfinding
         * @param from The location to start the path from
         * @param to The location to end the path at
         * @return The path as a Paper instance
         */
        fun getPath(from: Location, to: Location): Pathfinder.PathResult? {
            testEntity.teleport(from)
            val path = testEntity.pathfinder.findPath(to)
            testEntity.teleport(holdingLocation)
            return path
        }

        // Function to convert a PaperSpigot Path to our own Path class
        fun toPath(paperPath: Pathfinder.PathResult, world: World): NPCPath {
            val newPoints = mutableListOf<Vector>()
            paperPath.points.forEach { newPoints.add(Vector(it.x+0.5, it.y, it.z+0.5)) }
            return NPCPath(world, newPoints)
        }
    }

    class TestCommand: CommandExecutor {
        companion object {
            val activeNPCs = hashMapOf<Player, NPC>()
        }

        override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
            if(command.name.equals("npctest", true)) {
                if(sender is Player) {
                    if(!activeNPCs.containsKey(sender)) {
                        val npc = NPC("Test", ThemeParkNPCSkin.getFromPlayer(sender).textureProperty, NPC.NPCVisibilityType.BLACKLIST, NPC.getMinecraftServer(sender)!!)
                        npc.spawn(sender.location)
                        npc.moveHead(sender.location.pitch, sender.location.yaw)
                        npc.walkSpeed = 3.0
                        activeNPCs[sender] = npc
                    } else {
                        val npc = activeNPCs[sender]!!
                        npc.walkTo(Location(sender.world, sender.location.x, sender.location.y, sender.location.z))
                    }
                }
            }
            return false
        }
    }
}