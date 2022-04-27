package net.bandithemepark.bandicore.server.restart

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class RestartCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(command.name.equals("bandirestart", true)) {
            if(sender.hasPermission("bandithemepark.crew")) {
                BandiCore.instance.restarter.start()
            } else {
                sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
            }
        }
        return false
    }
}