package net.bandithemepark.bandicore.server.minigames.cooking.placeable.types

import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.CookingPlaceable
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.joml.Matrix4f

class CookingPlaceableDispenser(location: Location, model: ItemStack, val item: CookingItem): CookingPlaceable(location, model) {
    override fun onRightClick(player: CookingPlayer) {
        if(player.currentItem != null) return
        player.setItem(item)
    }

    override fun onLeftClick(player: CookingPlayer) {

    }

    lateinit var itemDisplay: PacketItemDisplay
    override fun place(players: List<CookingPlayer>) {
        super.place(players)

        itemDisplay = PacketItemDisplay()
        itemDisplay.visibilityType = PacketEntity.VisibilityType.WHITELIST
        itemDisplay.visibilityList = displayEntity!!.visibilityList.toMutableList()
        itemDisplay.spawn(location.clone().add(0.0, 0.5, 0.0))
        itemDisplay.setItemStack(item.itemStack)
        itemDisplay.setTransformationMatrix(Matrix4f().rotateXYZ(0.0f, Math.toRadians(location.yaw.toDouble()).toFloat(), 0.0f).translation(0.0f, 0.0f, 0.5f).scale(0.5f))
        itemDisplay.updateMetadata()
    }

    override fun remove() {
        super.remove()

        itemDisplay.deSpawn()
    }
}