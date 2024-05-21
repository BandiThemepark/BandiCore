package net.bandithemepark.bandicore.util.entity.display

import com.mojang.math.Transformation
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Display.ItemDisplay
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemDisplayContext
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.joml.Matrix4f

class PacketItemDisplay: PacketEntity() {
    override fun getInstance(world: ServerLevel, x: Double, y: Double, z: Double): Entity {
        return ItemDisplay(EntityType.ITEM_DISPLAY, world)
    }

    fun setItemDisplayTransform(display: ItemDisplayTransform?) {
        (handle as ItemDisplay).itemTransform = ItemDisplayContext.BY_ID.apply(display!!.ordinal)
    }

    fun setItemStack(item: ItemStack?) {
        (handle as ItemDisplay).itemStack = CraftItemStack.asNMSCopy(item)
    }

    fun setTransformationMatrix(transformationMatrix: Matrix4f?) {
        (handle as ItemDisplay).setTransformation(Transformation(transformationMatrix))
    }

    fun setInterpolationDuration(duration: Int) {
        (handle as ItemDisplay).transformationInterpolationDuration = duration
        (handle as ItemDisplay).entityData.set(Display.DATA_POS_ROT_INTERPOLATION_DURATION_ID, duration)
    }

    fun setInterpolationDelay(ticks: Int) {
        (handle as ItemDisplay).transformationInterpolationDelay = ticks
    }

    override fun updateMetadata() {
        for(player in getPlayersVisibleFor()) {
            if(glowingFor.contains(player)) {
                val before = (this as PacketEntity).handle!!.hasGlowingTag()
                (this as PacketEntity).handle!!.setGlowingTag(true)

                val packet = ClientboundSetEntityDataPacket(handle!!.id, handle!!.entityData.nonDefaultValues!!)
                (player as CraftPlayer).handle.connection.send(packet)

                (this as PacketEntity).handle!!.setGlowingTag(before)
            } else {
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