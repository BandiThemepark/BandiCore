package net.bandithemepark.bandicore.park.attractions.info

import net.bandithemepark.bandicore.BandiCore
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

        inv.setItem(10, ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>AudioServer")).setCustomModelData(1020).build())
        inv.setItem(12, ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Teleport party")).setCustomModelData(1020).build())
        inv.setItem(14, ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>View ridecounters")).setCustomModelData(1020).build())
        inv.setItem(16, ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>SmoothCoasters")).setCustomModelData(1020).build())

        player.openInventory(inv)
    }

    class Events: Listener {
        @EventHandler
        fun onArmorStandInteract(event: PlayerInteractAtEntityEvent) {
            if(event.rightClicked is ArmorStand && event.rightClicked.customName()?.getText()!!.startsWith("attraction_info_")) {
                val attractionId = event.rightClicked.customName()?.getText()!!.replace("attraction_info_", "")
                val attraction = Attraction.get(attractionId) ?: return

                AttractionInfoBoard(attraction).open(event.player)
            }
        }

        @EventHandler
        fun onInventoryClick(event: InventoryClickEvent) {
            if(event.clickedInventory?.holder !is AttractionInfoBoard) return
            val attraction = event.clickedInventory!!.holder as AttractionInfoBoard
            event.isCancelled = true

            when(event.slot) {
                10 -> {
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { event.whoClicked.closeInventory() })
                    // TODO Send player AudioServer connect message
                }
                12 -> {
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { event.whoClicked.closeInventory() })
                    // TODO Warp all friends when party system is added
                }
                14 -> {
                    val rideCounterMenu = RideCounterMenu(attraction.attraction)
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { rideCounterMenu.open(event.whoClicked as Player) })
                }
                16 -> {
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
    }

    override fun getInventory(): Inventory {
        return lastInventory
    }
}