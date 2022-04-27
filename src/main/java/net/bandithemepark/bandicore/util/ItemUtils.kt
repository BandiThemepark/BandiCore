package net.bandithemepark.bandicore.util

import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object ItemUtils {
    fun getPlayerHead(owner: OfflinePlayer, customModelData: Int = 0): ItemStack {
        val itemStack = ItemStack(Material.PLAYER_HEAD)
        val itemMeta = itemStack.itemMeta as SkullMeta
        itemMeta.owningPlayer = owner
        if(customModelData != 0) itemMeta.setCustomModelData(customModelData)
        itemStack.itemMeta = itemMeta
        return itemStack
    }
}