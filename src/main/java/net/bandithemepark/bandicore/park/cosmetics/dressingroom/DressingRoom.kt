package net.bandithemepark.bandicore.park.cosmetics.dressingroom

import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ItemDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class DressingRoom {
    val world = Bukkit.getWorld("world")!!
    val playerPosition: Vector = Vector(68.5, 0.0, -150.5)
    val playerYaw: Double = -45.0
    val cameraPosition: Vector = Vector(70.5, 1.4, -148.5)
    val cameraYaw: Double = 135.0
    val cameraPitch: Double = -8.0

    fun spawnDecorations() {
        spawnDecoration(ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(17).build(), Location(Bukkit.getWorld("world")!!, 70.5, 1.5,-153.0, 0f, 0f))
    }

    private fun spawnDecoration(itemStack: ItemStack, location: Location) {
        val itemDisplay = PacketItemDisplay()
        itemDisplay.spawn(location)
        itemDisplay.setItemStack(itemStack)
        itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)
        itemDisplay.updateMetadata()
    }
}