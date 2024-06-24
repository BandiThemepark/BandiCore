package net.bandithemepark.bandicore.park.shops

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendCosmetic
import net.bandithemepark.bandicore.park.cosmetics.Cosmetic
import net.bandithemepark.bandicore.park.cosmetics.CosmeticManager.Companion.getOwnedCosmetics
import net.bandithemepark.bandicore.park.cosmetics.OwnedCosmetic
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.DressingRoomCategoryMenu
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.DressingRoomSession
import net.bandithemepark.bandicore.server.essentials.coins.CoinManager
import net.bandithemepark.bandicore.server.essentials.coins.CoinManager.Companion.getBalance
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.ItemFactory.Companion.getPersistentData
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.debug.Testable
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.util.*

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

    class Events: Listener {
        @EventHandler
        fun onInventoryClick(event: InventoryClickEvent) {
            // Check if it is a shop menu, if so cancel interaction
            if (event.clickedInventory?.holder !is ShopMenu) return
            event.isCancelled = true
            val session = event.clickedInventory?.holder as ShopMenu

            // Check if an item was clicked
            if (event.clickedInventory!!.getItem(event.slot) == null) return

            // Find cosmetic from ID stored in item stack
            val uuidString = event.clickedInventory!!.getItem(event.slot)!!.getPersistentData("cosmetic") ?: return
            val uuid = UUID.fromString(uuidString)
            val cosmetic = BandiCore.instance.cosmeticManager.cosmetics.find { it.id == uuid } ?: return

            // Check if player owns cosmetic
            if((event.whoClicked as Player).getOwnedCosmetics()!!.ownedCosmetics.any { it.cosmetic.id == cosmetic.id }) {
                // Player owns cosmetic, so equip it
                BandiCore.instance.cosmeticManager.equip(event.whoClicked as Player, cosmetic)
            } else {
                // Check if player did left-click or right-click, to perform appropriate action
                if(event.isLeftClick) {
                    // Check if item can be bought, and if so buy
                    if(cosmetic.canPurchase(event.whoClicked as Player)) {
                        BackendCosmetic.give(event.whoClicked as Player, cosmetic) {
                            (event.whoClicked as Player).getOwnedCosmetics()!!.ownedCosmetics.add(OwnedCosmetic(cosmetic, false, 1, null))
                        }

                        CoinManager.setLoadedBalance((event.whoClicked as Player), (event.whoClicked as Player).getBalance() - cosmetic.price)
                        CoinManager.saveBalance(event.whoClicked as Player)
                        event.whoClicked.sendTranslatedMessage("shop-purchase-success", BandiColors.YELLOW.toString(), MessageReplacement("cosmetic", cosmetic.displayName))
                    }
                } else {
                    // Open dressing room preview for cosmetic
                    DressingRoomSession(event.whoClicked as Player, BandiCore.instance.cosmeticManager.dressingRoom, cosmetic, session.shop)
                }
            }

            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                event.whoClicked.closeInventory()
            })
        }
    }

    class Test: Testable {
        override fun test(sender: CommandSender) {
            if(sender !is Player) return
            ShopMenu(sender, BandiCore.instance.shopManager.shops[0])
        }
    }
}