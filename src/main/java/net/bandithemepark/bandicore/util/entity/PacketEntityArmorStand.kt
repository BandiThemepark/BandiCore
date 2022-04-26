package net.bandithemepark.bandicore.util.entity

import net.bandithemepark.bandicore.util.math.MathUtil
import net.bandithemepark.bandicore.util.math.Quaternion
import net.minecraft.core.Rotations
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.util.Vector

class PacketEntityArmorStand: PacketEntity() {
    override fun getInstance(world: ServerLevel, x: Double, y: Double, z: Double): LivingEntity {
        return ArmorStand(world, x, y, z)
    }

    /**
     * Makes the arms of this armor stand visible
     */
    fun setArmsVisible() {
        (this.handle as ArmorStand).isShowArms = true
        this.updateMetadata()
    }

    /**
     * Sets the left arm pose of this armor stand
     * @param x X rotation
     * @param y Y rotation
     * @param z Z rotation
     */
    fun setLeftArmPose(x: Double, y: Double, z: Double) {
        (this.handle as ArmorStand).setLeftArmPose(Rotations(x.toFloat(), y.toFloat(), z.toFloat()))
        this.updateMetadata()
    }

    /**
     * Sets the right arm pose of this armor stand
     * @param x X rotation
     * @param y Y rotation
     * @param z Z rotation
     */
    fun setRightArmPose(x: Double, y: Double, z: Double) {
        (this.handle as ArmorStand).setRightArmPose(Rotations(x.toFloat(), y.toFloat(), z.toFloat()))
        this.updateMetadata()
    }

    /**
     * Sets the head pose of this armor stand
     * @param x X rotation
     * @param y Y rotation
     * @param z Z rotation
     */
    fun setHeadPose(x: Double, y: Double, z: Double) {
        (this.handle as ArmorStand).setHeadPose(Rotations(x.toFloat(), y.toFloat(), z.toFloat()))
        this.updateMetadata()
    }

    /**
     * Moves the armor stand and applies a certain rotation to its head
     * @param position The position to move to as a Bukkit Vector
     * @param rotation The rotation to apply as a Quaternion
     */
    fun moveWithHead(position: Vector, rotation: Quaternion) {
        val armorStandPose = MathUtil.getArmorStandPose(rotation)
        setHeadPose(Math.toDegrees(armorStandPose.x), Math.toDegrees(armorStandPose.y), Math.toDegrees(armorStandPose.z))
        moveEntity(position.x, position.y, position.z)
    }
}