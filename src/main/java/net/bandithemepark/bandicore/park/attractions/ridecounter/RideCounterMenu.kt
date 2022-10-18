package net.bandithemepark.bandicore.park.attractions.ridecounter

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.park.attractions.info.AttractionInfoBoard
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
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

        inv.setItem(0, ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Back")).setCustomModelData(1004).build())

        val availablePlayerSlots = mutableListOf(13, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44)
        // TODO Add players when ridecounter has been implemented

        player.openInventory(inv)
    }

    class Events: Listener {
        @EventHandler
        fun onInventoryClick(event: InventoryClickEvent) {
            if(event.clickedInventory?.holder !is RideCounterMenu) return
            val menu = event.clickedInventory!!.holder as RideCounterMenu
            event.isCancelled = true

            when(event.slot) {
                0 -> {
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