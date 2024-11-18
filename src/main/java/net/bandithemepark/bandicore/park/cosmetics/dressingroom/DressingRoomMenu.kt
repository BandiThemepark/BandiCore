package net.bandithemepark.bandicore.park.cosmetics.dressingroom

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.cosmetics.CosmeticType
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

class DressingRoomMenu: InventoryHolder {
    var lastInventory = Bukkit.createInventory(this, 36, Util.color("<#FFFFFF>\uE002\uE022"))

    fun open(player: Player) {
        val inv = Bukkit.createInventory(this, 36, Util.color("<#FFFFFF>\uE002\uE022"))
        lastInventory = inv

        val hatsItem = ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Hats")).setLore(mutableListOf(
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>Choose a stylish piece to hide"),
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>your bald spot!")
            )).setCustomModelData(1020).build()
        inv.setItem(0, hatsItem)
        inv.setItem(1, hatsItem)
        inv.setItem(2, hatsItem)
        inv.setItem(9, hatsItem)
        inv.setItem(10, hatsItem)
        inv.setItem(11, hatsItem)

        val handheldsItem = ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Handhelds")).setLore(mutableListOf(
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>Choose an item to hold"),
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>in your offhand!")
            )).setCustomModelData(1020).build()
        inv.setItem(3, handheldsItem)
        inv.setItem(4, handheldsItem)
        inv.setItem(12, handheldsItem)
        inv.setItem(13, handheldsItem)

        val balloonsItem = ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Balloons")).setLore(mutableListOf(
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>Choose a balloon to hold"),
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>in your offhand!")
            )).setCustomModelData(1020).build()
        inv.setItem(5, balloonsItem)
        inv.setItem(6, balloonsItem)
        inv.setItem(14, balloonsItem)
        inv.setItem(15, balloonsItem)

        val titlesItem = ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Titles")).setLore(mutableListOf(
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>Choose a title to display"),
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>above your head!")
            )).setCustomModelData(1020).build()
        inv.setItem(7, titlesItem)
        inv.setItem(8, titlesItem)
        inv.setItem(16, titlesItem)
        inv.setItem(17, titlesItem)

        val bootsItem = ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Boots")).setLore(mutableListOf(
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>Choose a stylish pair to"),
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>protect your feet!")
            )).setCustomModelData(1020).build()
        inv.setItem(18, bootsItem)
        inv.setItem(27, bootsItem)
        inv.setItem(19, bootsItem)
        inv.setItem(28, bootsItem)

        val leggingsItem = ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Leggings")).setLore(mutableListOf(
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>Choose a stylish pair to"),
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>protect your legs!")
            )).setCustomModelData(1020).build()
        inv.setItem(20, leggingsItem)
        inv.setItem(21, leggingsItem)
        inv.setItem(29, leggingsItem)
        inv.setItem(30, leggingsItem)

        val chestplateItem = ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>Chestplates")).setLore(mutableListOf(
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>Choose a stylish piece to"),
                Util.color("<!i><${BandiColors.LIGHT_GRAY}>protect your chest!")
            )).setCustomModelData(1020).build()
        inv.setItem(22, chestplateItem)
        inv.setItem(23, chestplateItem)
        inv.setItem(31, chestplateItem)
        inv.setItem(32, chestplateItem)

        player.openInventory(inv)
    }

    class Events: Listener {
        @EventHandler
        fun onInventoryClick(event: InventoryClickEvent) {
            if(event.clickedInventory?.holder !is DressingRoomMenu) return
            event.isCancelled = true

            if(event.slot == 0 || event.slot == 1 || event.slot == 2 || event.slot == 9 || event.slot == 10 || event.slot == 11) {
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                    DressingRoomCategoryMenu(event.whoClicked as Player, CosmeticType.getType("hat")!!).open(0)
                })
            }

            if(event.slot == 3 || event.slot == 4 || event.slot == 12 || event.slot == 13) {
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                    DressingRoomCategoryMenu(event.whoClicked as Player, CosmeticType.getType("handheld")!!).open(0)
                })
            }

            if(event.slot == 5 || event.slot == 6 || event.slot == 14 || event.slot == 15) {
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                    DressingRoomCategoryMenu(event.whoClicked as Player, CosmeticType.getType("balloon")!!).open(0)
                })
            }

            if(event.slot == 7 || event.slot == 8 || event.slot == 16 || event.slot == 17) {
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                    DressingRoomCategoryMenu(event.whoClicked as Player, CosmeticType.getType("title")!!).open(0)
                })
            }

            if(event.slot == 18 || event.slot == 19 || event.slot == 27 || event.slot == 28) {
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                    DressingRoomCategoryMenu(event.whoClicked as Player, CosmeticType.getType("boots")!!).open(0)
                })
            }

            if(event.slot == 20 || event.slot == 21 || event.slot == 29 || event.slot == 30) {
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                    DressingRoomCategoryMenu(event.whoClicked as Player, CosmeticType.getType("leggings")!!).open(0)
                })
            }

            if(event.slot == 22 || event.slot == 23 || event.slot == 31 || event.slot == 32) {
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                    DressingRoomCategoryMenu(event.whoClicked as Player, CosmeticType.getType("chestplate")!!).open(0)
                })
            }
        }
    }

    override fun getInventory(): Inventory {
        return lastInventory
    }
}