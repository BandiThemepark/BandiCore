package net.bandithemepark.bandicore.park.cosmetics.balloons

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.debug.Testable
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import org.joml.Matrix4f

class Balloon(val model: ItemStack, val world: World, var attachedToPlayer: Player? = null): Testable {
    private var physics: BalloonPhysics? = null
    val displayEntity = PacketItemDisplay()
    var leash: BalloonLeash? = null

    private fun spawnLeash() {
        leash = BalloonLeash(attachedToPlayer!!, physics!!.position.toLocation(world))
        leash!!.spawn()
    }

    private fun deSpawnLeash() {
        leash!!.deSpawn()
        leash = null
    }

    private fun spawn(attachmentPoint: Vector) {
        spawnedBalloons.add(this)
        physics = BalloonPhysics(attachmentPoint, this::deSpawn)
        if(attachedToPlayer != null) spawnLeash()

        displayEntity.spawn(physics!!.position.toLocation(world))
        displayEntity.setItemStack(model)
        displayEntity.setInterpolationDuration(2)
        displayEntity.updateMetadata()
    }

    private fun deSpawn() {
        displayEntity.deSpawn()
        spawnedBalloons.remove(this)
        if(attachedToPlayer != null) deSpawnLeash()
        playPopParticles()
    }

    private fun playPopParticles() {
        world.spawnParticle(Particle.CLOUD, physics!!.position.toLocation(world).add(0.0, 0.2, 0.0), 20, 0.3, 0.3, 0.3, 0.0)
    }

    private fun getPlayerAttachmentPosition(player: Player): Vector {
        return player.location.toVector().add(Vector(0.0, 0.75, 0.0))
    }

    fun updatePhysics() {
        if(physics == null) return

        physics!!.attachmentPoint = if(attachedToPlayer != null) getPlayerAttachmentPosition(attachedToPlayer!!) else null
        physics!!.tick()

        updatePosition()
        updateLeash()
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
}