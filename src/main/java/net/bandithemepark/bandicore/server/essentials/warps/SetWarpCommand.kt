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
import org.bukkit.entity.Player
import java.util.*

class SetWarpCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("setwarp", true)) return false
        if(sender !is Player) return false
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
            return true
        }

        if(args.size !in 1..2) {
            sender.sendTranslatedMessage("set-warp-command-incorrect-usage", BandiColors.RED.toString())
            return false
        }

        val warpName = args[0]
        val warp = BandiCore.instance.server.warpManager.warps.find { it.name == warpName }
        var permission = null as String?
        if(args.size > 1) permission = args[1]

        if(warp == null) {
            val newWarp = Warp(UUID.randomUUID(), warpName, sender.location, permission)
            BackendWarp(newWarp).create {
                newWarp.uuid = UUID.fromString(it.get("id").asString)
                BandiCore.instance.server.warpManager.warps.add(newWarp)
                sender.sendTranslatedMessage("set-warp-command-warp-created", BandiColors.YELLOW.toString())
            }
        } else {
            warp.location = sender.location
            warp.permission = permission
            BackendWarp(warp).update {
                sender.sendTranslatedMessage("set-warp-command-warp-updated", BandiColors.YELLOW.toString())
            }
        }

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        if(!command.name.equals("setwarp", true)) return null
        if (!sender.hasPermission("bandithemepark.crew")) return null
        if(args.size != 1) return null

        return Util.getTabCompletions(args[0], BandiCore.instance.server.warpManager.warps.map { it.name })
    }
}