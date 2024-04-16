package net.bandithemepark.bandicore.util.entity.display

import com.mojang.math.Transformation
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Display.BlockDisplay
import net.minecraft.world.entity.Display.ItemDisplay
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemDisplayContext
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.joml.Matrix4f

class PacketBlockDisplay: PacketEntity() {
    override fun getInstance(world: ServerLevel, x: Double, y: Double, z: Double): Entity {
        return BlockDisplay(EntityType.BLOCK_DISPLAY, world)
    }

    fun setTransformationMatrix(transformationMatrix: Matrix4f?) {
        (handle as BlockDisplay).setTransformation(Transformation(transformationMatrix))
    }

    fun setInterpolationDuration(duration: Int) {
        (handle as BlockDisplay).interpolationDuration = duration
    }

    fun setInterpolationDelay(ticks: Int) {
        (handle as BlockDisplay).interpolationDelay = ticks
    }

    override fun updateMetadata() {
        for(player in getPlayersVisibleFor()) {
            val packet = ClientboundSetEntityDataPacket(handle.id, handle.entityData.nonDefaultValues!!)
            (player as CraftPlayer).handle.connection.send(packet)
        }
    }
}