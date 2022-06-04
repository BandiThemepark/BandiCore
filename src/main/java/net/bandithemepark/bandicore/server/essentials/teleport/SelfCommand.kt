package net.bandithemepark.bandicore.server.essentials.teleport

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SelfCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("self", true)) return false
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage(
                "no-permission",
                BandiColors.RED.toString()
            )
            return true
        }
        if (sender !is Player) return false
        if (args.size == 1) {
            val target = Bukkit.getPlayer(args[0])
            if (target == null) {
                sender.sendTranslatedMessage("player-not-online", BandiColors.RED.toString())
                return true
            }

            target.teleport(sender)
            sender.sendTranslatedMessage(
                "teleport-self",
                BandiColors.YELLOW.toString(),
                MessageReplacement("target", target.name)
            )

            return true
        }
        if (args.size > 1) {
            val playerStrings = args.copyOfRange(1, args.size - 1)

            for (player in playerStrings) {
                val target = Bukkit.getPlayer(player)
                if (target == null) {
                    sender.sendTranslatedMessage("player-not-online", BandiColors.RED.toString())
                    break
                }
                target.teleport(sender)
            }

            sender.sendTranslatedMessage("teleport-self-multiple", BandiColors.YELLOW.toString(),
                MessageReplacement("target_count", (args.size - 2).toString()))

            return true
        }
        sender.sendTranslatedMessage("teleport-self-usage", BandiColors.RED.toString())

        return true
    }
}