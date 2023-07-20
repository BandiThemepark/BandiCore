package net.bandithemepark.bandicore.util.entity.itemdisplay

import com.mojang.math.Transformation
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Display.ItemDisplay
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemDisplayContext
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform
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
        (handle as ItemDisplay).interpolationDuration = duration
    }

    fun setInterpolationDelay(ticks: Int) {
        (handle as ItemDisplay).interpolationDelay = ticks
    }
}