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
    val playerYaw: Double = -54.5
    val cameraPosition: Vector = Vector(70.25, 2.2, -148.2)
    val cameraYaw: Double = 109.6
    val cameraPitch: Double = 0.0
    val interfacePosition: Vector = Vector(68.0, 1.5, -147.6)
    val interfaceYaw: Double = -105.0

    fun spawnDecorations() {
        spawnDecoration(ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(17).build(), Location(Bukkit.getWorld("world")!!, 66.0, 1.5,-153.0, -45f, 0f))
    }

    private fun spawnDecoration(itemStack: ItemStack, location: Location) {
        val itemDisplay = PacketItemDisplay()
        itemDisplay.spawn(location)
        itemDisplay.setItemStack(itemStack)
        itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD)
        itemDisplay.updateMetadata()
    }
}