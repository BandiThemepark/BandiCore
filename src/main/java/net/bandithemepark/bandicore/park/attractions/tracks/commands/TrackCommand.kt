package net.bandithemepark.bandicore.park.attractions.tracks.commands

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.BandiColors
import net.bandithemepark.bandicore.util.Util
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

abstract class TrackCommand(val id: String, val howToUse: String) {
    abstract fun onUse(sender: CommandSender, args: MutableList<String>)

    fun sendUsage(sender: CommandSender) {
        sender.sendTranslatedMessage("track-command-usage", BandiColors.RED.toString(), MessageReplacement("id", id), MessageReplacement("howToUse", howToUse))
    }

    fun register() {
        registeredCommands.add(this)
    }

    companion object {
        val registeredCommands = mutableListOf<TrackCommand>()

        fun sendHelp(sender: CommandSender) {
            sender.sendTranslatedMessage("track-command-help", BandiColors.RED.toString())

            for (command in registeredCommands) {
                sender.sendMessage(Util.color("<${BandiColors.RED}>/track ${command.id} ${command.howToUse}"))
            }
        }

        fun getCommand(id: String): TrackCommand? {
            return registeredCommands.find { it.id == id.lowercase() }
        }
    }

    class Command: CommandExecutor {
        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
            if(command.name.equals("track", true)) {
                if(sender.hasPermission("bandithemepark.crew")) {
                    if(args.isNotEmpty()) {
                        if(getCommand(args[0]) == null) {
                            sendHelp(sender)
                        } else {
                            val newArgs = args.toMutableList()
                            newArgs.removeAt(0)
                            getCommand(args[0])!!.onUse(sender, newArgs)
                        }
                    } else {
                        sendHelp(sender)
                    }
                } else {
                    sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
                }
            }
            return false
        }

    }
}