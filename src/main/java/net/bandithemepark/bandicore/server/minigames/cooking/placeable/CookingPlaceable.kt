package net.bandithemepark.bandicore.server.minigames.cooking.placeable

import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

abstract class CookingPlaceable(val location: Location, val model: ItemStack) {

    var displayEntity: PacketItemDisplay? = null
    open fun place(players: List<CookingPlayer>) {
        location.block.type = Material.BARRIER
        displayEntity = PacketItemDisplay()
        displayEntity!!.visibilityType = PacketEntity.VisibilityType.WHITELIST
        displayEntity!!.visibilityList = players.toMutableList()
        displayEntity!!.spawn(location.clone().add(0.0, 0.5, 0.0))
        displayEntity!!.setItemStack(model)
        displayEntity!!.updateMetadata()
    }

    open fun remove() {
        displayEntity?.deSpawn()
        displayEntity = null
    }

    abstract fun onRightClick(player: CookingPlayer)
    abstract fun onLeftClick(player: CookingPlayer)

    fun showGlow(player: CookingPlayer) {
        displayEntity!!.startGlowFor(player)
    }

    fun hideGlow(player: CookingPlayer) {
        displayEntity!!.endGlowFor(player)
    }
}