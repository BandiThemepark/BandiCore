package net.bandithemepark.bandicore.server.tools.painter

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class ItemPainter {
    companion object {
        val selections = hashMapOf<Player, Selection>()
    }

    data class Selection(var r: Int, var g: Int, var b: Int, var customModelData: Int) {
        fun getColor(): Color {
            return Color.fromRGB(r, g, b)
        }
    }

    fun open(player: Player) {
        open(player, 0, 0, 0, 0)
    }

    fun open(player: Player, r: Int, g: Int, b: Int, customModelData: Int) {
        selections[player] = Selection(r, g, b, customModelData)
        val inv = Bukkit.createInventory(null, 45, Component.text("Item Painter"))
        for(slot in 0..44) inv.setItem(slot, ItemFactory.create(Material.GRAY_STAINED_GLASS_PANE, Util.color(" ")))

        inv.setItem(1, ItemFactory.create(Material.LIME_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Add 10 to Red")))
        inv.setItem(2, ItemFactory.create(Material.LIME_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Add 10 to Green")))
        inv.setItem(3, ItemFactory.create(Material.LIME_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Add 10 to Blue")))
        inv.setItem(5, ItemFactory.create(Material.LIME_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Add 10 to Custom Model Data")))

        inv.setItem(10, ItemFactory.create(Material.LIME_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Add 1 to Red")))
        inv.setItem(11, ItemFactory.create(Material.LIME_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Add 1 to Green")))
        inv.setItem(12, ItemFactory.create(Material.LIME_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Add 1 to Blue")))
        inv.setItem(14, ItemFactory.create(Material.LIME_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Add 1 to Custom Model Data")))

        inv.setItem(28, ItemFactory.create(Material.RED_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Remove 1 from Red")))
        inv.setItem(29, ItemFactory.create(Material.RED_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Remove 1 from Green")))
        inv.setItem(30, ItemFactory.create(Material.RED_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Remove 1 from Blue")))
        inv.setItem(32, ItemFactory.create(Material.RED_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Remove 1 from Custom Model Data")))

        inv.setItem(37, ItemFactory.create(Material.RED_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Remove 10 from Red")))
        inv.setItem(38, ItemFactory.create(Material.RED_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Remove 10 from Green")))
        inv.setItem(39, ItemFactory.create(Material.RED_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Remove 10 from Blue")))
        inv.setItem(41, ItemFactory.create(Material.RED_TERRACOTTA, Util.color("<!i><${BandiColors.GREEN}>Remove 10 from Custom Model Data")))

        inv.setItem(19, ItemFactory.create(Material.RED_DYE, getOneIfZero(r), 0, Util.color("<!i><${BandiColors.GREEN}>Red ($r)")))
        inv.setItem(20, ItemFactory.create(Material.GREEN_DYE, getOneIfZero(g), 0, Util.color("<!i><${BandiColors.GREEN}>Green ($g)")))
        inv.setItem(21, ItemFactory.create(Material.BLUE_DYE, getOneIfZero(b), 0, Util.color("<!i><${BandiColors.GREEN}>Blue ($b)")))
        inv.setItem(23, ItemFactory.create(Material.DIAMOND_SWORD, getOneIfZero(customModelData), 0, Util.color("<!i><${BandiColors.GREEN}>Custom Model Data ($customModelData)")))

        inv.setItem(7, ItemFactory.create(Material.LEATHER_HELMET, 1, customModelData, Color.fromRGB(r, g, b), Util.color("<!i><${BandiColors.GREEN}>Get Helmet")))
        inv.setItem(16, ItemFactory.create(Material.LEATHER_CHESTPLATE, 1, customModelData, Color.fromRGB(r, g, b), Util.color("<!i><${BandiColors.GREEN}>Get Chestplate")))
        inv.setItem(25, ItemFactory.create(Material.LEATHER_LEGGINGS, 1, customModelData, Color.fromRGB(r, g, b), Util.color("<!i><${BandiColors.GREEN}>Get Leggings")))
        inv.setItem(34, ItemFactory.create(Material.LEATHER_BOOTS, 1, customModelData, Color.fromRGB(r, g, b), Util.color("<!i><${BandiColors.GREEN}>Get Boots")))
        inv.setItem(43, ItemFactory.create(Material.LEATHER_HORSE_ARMOR, 1, customModelData, Color.fromRGB(r, g, b), Util.color("<!i><${BandiColors.GREEN}>Get Horse Armor")))

        player.openInventory(inv)
    }

    fun getOneIfZero(int: Int): Int {
        if(int == 0) return 1
        return int
    }

    class Command: CommandExecutor {
        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
            if(command.name.equals("painter", true)) {
                if(sender is Player) {
                    if (sender.hasPermission("bandithemepark.crew")) {
                        ItemPainter().open(sender)
                    } else {
                        sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
                    }
                }
            }
            return false
        }
    }

    class Events: Listener {
        @EventHandler
        fun onInventoryClick(event: InventoryClickEvent) {
            if(event.view.title() == Component.text("Item Painter")) {
                event.isCancelled = true

                when(event.slot) {
                    1 -> openDifference(event.whoClicked as Player, 10, 0, 0, 0)
                    2 -> openDifference(event.whoClicked as Player, 0, 10, 0, 0)
                    3 -> openDifference(event.whoClicked as Player, 0, 0, 10, 0)
                    5 -> openDifference(event.whoClicked as Player, 0, 0, 0, 10)

                    10 -> openDifference(event.whoClicked as Player, 1, 0, 0, 0)
                    11 -> openDifference(event.whoClicked as Player, 0, 1, 0, 0)
                    12 -> openDifference(event.whoClicked as Player, 0, 0, 1, 0)
                    14 -> openDifference(event.whoClicked as Player, 0, 0, 0, 1)

                    28 -> openDifference(event.whoClicked as Player, -1, 0, 0, 0)
                    29 -> openDifference(event.whoClicked as Player, 0, -1, 0, 0)
                    30 -> openDifference(event.whoClicked as Player, 0, 0, -1, 0)
                    32 -> openDifference(event.whoClicked as Player, 0, 0, 0, -1)

                    37 -> openDifference(event.whoClicked as Player, -10, 0, 0, 0)
                    38 -> openDifference(event.whoClicked as Player, 0, -10, 0, 0)
                    39 -> openDifference(event.whoClicked as Player, 0, 0, -10, 0)
                    41 -> openDifference(event.whoClicked as Player, 0, 0, 0, -10)

                    7 -> {
                        val oldSelection = selections[event.whoClicked as Player]!!
                        event.whoClicked.inventory.addItem(ItemFactory.create(Material.LEATHER_HELMET, 1, oldSelection.customModelData, oldSelection.getColor(), Component.text("")))
                    }
                    16 -> {
                        val oldSelection = selections[event.whoClicked as Player]!!
                        event.whoClicked.inventory.addItem(ItemFactory.create(Material.LEATHER_CHESTPLATE, 1, oldSelection.customModelData, oldSelection.getColor(), Component.text("")))
                    }
                    25 -> {
                        val oldSelection = selections[event.whoClicked as Player]!!
                        event.whoClicked.inventory.addItem(ItemFactory.create(Material.LEATHER_LEGGINGS, 1, oldSelection.customModelData, oldSelection.getColor(), Component.text("")))
                    }
                    34 -> {
                        val oldSelection = selections[event.whoClicked as Player]!!
                        event.whoClicked.inventory.addItem(ItemFactory.create(Material.LEATHER_BOOTS, 1, oldSelection.customModelData, oldSelection.getColor(), Component.text("")))
                    }
                    43 -> {
                        val oldSelection = selections[event.whoClicked as Player]!!
                        event.whoClicked.inventory.addItem(ItemFactory.create(Material.LEATHER_HORSE_ARMOR, 1, oldSelection.customModelData, oldSelection.getColor(), Component.text("")))
                    }

                    else -> {}
                }
            }
        }

        private fun openDifference(player: Player, r: Int, g: Int, b: Int, customModelData: Int) {
            val oldSelection = selections[player]!!
            val newSelection = Selection(oldSelection.r+r, oldSelection.g+g, oldSelection.b+b, oldSelection.customModelData+customModelData)

            if(newSelection.r < 0) newSelection.r = 0
            if(newSelection.g < 0) newSelection.g = 0
            if(newSelection.b < 0) newSelection.b = 0

            if(newSelection.r > 255) newSelection.r = 255
            if(newSelection.g > 255) newSelection.g = 255
            if(newSelection.b > 255) newSelection.b = 255

            ItemPainter().open(player, newSelection.r, newSelection.g, newSelection.b, newSelection.customModelData)
        }
    }
}