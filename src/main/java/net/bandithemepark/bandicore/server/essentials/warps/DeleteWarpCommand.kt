package net.bandithemepark.bandicore.server.essentials.warps

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendWarp
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class DeleteWarpCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("deletewarp", true)) return false
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
            return true
        }

        if(args.size != 1) {
            sender.sendTranslatedMessage("delete-warp-command-incorrect-usage", BandiColors.RED.toString())
            return false
        }

        val warpName = args[0]
        val warp = BandiCore.instance.server.warpManager.warps.find { it.name == warpName }

        if(warp == null) {
            sender.sendTranslatedMessage("warp-command-warp-not-found", BandiColors.RED.toString())
            return false
        }

        BackendWarp(warp).delete {
            BandiCore.instance.server.warpManager.warps.remove(warp)
            sender.sendTranslatedMessage("delete-warp-command-warp-deleted", BandiColors.YELLOW.toString())
        }
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        if(!command.name.equals("deletewarp", true)) return null
        if (!sender.hasPermission("bandithemepark.crew")) return null
        if(args.size != 1) return null

        return Util.getTabCompletions(args[0], BandiCore.instance.server.warpManager.warps.map { it.name })
    }
}