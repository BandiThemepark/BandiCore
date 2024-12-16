package net.bandithemepark.bandicore.park.shops

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.menu.MainMenu
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.ItemFactory.Companion.getPersistentData
import net.bandithemepark.bandicore.util.ItemFactory.Companion.setPersistentData
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class ShopsMenu(val player: Player): InventoryHolder {
    var currentPage = 0
    var canNextPage = false
    var canPreviousPage = false
    var lastInventory = Bukkit.createInventory(this, 54, Util.color("<#FFFFFF>\uE002\uE042"))

    fun open(page: Int) {
        currentPage = page
        val inv = Bukkit.createInventory(this, 54, Util.color("<#FFFFFF>\uE002\uE042"))
        lastInventory = inv

        val visibleShops = BandiCore.instance.shopManager.shops.filter { it.warp != null }
        visibleShops.sortedBy { it.displayName }

        val availableSlots = mutableListOf(1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 32, 33, 34, 35, 36, 37, 38)

        val toGetStart = page * availableSlots.size
        var toGetEnd = toGetStart + availableSlots.size
        if (toGetEnd > visibleShops.size) {
            toGetEnd = visibleShops.size
        }

        for ((i2, i) in (toGetStart until toGetEnd).withIndex()) {
            val shop = visibleShops[i]
            val slot = availableSlots[i2]
            val item = ItemFactory(shop.icon)
                .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${shop.displayName}"))
                .setLore(mutableListOf(
                    Util.color("<!i><${BandiColors.LIGHT_GRAY}>Click to warp to this shop")
                ))
                .build()

            item.setPersistentData("warp", shop.warp!!.name)
            inv.setItem(slot, item)
        }

        val pageCount = (visibleShops.size / availableSlots.size) + 1

        canNextPage = page < pageCount - 1
        canPreviousPage = page > 0

        val previousPageModelData = 1004
        val nextPageModelData = if (canNextPage) 1002 else 1003

        inv.setItem(
            37,
            ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${if(canPreviousPage) "Previous page" else "Back"}"))
                .setCustomModelData(previousPageModelData).build()
        )

        inv.setItem(
            43,
            ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Next page"))
                .setCustomModelData(nextPageModelData).build()
        )

        player.openInventory(inv)
    }

    class Events: Listener {
        @EventHandler
        fun onInventoryClick(event: InventoryClickEvent) {
            if (event.clickedInventory?.holder !is ShopsMenu) return
            event.isCancelled = true

            val session = (event.clickedInventory!!.holder as ShopsMenu)
            when(event.slot) {
                37 -> {
                    if(session.currentPage == 0) {
                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                            MainMenu(session.player).open()
                        })
                        return
                    }

                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        session.open(session.currentPage - 1)
                    })

                    session.player.playSound(Sound.sound(Key.key("item.book.page_turn"), Sound.Source.MASTER, 10F, 1F))
                }

                43 -> {
                    if(session.canNextPage) {
                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                            session.open(session.currentPage + 1)
                        })

                        session.player.playSound(Sound.sound(Key.key("item.book.page_turn"), Sound.Source.MASTER, 10F, 1F))
                    } else {
                        session.player.playSound(Sound.sound(Key.key("entity.villager.no"), Sound.Source.MASTER, 10F, 1F))
                    }
                }

                else -> {
                    if (event.clickedInventory!!.getItem(event.slot) == null) return

                    val warpName = event.clickedInventory!!.getItem(event.slot)!!.getPersistentData("warp") ?: return

                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        event.whoClicked.closeInventory()
                        val warp = BandiCore.instance.server.warpManager.warps.find { it.name == warpName }
                        event.whoClicked.teleport(warp!!.location)
                    })
                }
            }
        }
    }

    override fun getInventory(): Inventory {
        return lastInventory
    }
}