package net.bandithemepark.bandicore.park.cosmetics.balloons

import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import org.joml.Matrix4f

class BalloonTrailPart(val model: ItemStack, val world: World, val pieceLength: Double) {
    val displayEntity = PacketItemDisplay()

    var position = Vector()
    var rotation = Vector()

    fun spawn(position: Vector) {
        this.position = position
        displayEntity.spawn(position.toLocation(world))
        displayEntity.setItemStack(model)
        displayEntity.setInterpolationDuration(2)
        displayEntity.updateMetadata()
    }

    fun deSpawn() {
        displayEntity.deSpawn()
        playPopParticles()
    }

    private fun playPopParticles() {
        world.spawnParticle(Particle.CLOUD, position.toLocation(world).add(0.0, 0.2, 0.0), 20, 0.3, 0.3, 0.3, 0.0)
    }

    fun updatePosition() {
        displayEntity.moveEntity(position.x, position.y, position.z)

        val quaternion = Quaternion.fromYawPitchRoll(rotation.x, rotation.y, rotation.z)
        displayEntity.setTransformationMatrix(Matrix4f().rotate(quaternion.toBukkitQuaternion()))
        displayEntity.updateMetadata()
    }

}