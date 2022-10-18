package net.bandithemepark.bandicore.park.attractions.menu

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.server.custom.blocks.CustomBlock
import net.bandithemepark.bandicore.server.custom.blocks.CustomBlockMenu
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.ItemFactory.Companion.getPersistentData
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

class AttractionMenu(val player: Player) {
    var currentPage = 0
    var canNextPage = false
    var canPreviousPage = false

    init {
        sessions[player] = this
        open(0)
    }

    fun open(page: Int) {
        currentPage = page
        val inv = Bukkit.createInventory(null, 54, Util.color("<#FFFFFF>\uE002\uE012"))

        val attractions = mutableListOf<Attraction>()
        val tempAttractions = mutableListOf<Attraction>()

        for(attraction in Attraction.attractions.filter { it.mode.shown }) {
            if(attraction.mode.glow) attractions.add(attraction) else tempAttractions.add(attraction)
        }

        attractions.sortBy { it.appearance.displayName }
        tempAttractions.sortBy { it.appearance.displayName }

        attractions.addAll(tempAttractions)

        val toGetStart = page * 20
        var toGetEnd = toGetStart + 20
        if(toGetEnd > attractions.size) {
            toGetEnd = attractions.size
        }

        val availableSlots = mutableListOf(1, 2, 3, 4, 5, 10, 11, 12, 13, 14, 19, 20, 21, 22, 23, 32, 33, 34, 35, 36)
        for((i2, i) in (toGetStart until toGetEnd).withIndex()) {
            val type = attractions[i]
            val slot = availableSlots[i2]
            val item = type.appearance.getItemStack(player, type)
            inv.setItem(slot, item)
        }

        val pageCount = (attractions.size / 20) + 1

        canNextPage = page < pageCount-1
        canPreviousPage = page > 0

        val previousPageModelData = if(canPreviousPage) 1004 else 1005
        val nextPageModelData = if(canNextPage) 1002 else 1003

        inv.setItem(37, ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Previous page")).setCustomModelData(previousPageModelData).build())
        inv.setItem(43, ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Next page")).setCustomModelData(nextPageModelData).build())

        player.openInventory(inv)
    }

    companion object {
        val sessions = hashMapOf<Player, AttractionMenu>()
    }

    class Events: Listener {
        @EventHandler
        fun onInventoryClick(event: InventoryClickEvent) {
            if(event.view.title() != Util.color("<#FFFFFF>\uE002\uE012")) return
            if(event.view.topInventory != event.clickedInventory) return

            event.isCancelled = true
            when(event.slot) {
                37 -> {
                    val session = sessions[event.whoClicked as Player]!!
                    if(session.canPreviousPage) {
                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                            session.open(session.currentPage-1)
                        })
                        session.player.playSound(Sound.sound(Key.key("item.book.page_turn"), Sound.Source.MASTER, 10F, 1F))
                    } else {
                        session.player.playSound(Sound.sound(Key.key("entity.villager.no"), Sound.Source.MASTER, 10F, 1F))
                    }
                }
                43 -> {
                    val session = sessions[event.whoClicked as Player]!!
                    if(session.canNextPage) {
                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                            session.open(session.currentPage+1)
                        })
                        session.player.playSound(Sound.sound(Key.key("item.book.page_turn"), Sound.Source.MASTER, 10F, 1F))
                    } else {
                        session.player.playSound(Sound.sound(Key.key("entity.villager.no"), Sound.Source.MASTER, 10F, 1F))
                    }
                }
                else -> {
                    if(event.clickedInventory!!.getItem(event.slot) == null) return

                    val attractionId = event.clickedInventory!!.getItem(event.slot)!!.getPersistentData("attraction") ?: return
                    val attraction = Attraction.attractions.find { it.id == attractionId } ?: return

                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        event.whoClicked.closeInventory()
                        val warp = BandiCore.instance.server.warpManager.warps.find { it.name == attractionId }!!
                        event.whoClicked.teleport(warp.location)
                    })
                }
            }
        }
    }
}