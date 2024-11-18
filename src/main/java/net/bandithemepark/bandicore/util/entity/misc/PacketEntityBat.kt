package net.bandithemepark.bandicore.util.entity.misc

import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ambient.Bat

class PacketEntityBat: PacketEntity() {
    override fun getInstance(world: ServerLevel, x: Double, y: Double, z: Double): Entity {
        return Bat(EntityType.BAT, world)
    }

    fun setVisible(visible: Boolean) {
        this.handle.isInvisible = !visible
        this.updateMetadata()
    }
}