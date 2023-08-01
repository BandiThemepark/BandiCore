package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.switch

import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import net.minecraft.util.Brightness
import net.minecraft.world.entity.Display
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ItemDisplay

class LogFlumeStorageDoor(val location: Location) {
    lateinit var itemDisplay: PacketItemDisplay
    var spawned = false

    fun spawn() {
        itemDisplay = PacketItemDisplay()
        itemDisplay.spawn(location)
        itemDisplay.setItemStack(ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(16).build())
        itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)
        itemDisplay.setInterpolationDuration(2)
        itemDisplay.setInterpolationDelay(-1)
        (itemDisplay.handle as Display.ItemDisplay).brightnessOverride = Brightness(6, 12)
        itemDisplay.updateMetadata()

        spawned = true
        close()
    }

    fun open() {
        opening = true

        if(currentTick != OPEN_TIME_TICKS) {
            currentTick = OPEN_TIME_TICKS - currentTick
        } else {
            currentTick = 0
        }
    }

    fun close() {
        opening = false

        if(currentTick != OPEN_TIME_TICKS) {
            currentTick = OPEN_TIME_TICKS - currentTick
        } else {
            currentTick = 0
        }
    }

    val OPEN_TIME_TICKS = 60
    val OPEN_HEIGHT = 4.0
    var currentTick = 0
    var opening = false

    fun onTick() {
        if(currentTick < OPEN_TIME_TICKS) {
            currentTick++
            updatePosition()
        }
    }

    private fun updatePosition() {
        if(!spawned) return

        itemDisplay.setInterpolationDelay(-1)
        itemDisplay.updateMetadata()

        var progress = currentTick.toDouble() / OPEN_TIME_TICKS.toDouble()
        if(!opening) progress = 1.0 - progress

        itemDisplay.moveEntity(location.x, location.y + progress * OPEN_HEIGHT, location.z, location.yaw, 0.0F)
    }
}