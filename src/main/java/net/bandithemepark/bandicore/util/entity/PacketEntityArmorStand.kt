package net.bandithemepark.bandicore.util.entity

import net.minecraft.core.Rotations
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand

class PacketEntityArmorStand: PacketEntity() {
    override fun getInstance(world: ServerLevel, x: Double, y: Double, z: Double): LivingEntity {
        return ArmorStand(world, x, y, z)
    }

    fun setArmsVisible() {
        (this.handle as ArmorStand).isShowArms = true
        this.updateMetadata()
    }

    fun setLeftArmPose(x: Double, y: Double, z: Double) {
        (this.handle as ArmorStand).setLeftArmPose(Rotations(x.toFloat(), y.toFloat(), z.toFloat()))
        this.updateMetadata()
    }

    fun setRightArmPose(x: Double, y: Double, z: Double) {
        (this.handle as ArmorStand).setRightArmPose(Rotations(x.toFloat(), y.toFloat(), z.toFloat()))
        this.updateMetadata()
    }

    fun setHeadPose(x: Double, y: Double, z: Double) {
        (this.handle as ArmorStand).setHeadPose(Rotations(x.toFloat(), y.toFloat(), z.toFloat()))
        this.updateMetadata()
    }
}