package net.bandithemepark.bandicore.server.essentials.teleport

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TeleportCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("teleport", true)) return false
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage(
                "no-permission",
                BandiColors.RED.toString()
            )
            return true
        }
        if (args.size == 1) {
            if (sender !is Player) return false

            val target = Bukkit.getPlayer(args[0])
            if (target == null) {
                sender.sendTranslatedMessage("player-not-online", BandiColors.RED.toString())
                return true
            }

            sender.teleport(target)
            sender.sendTranslatedMessage(
                "teleport-to-player",
                BandiColors.YELLOW.toString(),
                MessageReplacement("target", target.name)
            )

            return true
        }
        if (args.size == 2) {

            val target1 = Bukkit.getPlayer(args[0])
            val target2 = Bukkit.getPlayer(args[1])

            if (target1 == null || target2 == null) {
                sender.sendTranslatedMessage("player-not-online", BandiColors.RED.toString())
                return true
            }

            target1.teleport(target2)
            sender.sendTranslatedMessage(
                "teleport-player-to-player",
                BandiColors.YELLOW.toString(),
                MessageReplacement("target1", target1.name),
                MessageReplacement("target2", target2.name)
            )

            return true
        }
        sender.sendTranslatedMessage("teleport-usage", BandiColors.RED.toString())

        return true;
    }
}