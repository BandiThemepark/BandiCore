package net.bandithemepark.bandicore.server.menu

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.audioserver.AudioCommand
import net.bandithemepark.bandicore.park.shops.ShopsMenu
import net.bandithemepark.bandicore.server.achievements.menu.AchievementCategoriesMenu
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
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

class MainMenu(val player: Player): InventoryHolder {
    var lastInventory = Bukkit.createInventory(this, 27, Util.color("<#FFFFFF>\uE002\uE043"))

    fun open() {
        val inv = Bukkit.createInventory(this, 27, Util.color("<#FFFFFF>\uE002\uE043"))
        lastInventory = inv

        val audioServerItem = ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>AudioClient")).setLore(mutableListOf(
            Util.color("<!i><${BandiColors.LIGHT_GRAY}>Listen to music around"),
            Util.color("<!i><${BandiColors.LIGHT_GRAY}>the park and on rides")
        )).setCustomModelData(1020).build()
        inv.setItem(0, audioServerItem)
        inv.setItem(1, audioServerItem)
        inv.setItem(2, audioServerItem)
        inv.setItem(9, audioServerItem)
        inv.setItem(10, audioServerItem)
        inv.setItem(11, audioServerItem)
        inv.setItem(18, audioServerItem)
        inv.setItem(19, audioServerItem)
        inv.setItem(20, audioServerItem)

        val achievementsItem = ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Achievements")).setLore(mutableListOf(
            Util.color("<!i><${BandiColors.LIGHT_GRAY}>View your achievements"),
            Util.color("<!i><${BandiColors.LIGHT_GRAY}>and progress")
        )).setCustomModelData(1020).build()
        inv.setItem(3, achievementsItem)
        inv.setItem(4, achievementsItem)
        inv.setItem(5, achievementsItem)
        inv.setItem(12, achievementsItem)
        inv.setItem(13, achievementsItem)
        inv.setItem(14, achievementsItem)
        inv.setItem(21, achievementsItem)
        inv.setItem(22, achievementsItem)
        inv.setItem(23, achievementsItem)

        val shopsItem = ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Shops")).setLore(mutableListOf(
            Util.color("<!i><${BandiColors.LIGHT_GRAY}>View a list of all"),
            Util.color("<!i><${BandiColors.LIGHT_GRAY}>shops around the park")
        )).setCustomModelData(1020).build()
        inv.setItem(6, shopsItem)
        inv.setItem(7, shopsItem)
        inv.setItem(8, shopsItem)

        val siteItem = ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Website")).setLore(mutableListOf(
            Util.color("<!i><${BandiColors.LIGHT_GRAY}>Visit our site and"),
            Util.color("<!i><${BandiColors.LIGHT_GRAY}>our store here")
        )).setCustomModelData(1020).build()
        inv.setItem(15, siteItem)
        inv.setItem(16, siteItem)
        inv.setItem(17, siteItem)

        val discordItem = ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Discord")).setLore(mutableListOf(
            Util.color("<!i><${BandiColors.LIGHT_GRAY}>Join our Discord server"),
            Util.color("<!i><${BandiColors.LIGHT_GRAY}>for updates and more")
        )).setCustomModelData(1020).build()
        inv.setItem(24, discordItem)
        inv.setItem(25, discordItem)
        inv.setItem(26, discordItem)

        player.openInventory(inv)
    }

    class Events: Listener {
        @EventHandler
        fun onInventoryClick(event: InventoryClickEvent) {
            if (event.clickedInventory?.holder !is MainMenu) return
            event.isCancelled = true

            when(event.slot) {
                0, 1, 2, 9, 10, 11, 18, 19, 20 -> {
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        event.whoClicked.closeInventory()
                        AudioCommand.sendMessage(event.whoClicked as Player)
                    })
                }

                3, 4, 5, 12, 13, 14, 21, 22, 23 -> {
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        AchievementCategoriesMenu(event.whoClicked as Player).open(0)
                    })
                }

                6, 7, 8 -> {
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        ShopsMenu(event.whoClicked as Player).open(0)
                    })
                }

                15, 16, 17 -> {
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        event.whoClicked.closeInventory()
                        event.whoClicked.sendMessage(Util.color("<${BandiColors.YELLOW}><click:open_url:'${WEBSITE_URL}'>${(event.whoClicked as Player).getTranslatedMessage("website-link")}</click>"))
                    })
                }

                24, 25, 26 -> {
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                        event.whoClicked.closeInventory()
                        event.whoClicked.sendMessage(Util.color("<${BandiColors.YELLOW}><click:open_url:'${DISCORD_URL}'>${(event.whoClicked as Player).getTranslatedMessage("discord-link")}</click>"))
                    })
                }
            }
        }
    }

    override fun getInventory(): Inventory {
        return lastInventory
    }

    companion object {
        const val WEBSITE_URL = "https://bandithemepark.net/"
        const val DISCORD_URL = "https://discord.bandithemepark.net/"
    }
}