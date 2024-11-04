package net.bandithemepark.bandicore.server.essentials

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.menu.AttractionMenu
import net.bandithemepark.bandicore.park.cosmetics.dressingroom.DressingRoomSession
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.ItemFactory.Companion.getPersistentData
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.EquipmentSlot

class JoinItems: Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.inventory.setItem(3, ItemFactory(Material.ENDER_CHEST)
            .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${event.player.getTranslatedMessage("cosmetics")}"))
            .setKeyInPersistentStorage("join_item", "cosmetics")
            .build())

        event.player.inventory.setItem(4, ItemFactory(Material.NETHER_STAR)
            .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>BandiThemepark"))
            .setKeyInPersistentStorage("join_item", "bandithemepark")
            .build())

        event.player.inventory.setItem(5, ItemFactory(Material.MINECART)
            .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${event.player.getTranslatedMessage("attractions")}"))
            .setKeyInPersistentStorage("join_item", "attractions")
            .build())
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if(event.hand != EquipmentSlot.HAND) return
        if(event.item == null) return
        if(!event.item!!.hasItemMeta()) return

        val persistentData = event.item!!.getPersistentData("join_item") ?: return
        event.isCancelled = true

        when(persistentData) {
            "cosmetics" -> {
                DressingRoomSession(event.player, BandiCore.instance.cosmeticManager.dressingRoom)
            }
            "bandithemepark" -> {
                // Open BandiThemepark menu
            }
            "attractions" -> {
                AttractionMenu(event.player)
            }
        }
    }
}