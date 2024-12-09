package net.bandithemepark.bandicore.park.attractions.ridecounter

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.park.attractions.info.AttractionInfoBoard
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class RideCounterMenu(val attraction: Attraction): InventoryHolder {
    var lastInventory = Bukkit.createInventory(this, 54, Util.color("<#FFFFFF>\uE002\uE014"))
    fun open(player: Player) {
        val inv = Bukkit.createInventory(this, 54, Util.color("<#FFFFFF>\uE002\uE014"))
        lastInventory = inv

        val counterOf = BandiCore.instance.server.ridecounterManager.getPlayerRidecounter(player).getRidecount(attraction.id)
        val lore = if(counterOf != null) {
            mutableListOf(
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>You are at ${counterOf.count} rides. The first time you"),
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>rode this ride was ${counterOf.firstRide}."),
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>The last time was ${counterOf.lastRide}.")
            )
        } else {
            mutableListOf(Util.color("<!i><${BandiColors.LIGHT_GRAY}>You haven't ridden this ride yet."))
        }

        inv.setItem(37, ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Back")).setCustomModelData(1004).build())
        inv.setItem(38, ItemFactory(Material.WRITABLE_BOOK)
            .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Your stats"))
            .setLore(lore)
            .build())

        val availablePlayerSlots = mutableListOf(19, 13, 14, 15, 16, 22, 23, 24, 25, 31, 32, 33, 34, 40, 41, 42, 43)

        val counters = BandiCore.instance.server.ridecounterManager.getRideRidecounter(attraction.id).counters.toMutableList()
        counters.sortByDescending { it.count }
        for((counter, rideCounter) in counters.withIndex()) {
            if(availablePlayerSlots.isEmpty()) break

            val slot = availablePlayerSlots.removeFirst()
            val customModelData = if(slot == 19) 9 else 10

            val item = ItemFactory(Material.PLAYER_HEAD)
                .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>#${counter+1} ${rideCounter.player.name}"))
                .setCustomModelData(customModelData)
                .setLore(mutableListOf(Util.color("<!i><${BandiColors.LIGHT_GRAY}>With ${rideCounter.count} ride${if(rideCounter.count == 1) "" else "s"}")))
                .setSkullOwner(rideCounter.player)
                .build()

            inv.setItem(slot, item)

            if(slot == 19) {
                val dataItem = ItemFactory(Material.PAPER)
                    .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>#${counter+1} ${rideCounter.player.name}"))
                    .setCustomModelData(1020)
                    .setLore(mutableListOf(Util.color("<!i><${BandiColors.LIGHT_GRAY}>With ${rideCounter.count} ride${if(rideCounter.count == 1) "" else "s"}")))
                    .build()

                inv.setItem(20, dataItem)
                inv.setItem(28, dataItem)
                inv.setItem(29, dataItem)
            }
        }

        player.openInventory(inv)
    }

    class Events: Listener {
        @EventHandler
        fun onInventoryClick(event: InventoryClickEvent) {
            if(event.clickedInventory?.holder !is RideCounterMenu) return
            val menu = event.clickedInventory!!.holder as RideCounterMenu
            event.isCancelled = true

            when(event.slot) {
                37 -> {
                    val infoBoard = AttractionInfoBoard(menu.attraction)
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { infoBoard.open(event.whoClicked as Player) })
                }
            }
        }
    }

    override fun getInventory(): Inventory {
        return lastInventory
    }
}