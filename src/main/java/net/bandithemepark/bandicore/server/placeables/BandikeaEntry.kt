package net.bandithemepark.bandicore.server.placeables

import org.bukkit.inventory.ItemStack

abstract class BandikeaEntry(val id: String, val name: String) {
    abstract fun getBandikeaItemStack(): ItemStack
}