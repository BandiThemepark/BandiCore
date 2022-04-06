package net.bandithemepark.bandicore.util.npc

import com.destroystokyo.paper.entity.Pathfinder
import net.bandithemepark.bandicore.util.Util
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.util.Vector

class NPCPathfinding(val npc: NPC, var speed: Double) {
    var path: NPCPath? = null
    var goal: Location? = null

    private fun toPath(paperPath: Pathfinder.PathResult, world: World): NPCPath {
        val newPoints = mutableListOf<Vector>()
        paperPath.points.forEach { newPoints.add(Vector(it.x+0.5, it.y, it.z+0.5)) }
        return NPCPath(world, newPoints)
    }

    fun walkTo(location: Location) {
        val paperPath = getPath(npc.location!!, location)
        if(paperPath != null) {
            path = toPath(paperPath, npc.location!!.world)
        } else {
            // TODO Situatie handelen als er geen path gevonden is
        }
    }

    fun update() {
        if(npc.spawned) {
            if (goal != null) {
                val distanceToMovePerTick = speed / 20.0
                val distanceToGoal = Util.getLengthBetween(npc.location!!, goal!!)

                if(distanceToGoal > distanceToMovePerTick) {
                    val newLocation = goal!!.clone().subtract(npc.location!!.clone()).toVector().normalize().multiply(distanceToMovePerTick).add(npc.location!!.toVector())

                    val directionLoc = goal!!.clone()
                    directionLoc.direction = newLocation.clone().subtract(npc.location!!.clone().toVector())

                    npc.moveHead(directionLoc.pitch, directionLoc.yaw)
                    npc.teleport(newLocation.toLocation(npc.location!!.world))
                } else {
                    val directionLoc = goal!!.clone()
                    directionLoc.direction = goal!!.clone().subtract(npc.location!!.clone()).toVector()

                    npc.moveHead(directionLoc.pitch, directionLoc.yaw)
                    npc.teleport(goal!!)

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
        }

        fun getPath(from: Location, to: Location): Pathfinder.PathResult? {
            testEntity.teleport(from)
            val path = testEntity.pathfinder.findPath(to)
            testEntity.teleport(holdingLocation)
            return path
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
                        val npc = NPC("Test", sender, NPC.NPCVisibilityType.BLACKLIST, NPC.getMinecraftServer(sender)!!)
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