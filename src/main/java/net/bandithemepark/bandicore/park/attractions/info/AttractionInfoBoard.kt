package net.bandithemepark.bandicore.park.attractions.info

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.audioserver.AudioCommand
import net.bandithemepark.bandicore.park.attractions.Attraction
import net.bandithemepark.bandicore.park.attractions.ridecounter.RideCounterMenu
import net.bandithemepark.bandicore.park.modsupport.SmoothCoastersChecker.Companion.usingSmoothCoasters
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.Util.getText
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class AttractionInfoBoard(val attraction: Attraction): InventoryHolder {
    var lastInventory = Bukkit.createInventory(this, 18, Util.color("<#FFFFFF>\uE002\uE013"))
    fun open(player: Player) {
        val inv = Bukkit.createInventory(this, 18, Util.color("<#FFFFFF>\uE002\uE013"))
        lastInventory = inv

        val audioServerItem = ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>AudioClient")).setCustomModelData(1020).build()
        inv.setItem(1, audioServerItem)
        inv.setItem(2, audioServerItem)
        inv.setItem(10, audioServerItem)
        inv.setItem(11, audioServerItem)

        val ridecounterItem = ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>View Ridecounters")).setCustomModelData(1020).build()
        inv.setItem(3, ridecounterItem)
        inv.setItem(4, ridecounterItem)
        inv.setItem(5, ridecounterItem)
        inv.setItem(12, ridecounterItem)
        inv.setItem(13, ridecounterItem)
        inv.setItem(14, ridecounterItem)

        val smoothCoastersItem = ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>SmoothCoasters Mod")).setCustomModelData(1020).build()
        inv.setItem(6, smoothCoastersItem)
        inv.setItem(7, smoothCoastersItem)
        inv.setItem(15, smoothCoastersItem)
        inv.setItem(16, smoothCoastersItem)

        player.openInventory(inv)
    }

    class Events: Listener {
        @EventHandler (priority = EventPriority.LOW)
        fun onArmorStandInteract(event: PlayerInteractAtEntityEvent) {
            if(event.rightClicked !is ArmorStand) return
            if(event.rightClicked.customName() == null) return
            if(event.rightClicked.customName()?.getText() == null) return
            if(!event.rightClicked.customName()?.getText()!!.startsWith("attraction_info_")) return

            val attractionId = event.rightClicked.customName()?.getText()!!.replace("attraction_info_", "")
            val attraction = Attraction.get(attractionId) ?: return
            AttractionInfoBoard(attraction).open(event.player)
        }

        @EventHandler
        fun onInventoryClick(event: InventoryClickEvent) {
            if(event.clickedInventory?.holder !is AttractionInfoBoard) return
            val attraction = event.clickedInventory!!.holder as AttractionInfoBoard
            event.isCancelled = true

            if(event.slot == 1 || event.slot == 2 || event.slot == 10 || event.slot == 11) {
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { event.whoClicked.closeInventory() })
                AudioCommand.sendMessage(event.whoClicked as Player)
            }

            if(event.slot == 3 || event.slot == 4 || event.slot == 5 || event.slot == 12 || event.slot == 13 || event.slot == 14) {
                val rideCounterMenu = RideCounterMenu(attraction.attraction)
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { rideCounterMenu.open(event.whoClicked as Player) })
            }

            if(event.slot == 6 || event.slot == 7 || event.slot == 15 || event.slot == 16) {
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { event.whoClicked.closeInventory() })
                if((event.whoClicked as Player).usingSmoothCoasters()) {
                    (event.whoClicked as Player).sendTranslatedMessage("smoothcoasters-using", BandiColors.YELLOW.toString())
                } else {
                    (event.whoClicked as Player).sendTranslatedMessage("smoothcoasters-not-using", BandiColors.YELLOW.toString(),
                        MessageReplacement("linkstart", "<u><click:open_url:'https://www.bandithemepark.net/smoothcoasters/'>"),
                        MessageReplacement("linkend", "</click></u>")
                    )
                }
            }
        }
    }

    override fun getInventory(): Inventory {
        return lastInventory
    }
}