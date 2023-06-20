package net.bandithemepark.bandicore.util.entity.armorstand

import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.math.MathUtil
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import net.minecraft.core.Rotations
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.util.Vector

open class PacketEntityArmorStand: PacketEntity() {
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

    override fun updateMetadata() {
        for(player in getPlayersVisibleFor()) {
            if(glowingFor.contains(player)) {
                val before = (this as PacketEntity).handle!!.hasGlowingTag()
                (this as PacketEntity).handle!!.setGlowingTag(true)
                //val packet = ClientboundSetEntityDataPacket(handle!!.id, handle!!.entityData, true)
                val packet = ClientboundSetEntityDataPacket(handle!!.id, handle!!.entityData.nonDefaultValues!!)
                (player as CraftPlayer).handle.connection.send(packet)
                (this as PacketEntity).handle!!.setGlowingTag(before)
            } else {
                //val packet = ClientboundSetEntityDataPacket(handle!!.id, handle!!.entityData, true)
                val packet = ClientboundSetEntityDataPacket(handle!!.id, handle!!.entityData.nonDefaultValues!!)
                (player as CraftPlayer).handle.connection.send(packet)
            }
        }
    }

    val glowingFor = mutableListOf<Player>()
    fun startGlowFor(player: Player) {
        glowingFor.add(player)
        val before = (this as PacketEntity).handle!!.hasGlowingTag()
        (this as PacketEntity).handle!!.setGlowingTag(true)
        (this as PacketEntity).updateMetadataFor(player)
        (this as PacketEntity).handle!!.setGlowingTag(before)
    }

    fun endGlowFor(player: Player) {
        glowingFor.remove(player)
        val before = (this as PacketEntity).handle!!.hasGlowingTag()
        (this as PacketEntity).handle!!.setGlowingTag(false)
        (this as PacketEntity).updateMetadataFor(player)
        (this as PacketEntity).handle!!.setGlowingTag(before)
    }
}