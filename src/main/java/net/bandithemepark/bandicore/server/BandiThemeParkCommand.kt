package net.bandithemepark.bandicore.server

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.mode.ServerMode
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.debug.Debuggable
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class BandiThemeParkCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("bandithemepark", true)) return false
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
            return true
        }

        if(args.size == 1) {
            if(args[0].equals("restart", true)) {
                BandiCore.instance.restarter.start()
            } else {
                sendHelp(sender)
            }
        } else if(args.size == 2) {
            if(args[0].equals("servermode", true)) {
                val newServerMode = ServerMode.getFromId(args[1])

                if(newServerMode == null) {
                    sender.sendTranslatedMessage("server-mode-invalid-mode", BandiColors.RED.toString())
                } else {
                    BandiCore.instance.server.changeServerMode(newServerMode)
                    sender.sendTranslatedMessage("server-mode-changed", BandiColors.YELLOW.toString(), MessageReplacement("mode", newServerMode.id))
                }
            } else if(args[0].equals("debug", true)) {
                val debuggable = Debuggable.getDebuggable(args[1])

                if(debuggable != null) {
                    sender.sendTranslatedMessage("debug-start", BandiColors.YELLOW.toString(), MessageReplacement("option", args[1]))
                    debuggable.debug(sender)
                } else {
                    sender.sendTranslatedMessage("debug-invalid-option", BandiColors.RED.toString())
                }
            } else {
                sendHelp(sender)
            }
        } else {
            sendHelp(sender)
        }

        return false
    }

    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage(Util.color("<${BandiColors.RED}>/bandithemepark help"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/bandithemepark restart"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/bandithemepark servermode <mode>"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/bandithemepark debug <option>"))
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        if(!command.name.equals("region", true)) return null

        if(args.size == 1) {
            return Util.getTabCompletions(args[0], listOf("restart", "servermode", "debug", "help"))
        } else if(args.size == 2) {
            if(args[0].equals("servermode", true)) {
                return Util.getTabCompletions(args[1], ServerMode.getAllIds())
            } else if(args[0].equals("debug", true)) {
                return Util.getTabCompletions(args[1], Debuggable.debuggables.keys.toList())
            }
        }

        return null
    }
}