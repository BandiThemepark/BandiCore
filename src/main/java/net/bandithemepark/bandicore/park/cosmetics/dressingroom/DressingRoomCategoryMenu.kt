package net.bandithemepark.bandicore.park.cosmetics.dressingroom

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.cosmetics.CosmeticManager.Companion.getOwnedCosmetics
import net.bandithemepark.bandicore.park.cosmetics.CosmeticType
import net.bandithemepark.bandicore.server.achievements.menu.AchievementCategoriesMenu
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
import java.util.*

class DressingRoomCategoryMenu(val player: Player, val category: CosmeticType): InventoryHolder {
    var currentPage = 0
    var canNextPage = false
    var canPreviousPage = false
    var lastInventory = Bukkit.createInventory(this, 54, Util.color("<#FFFFFF>\uE002\uE023"))

    fun open(page: Int) {
        currentPage = page
        val inv = Bukkit.createInventory(this, 54, Util.color("<#FFFFFF>\uE002\uE023"))
        lastInventory = inv

        val cosmetics = player.getOwnedCosmetics()!!.ownedCosmetics.filter { it.cosmetic.type.id === category.id }
        cosmetics.sortedBy { it.cosmetic.displayName }

        val availableSlots = mutableListOf(1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 32, 33, 34, 35, 36, 37, 38)

        val toGetStart = page * availableSlots.size
        var toGetEnd = toGetStart + availableSlots.size
        if (toGetEnd > cosmetics.size) {
            toGetEnd = cosmetics.size
        }

        for ((i2, i) in (toGetStart until toGetEnd).withIndex()) {
            val type = cosmetics[i]
            val slot = availableSlots[i2]
            val item = type.cosmetic.type.getDressingRoomItem(player, type.color, type.cosmetic)
            item.setPersistentData("cosmetic", type.cosmetic.id.toString())
            inv.setItem(slot, item)
        }

        val pageCount = (cosmetics.size / availableSlots.size) + 1

        canNextPage = page < pageCount - 1
        canPreviousPage = page > 0

        val previousPageModelData = 1004
        val nextPageModelData = if (canNextPage) 1002 else 1003

        inv.setItem(
            37,
            ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${if(canPreviousPage) "Previous page" else "Back"}"))
                .setCustomModelData(previousPageModelData).build()
        )

        inv.setItem(42,
            ItemFactory(Material.BARRIER).setDisplayName(Util.color("<!i><${BandiColors.RED}>Unequip")).build()
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
            if (event.clickedInventory?.holder !is DressingRoomCategoryMenu) return
            event.isCancelled = true

            val session = (event.clickedInventory!!.holder as DressingRoomCategoryMenu)
            when(event.slot) {
                37 -> {
                    if(session.currentPage == 0) {
                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {  DressingRoomMenu().open(event.whoClicked as Player) })
                        return
                    }

                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        session.open(session.currentPage - 1)
                    })

                    session.player.playSound(Sound.sound(Key.key("item.book.page_turn"), Sound.Source.MASTER, 10F, 1F))
                }

                42 -> {
                    val dressingRoomSession = DressingRoomSession.activeSessions.find { it.player == event.whoClicked } ?: return
                    dressingRoomSession.unEquip(session.category.id)

                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        event.whoClicked.closeInventory()
                    })
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

                    val uuidString = event.clickedInventory!!.getItem(event.slot)!!.getPersistentData("cosmetic") ?: return
                    val uuid = UUID.fromString(uuidString)
                    val cosmetic = (event.whoClicked as Player).getOwnedCosmetics()!!.ownedCosmetics.find { it.cosmetic.id == uuid } ?: return

                    if(cosmetic.cosmetic.type.isColorable()) {
                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                            DressingRoomColorMenu(event.whoClicked as Player, cosmetic).open()
                        })
                    } else {
                        val dressingRoomSession = DressingRoomSession.activeSessions.find { it.player == event.whoClicked } ?: return
                        dressingRoomSession.equipCosmetic(cosmetic)

                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                            event.whoClicked.closeInventory()
                        })
                    }
                }
            }
        }
    }

    override fun getInventory(): Inventory {
        return lastInventory
    }
}