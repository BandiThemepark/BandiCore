package net.bandithemepark.bandicore.server.essentials

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class FlyCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("fly", true)) return false
        if(sender !is Player) return false

        if(!sender.hasPermission("bandithemepark.vip")) {
            sender.sendTranslatedMessage("no-fly-permissions", BandiColors.RED.toString())
            return false
        }

        if(sender.allowFlight) {
            sender.allowFlight = false
            sender.isFlying = false
            sender.sendTranslatedMessage("fly-disabled", BandiColors.RED.toString())
        } else {
            sender.allowFlight = true
            sender.sendTranslatedMessage("fly-enabled", BandiColors.GREEN.toString())
        }

        return false
    }
}