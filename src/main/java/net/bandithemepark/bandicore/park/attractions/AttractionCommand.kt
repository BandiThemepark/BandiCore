package net.bandithemepark.bandicore.park.attractions

import net.bandithemepark.bandicore.park.attractions.mode.AttractionMode
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class AttractionCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("attraction", true)) return false

        if (!sender.hasPermission("bandithemepark.crew")) {
            // TODO Open menu
        } else {
            if(args.isEmpty()) {
                // TODO Open menu
            } else {
                if(args.size == 3) {
                    if(args[0].equals("setmode", true)) {
                        val attraction = Attraction.get(args[1])
                        if(attraction == null) {
                            sender.sendTranslatedMessage("attraction-not-found", BandiColors.RED.toString())
                            return false
                        }

                        val mode = AttractionMode.getMode(args[2])
                        if(mode == null) {
                            sender.sendTranslatedMessage("attraction-mode-not-found", BandiColors.RED.toString())
                            return false
                        }

                        attraction.mode = mode
                        attraction.updateModeInConfig()
                        sender.sendTranslatedMessage("attraction-mode-set", BandiColors.YELLOW.toString(), MessageReplacement("attraction", attraction.appearance.displayName), MessageReplacement("mode", mode.id))
                    } else {
                        sendHelp(sender)
                    }
                } else {
                    sendHelp(sender)
                }
            }
        }
        return false
    }

    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage(Util.color("<${BandiColors.RED}>/attraction help"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/attraction setmode <attraction> <mode>"))
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        if(!command.name.equals("attraction", true)) return null
        if(!sender.hasPermission("bandithemepark.crew")) return null

        if(args.size == 1) {
            return Util.getTabCompletions(args[0], listOf("help", "setmode"))
        } else if(args.size == 2) {
            if(args[0].equals("setmode", true)) {
                return Util.getTabCompletions(args[1], Attraction.attractions.map { it.id })
            }
        } else if(args.size == 3) {
            if(args[0].equals("setmode", true)) {
                return Util.getTabCompletions(args[2], AttractionMode.modes.map { it.id })
            }
        }

        return null
    }
}