package net.bandithemepark.bandicore.server.mode

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.Util
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class ServerModeCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(command.name.equals("servermode", true)) {
            if(sender.hasPermission("bandithemepark.crew")) {
                if(args.size == 1) {
                    val newServerMode = ServerMode.getFromId(args[0])

                    if(newServerMode == null) {
                        sender.sendTranslatedMessage("server-mode-invalid-mode", "#963939")
                    } else {
                        BandiCore.instance.server.changeServerMode(newServerMode)
                        sender.sendTranslatedMessage("server-mode-changed", "#E0D268", MessageReplacement("mode", newServerMode.id))
                    }
                } else {
                    sender.sendTranslatedMessage("server-mode-invalid-args", "#963939")
                }
            } else {
                sender.sendTranslatedMessage("no-permission", "#963939")
            }
        }
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        if(command.name.equals("servermode", true)) {
            if(sender.hasPermission("bandithemepark.crew")) {
                if(args.size == 1) {
                    return Util.getTabCompletions(args[0], ServerMode.getAllIds())
                }
            }
        }
        return null
    }
}