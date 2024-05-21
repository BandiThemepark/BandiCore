package net.bandithemepark.bandicore.park.cosmetics.balloons

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.debug.Testable
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class Balloon(val model: ItemStack, attachmentPoint: Vector, val world: World): Testable {
    var velocity = Vector(0, 0, 0)
    var position = Vector(0, 0, 0)
    var attachmentPoint: Vector? = null

    val displayEntity = PacketItemDisplay()

    init {
        this.attachmentPoint = attachmentPoint
    }

    private fun spawn() {
        velocity = Vector(0, 0, 0)
        popTime = 0

        spawnedBalloons.add(this)
        position = attachmentPoint!!.clone().add(Vector(0.0, SPAWN_HEIGHT, 0.0))

        displayEntity.spawn(position.toLocation(world))
        displayEntity.setItemStack(model)
        displayEntity.setInterpolationDuration(2)
        displayEntity.updateMetadata()
    }

    private fun deSpawn() {
        displayEntity.deSpawn()
        spawnedBalloons.remove(this)
    }

    private fun playPopParticles() {
        world.spawnParticle(Particle.CLOUD, position.toLocation(world).add(0.0, 0.2, 0.0), 20, 0.3, 0.3, 0.3, 0.0)
    }

    fun updatePhysics() {
        if(attachmentPoint == null) {
            updateFlyAwayPhysics()
        } else {

        }

        updatePosition()
    }

    var popTime = 0
    private fun updateFlyAwayPhysics() {
        if(popTime > POP_TIME_TICKS) {
            deSpawn()
            return
        }
        popTime++

        if(POP_TIME_TICKS - POP_PARTICLES_OFFSET_TICKS == popTime) {
            playPopParticles()
        }

        velocity = Vector(-0.03, 0.1, 0.0)
    }

    private fun updatePosition() {
        position.add(velocity)
        displayEntity.moveEntity(position.x, position.y, position.z)
    }

    companion object {
        const val MAX_ROPE_LENGTH = 3.0
        const val POP_TIME_TICKS = 200
        const val SPAWN_HEIGHT = 2.0
        const val POP_PARTICLES_OFFSET_TICKS = 5

        val spawnedBalloons = mutableListOf<Balloon>()

        fun startTimer() {
            Bukkit.getScheduler().runTaskTimerAsynchronously(BandiCore.instance, Runnable {
                spawnedBalloons.toList().forEach { it.updatePhysics() }
            }, 1, 0)
        }
    }

    override fun test(sender: CommandSender) {
        attachmentPoint = Vector(-89.5, 29.0, -125.5)
        spawn()
        attachmentPoint = null
    }
}