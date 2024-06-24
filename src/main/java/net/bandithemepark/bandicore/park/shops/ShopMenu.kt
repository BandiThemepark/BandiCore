package net.bandithemepark.bandicore.park.shops

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.cosmetics.Cosmetic
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.debug.Testable
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class ShopMenu(val player: Player, val shop: Shop): InventoryHolder {
    var lastInventory = Bukkit.createInventory(this, 54, Util.color("<#FFFFFF>\uE002\uE034"))

    init {
        open()
    }

    fun open() {
        val inv = Bukkit.createInventory(this, 54, Util.color("<#FFFFFF>\uE002\uE034"))
        lastInventory = inv

        val cosmetics = shop.cosmetics.sortedBy { it.displayName }

        val availableSlots = mutableListOf(1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 32, 33, 34, 35, 36, 37, 38)

        for((index, cosmetic) in cosmetics.withIndex()) {
            val slot = availableSlots[index]
            val item = getShopItemStack(cosmetic)
            inv.setItem(slot, item)
        }

        player.openInventory(inv)
    }

    private fun getShopItemStack(cosmetic: Cosmetic): ItemStack {
        val itemFactory = ItemFactory(cosmetic.type.getDressingRoomItem(player, null, cosmetic))
        itemFactory.setKeyInPersistentStorage("cosmetic", cosmetic.id.toString())
        itemFactory.setLore(cosmetic.getShopDescription(player))
        return itemFactory.build()
    }

    override fun getInventory(): Inventory {
        return lastInventory
    }

    class Test: Testable {
        override fun test(sender: CommandSender) {
            if(sender !is Player) return
            ShopMenu(sender, BandiCore.instance.shopManager.shops[0])
        }
    }
}