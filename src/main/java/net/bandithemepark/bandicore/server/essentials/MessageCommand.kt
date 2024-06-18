package net.bandithemepark.bandicore.server.essentials

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MessageCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("message", true)) return false
        if(sender !is Player) return false

        if(args.size < 2) {
            sender.sendMessage(Util.color("<${BandiColors.RED}>Incorrect usage! /message <player> <text>"))
            return false
        }

        val targetPlayer = Bukkit.getPlayer(args[0])
        if(targetPlayer == null) {
            sender.sendTranslatedMessage("player-not-online", BandiColors.RED.toString())
            return false
        }

        val message = args.drop(1).joinToString(" ")
        message(sender, targetPlayer, message)

        return false
    }

    companion object {
        fun message(sender: Player, targetPlayer: Player, message: String) {
            sender.sendMessage(Util.color("<${BandiColors.YELLOW}>You » ${targetPlayer.name} <${BandiColors.LIGHT_GRAY}>$message"))
            targetPlayer.sendMessage(Util.color("<${BandiColors.YELLOW}>${sender.name} » You <${BandiColors.LIGHT_GRAY}>$message"))
            lastMessages[sender] = targetPlayer
            lastMessages[targetPlayer] = sender
        }

        val lastMessages = hashMapOf<Player, Player>()
    }
}