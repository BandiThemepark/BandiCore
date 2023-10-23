package net.bandithemepark.bandicore.server.minigames.cooking.placeable

import net.bandithemepark.bandicore.server.minigames.cooking.CookingPlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItemHolder
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.entity.display.PacketItemDisplay
import org.bukkit.Location
import org.bukkit.inventory.ItemStack

abstract class CookingPlaceableHolder(location: Location, model: ItemStack): CookingPlaceable(location, model), CookingItemHolder {
    override var currentItem: CookingItem? = null
    override fun onItemChange(item: CookingItem?) {
        if(currentItem == null && item != null) {
            spawnDisplay(item.itemStack)
        } else if(item == null) {
            deSpawnDisplay()
        } else {
            updateDisplay(item.itemStack)
        }
    }

    private var itemDisplay: PacketItemDisplay? = null
    private fun spawnDisplay(itemStack: ItemStack) {
        itemDisplay = PacketItemDisplay()
        itemDisplay!!.visibilityType = PacketEntity.VisibilityType.WHITELIST
        itemDisplay!!.visibilityList = displayEntity!!.visibilityList.toMutableList()
        itemDisplay!!.spawn(location.clone().add(0.0, 1.5, 0.0))
        itemDisplay!!.setItemStack(itemStack)
        itemDisplay!!.updateMetadata()
    }

    private fun deSpawnDisplay() {
        itemDisplay?.deSpawn()
        itemDisplay = null
    }

    private fun updateDisplay(itemStack: ItemStack) {
        itemDisplay?.setItemStack(itemStack)
        itemDisplay?.updateMetadata()
    }

    override fun onRightClick(player: CookingPlayer) {
        if(player.currentItem != null && currentItem == null) {
            if(!canPlace(player.currentItem, player)) return
        } else if(player.currentItem == null && currentItem != null) {
            if(!canTake()) return
        } else {
            if(!canPlace(player.currentItem, player) || !canTake()) return
        }

        val action: Action = if(player.currentItem != null && currentItem == null) {
            setItem(player.currentItem)
            player.setItem(null)
            Action.PLACE
        } else if (player.currentItem == null && currentItem != null) {
            player.setItem(currentItem)
            setItem(null)
            Action.TAKE
        } else {
            val oldItem = currentItem
            setItem(player.currentItem)
            player.setItem(oldItem)
            Action.SWAP
        }

        onInteract(player, currentItem, action)
    }

    override fun remove() {
        super.remove()
        deSpawnDisplay()
    }

    abstract fun canPlace(item: CookingItem?, player: CookingPlayer): Boolean
    abstract fun canTake(): Boolean
    abstract fun onInteract(player: CookingPlayer, item: CookingItem?, action: Action)

    enum class Action {
        PLACE, TAKE, SWAP
    }
}