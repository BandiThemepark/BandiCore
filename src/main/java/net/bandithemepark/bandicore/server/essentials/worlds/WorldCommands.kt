package net.bandithemepark.bandicore.server.essentials.worlds

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class WorldCommands: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(command.name.equals("loadworld", true)) {
            if (!sender.hasPermission("bandithemepark.crew")) {
                sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
                return true
            }

            if(args.size != 1) {
                sender.sendTranslatedMessage("load-world-usage", BandiColors.RED.toString())
                return true
            }

            sender.sendTranslatedMessage("loading-world", BandiColors.YELLOW.toString())
            val success = BandiCore.instance.worldManager.loadNewWorld(args[0])
            if(success) {
                sender.sendTranslatedMessage("load-world-success", BandiColors.YELLOW.toString())
            } else {
                sender.sendTranslatedMessage("load-world-failure", BandiColors.RED.toString())
            }
        }

        if(command.name.equals("unloadworld", true)) {
            if (!sender.hasPermission("bandithemepark.crew")) {
                sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
                return true
            }

            if(args.size != 1) {
                sender.sendTranslatedMessage("unload-world-usage", BandiColors.RED.toString())
                return true
            }

            sender.sendTranslatedMessage("unloading-world", BandiColors.YELLOW.toString())
            BandiCore.instance.worldManager.unloadWorld(args[0])
            sender.sendTranslatedMessage("unload-world-success", BandiColors.YELLOW.toString())
        }

        if(command.name.equals("worldtp", true)) {
            if(sender !is Player) return false
            if (!sender.hasPermission("bandithemepark.crew")) {
                sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
                return true
            }

            if(args.size != 1) {
                sender.sendTranslatedMessage("world-teleport-usage", BandiColors.RED.toString())
                return true
            }

            sender.teleport(Location(Bukkit.getWorld(args[0]), 0.5, 80.0, 0.5))
            sender.gameMode = GameMode.CREATIVE
            sender.allowFlight = true
            sender.isFlying = true
            sender.sendTranslatedMessage("world-teleport-success", BandiColors.YELLOW.toString())
        }

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        if(command.name.equals("worldtp", true)) {
            if(sender.hasPermission("bandithemepark.crew")) {
                if(args.size == 1) {
                    return Util.getTabCompletions(args[0], BandiCore.instance.worldManager.loadedWorldNames)
                }
            }
        }
        return null
    }
}