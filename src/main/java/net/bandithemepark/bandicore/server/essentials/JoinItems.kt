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
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.EquipmentSlot

class JoinItems: Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        giveJoinItems(event.player)
    }

    companion object {
        fun giveJoinItems(player: Player) {
            player.inventory.setItem(3, ItemFactory(Material.PAPER)
                .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${player.getTranslatedMessage("cosmetics")}"))
                .setKeyInPersistentStorage("join_item", "cosmetics")
                .setCustomModelData(1022)
                .build())

            player.inventory.setItem(4, ItemFactory(Material.PAPER)
                .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>BandiThemepark"))
                .setKeyInPersistentStorage("join_item", "bandithemepark")
                .setCustomModelData(1023)
                .build())

            player.inventory.setItem(5, ItemFactory(Material.PAPER)
                .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${player.getTranslatedMessage("attractions")}"))
                .setKeyInPersistentStorage("join_item", "attractions")
                .setCustomModelData(1024)
                .build())
        }
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
                if(DressingRoomSession.activeSessions.find { it.player == event.player } != null) return
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