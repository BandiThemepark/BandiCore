package net.bandithemepark.bandicore.park.cosmetics.balloons

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.cosmetics.types.BalloonCosmetic.Companion.getBalloon
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag.Companion.active
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag.Companion.getNameTag
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.debug.Testable
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import net.bandithemepark.bandicore.util.entity.event.SeatEnterEvent
import net.bandithemepark.bandicore.util.entity.event.SeatExitEvent
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import org.joml.Matrix4f

class Balloon(val model: ItemStack, val world: World, var attachedToPlayer: Player? = null): Testable {
    private var physics: BalloonPhysics? = null
    val displayEntity = PacketItemDisplay()
    var leash: BalloonLeash? = null
    val trailParts = mutableListOf<BalloonTrailPart>()
    var showParticles = true

    var overrideAttachmentPoint: Vector? = null

    private fun spawnLeash() {
        leash = BalloonLeash(attachedToPlayer!!, physics!!.position.toLocation(world))
        leash!!.spawn()
    }

    private fun deSpawnLeash() {
        leash!!.deSpawn()
        leash = null
    }

    fun resetVelocity() {
        physics?.resetVelocity()
    }

    var spawned = false
        private set

    fun spawn(attachmentPoint: Vector) {
        if(spawned) return
        spawned = true

        spawnedBalloons.add(this)
        physics = BalloonPhysics(attachmentPoint, this::deSpawn)
        if(attachedToPlayer != null) spawnLeash()

        displayEntity.spawn(physics!!.position.toLocation(world))
        displayEntity.setItemStack(model)
        displayEntity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)
        displayEntity.setInterpolationDuration(2)
        displayEntity.updateMetadata()
        trailParts.forEach { it.spawn(physics!!.position) }
    }

    fun deSpawn() {
        if(!spawned) return
        spawned = false

        displayEntity.deSpawn()
        spawnedBalloons.remove(this)
        if(attachedToPlayer != null) deSpawnLeash()
        playPopParticles()
        trailParts.forEach { it.deSpawn() }
    }

    private fun playPopParticles() {
        if(showParticles) world.spawnParticle(Particle.CLOUD, physics!!.position.toLocation(world).add(0.0, 0.2, 0.0), 20, 0.3, 0.3, 0.3, 0.0)
    }

    fun getPlayerAttachmentPosition(player: Player): Vector {
        return player.location.toVector().add(Vector(0.0, 0.75, 0.0))
    }

    private val trailPosition = mutableListOf(Vector())
    private var lastPosition = Vector()
    private fun updateTrailPhysics() {
        val totalTrailLength = trailParts.sumOf { it.pieceLength }

        var passedDistanceCheck = false
        if(trailPosition.size == 0) {
            trailPosition.add(physics!!.position)
        } else {
            val distance = physics!!.position.distance(lastPosition)
            if(distance > TRAIL_POINT_DISTANCE) {
                passedDistanceCheck = true
                val direction = physics!!.position.clone().subtract(lastPosition).normalize()
                val currentTrailPosition = lastPosition.clone()
                while(currentTrailPosition.distance(physics!!.position) > TRAIL_POINT_DISTANCE) {
                    currentTrailPosition.add(direction.clone().multiply(TRAIL_POINT_DISTANCE))
                    trailPosition.add(0, currentTrailPosition.clone())
                }
            }
        }

        while(trailPosition.size > totalTrailLength/TRAIL_POINT_DISTANCE) {
            trailPosition.removeAt(trailPosition.size-1)
        }

        var passedLength = 0.0
        var lastPartPosition = physics!!.position
        for(trailPart in trailParts) {
            passedLength += trailPart.pieceLength

            val index = (passedLength/TRAIL_POINT_DISTANCE).toInt()
            trailPart.position = if(index >= trailPosition.size) trailPosition.last() else trailPosition[index]

            // Rotate looking to previous position
            val direction = lastPartPosition.clone().subtract(trailPart.position).normalize()
            val yaw = Math.toDegrees(Math.atan2(direction.z, direction.x))
            val pitch = Math.toDegrees(Math.asin(direction.y))
            trailPart.rotation = Vector(-pitch, yaw-90, 0.0)

            lastPartPosition = trailPart.position
        }

        trailParts.forEach { it.updatePosition() }
        if(passedDistanceCheck) lastPosition = physics!!.position
    }

    private fun updatePhysics() {
        if(physics == null) return

        physics!!.attachmentPoint = if(overrideAttachmentPoint != null) overrideAttachmentPoint else if(attachedToPlayer != null) getPlayerAttachmentPosition(attachedToPlayer!!) else null
        physics!!.tick()

        updatePosition()
        updateLeash()
        updateTrailPhysics()
    }

    private fun updateLeash() {
        if(leash == null) {
            if(attachedToPlayer != null) spawnLeash()
        } else {
            if(attachedToPlayer == null) {
                deSpawnLeash()
            } else {
                leash!!.to = physics!!.position.toLocation(leash!!.to.world)
                leash!!.update()
            }
        }
    }

    private fun updatePosition() {
        if(physics == null) return
        displayEntity.moveEntity(physics!!.position.x, physics!!.position.y, physics!!.position.z)

        val quaternion = Quaternion.fromYawPitchRoll(physics!!.rotation.x, physics!!.rotation.y, physics!!.rotation.z)
        displayEntity.setTransformationMatrix(Matrix4f().rotate(quaternion.toBukkitQuaternion()))
        displayEntity.updateMetadata()
    }

    companion object {
        const val TRAIL_POINT_DISTANCE = 0.1

        val spawnedBalloons = mutableListOf<Balloon>()

        fun startTimer() {
            Bukkit.getScheduler().runTaskTimerAsynchronously(BandiCore.instance, Runnable {
                spawnedBalloons.toList().forEach { it.updatePhysics() }
            }, 1, 0)
        }
    }

    override fun test(sender: CommandSender) {
        if(sender !is Player) return
        attachedToPlayer = sender
        spawn(getPlayerAttachmentPosition(sender))

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            attachedToPlayer = null
        }, 20*10)
    }

    class Events: Listener {
        @EventHandler
        fun onGameModeSwitch(event: PlayerGameModeChangeEvent) {
            if(event.newGameMode == GameMode.SPECTATOR) {
                event.player.getBalloon()?.deSpawn()
            } else {
                if(event.player.gameMode == GameMode.SPECTATOR) {
                    event.player.getBalloon()?.spawn(event.player.getBalloon()!!.getPlayerAttachmentPosition(event.player))
                }
            }
        }

        @EventHandler
        fun onQuit(event: PlayerQuitEvent) {
            event.player.getBalloon()?.deSpawn()
        }

        @EventHandler
        fun onSeatEnter(event: SeatEnterEvent) {
            event.player.getBalloon()?.deSpawn()
        }

        @EventHandler
        fun onSeatExit(event: SeatExitEvent) {
            event.player.getBalloon()?.spawn(event.player.getBalloon()!!.getPlayerAttachmentPosition(event.player))
        }
    }
}