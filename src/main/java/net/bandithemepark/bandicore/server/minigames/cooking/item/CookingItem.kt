package net.bandithemepark.bandicore.server.minigames.cooking.item

import org.bukkit.inventory.ItemStack

abstract class CookingItem {
    abstract fun getItemStack(): ItemStack
}