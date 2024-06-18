package net.bandithemepark.bandicore.server.essentials

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ReactCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("react", true)) return false
        if (sender !is Player) return false

        if (args.isEmpty()) {
            sender.sendMessage(Util.color("<${BandiColors.RED}>Incorrect usage! /react <text>"))
            return false
        }

        val targetPlayer = MessageCommand.lastMessages[sender]
        if (targetPlayer == null) {
            sender.sendTranslatedMessage("no-one-to-react", BandiColors.RED.toString())
            return false
        }

        if(!targetPlayer.isOnline) {
            sender.sendTranslatedMessage("player-not-online", BandiColors.RED.toString())
            return false
        }

        val message = args.joinToString(" ")
        MessageCommand.message(sender, targetPlayer, message)

        return false
    }
}