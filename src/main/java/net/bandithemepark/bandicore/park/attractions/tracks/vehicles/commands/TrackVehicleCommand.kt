package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.commands

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

abstract class TrackVehicleCommand(val id: String, val howToUse: String) {
    abstract fun onUse(sender: CommandSender, args: MutableList<String>)

    /**
     * Sends the command usage of a command
     * @param sender Who to send the usage to
     */
    fun sendUsage(sender: CommandSender) {
        sender.sendTranslatedMessage("track-command-usage", BandiColors.RED.toString(), MessageReplacement("id", id), MessageReplacement("howToUse", howToUse))
    }

    fun register() {
        registeredCommands.add(this)
    }

    companion object {
        val registeredCommands = mutableListOf<TrackVehicleCommand>()

        /**
         * Sends a sender a help message
         * @param sender The sender to send the help message to
         */
        fun sendHelp(sender: CommandSender) {
            sender.sendTranslatedMessage("track-vehicle-command-help", BandiColors.RED.toString())

            for (command in registeredCommands) {
                sender.sendMessage(Util.color("<${BandiColors.RED}>/trackvehicle ${command.id} ${command.howToUse}"))
            }
        }

        /**
         * Used to retrieve a command from its ID. Ignores casing
         * @param id The id of the command
         * @return The command if it exists, null otherwise
         */
        fun getCommand(id: String): TrackVehicleCommand? {
            return registeredCommands.find { it.id == id.lowercase() }
        }
    }

    class Command: CommandExecutor {
        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
            if(command.name.equals("trackvehicle", true)) {
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