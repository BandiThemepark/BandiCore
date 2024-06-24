package net.bandithemepark.bandicore.park.shops.opener

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class ShopOpenerCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("shopopener", ignoreCase = true)) return false
        if(sender !is Player) return false

        if(!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
            return false
        }

        if(args.isEmpty()) {
            sendHelp(sender)
            return false
        }

        if(args[0].equals("remove", true)) {
            val nearbyShopOpener = BandiCore.instance.shopManager.shopOpenerManager.getNearestInRadius(sender.location, 3.0)

            if(nearbyShopOpener == null) {
                sender.sendTranslatedMessage("shop-opener-not-found", BandiColors.RED.toString())
                return false
            }

            BandiCore.instance.shopManager.shopOpenerManager.deleteShopOpener(nearbyShopOpener)
            sender.sendTranslatedMessage("shop-opener-deleted", BandiColors.YELLOW.toString())
        } else if(args[0].equals("create", true)) {
            if(args.size != 2) {
                sendHelp(sender)
                return false
            }

            val shop = BandiCore.instance.shopManager.shops.find { it.name.equals(args[1], ignoreCase = true) }
            if(shop == null) {
                sender.sendTranslatedMessage("shop-not-found", BandiColors.RED.toString())
                return false
            }

            // Snap location to middle of block
            val location = Location(sender.world, sender.location.blockX + 0.5, sender.location.blockY + 0.5, sender.location.blockZ + 0.5)

            // Snap yaw to 15 degrees
            val yaw = Math.round(sender.location.yaw / 15.0) * 15.0
            location.yaw = yaw.toFloat()

            BandiCore.instance.shopManager.shopOpenerManager.createShopOpener(location, shop)
            sender.sendTranslatedMessage("shop-opener-created", BandiColors.YELLOW.toString())
        } else {
            sendHelp(sender)
        }

        return false
    }

    fun sendHelp(sender: CommandSender) {
        sender.sendMessage(Util.color("<${BandiColors.RED}>/shopopener create <shop-name>"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/shopopener remove"))
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        if(!command.name.equals("shopopener", true)) return null

        if(args.size == 1) {
            return Util.getTabCompletions(args[0], listOf("create", "remove"))
        } else if(args.size == 2) {
            if(args[0].equals("create", true)) {
                val shopNames = BandiCore.instance.shopManager.shops.map { it.name }
                return Util.getTabCompletions(args[1], shopNames)
            }
        }

        return null
    }
}