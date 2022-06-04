package net.bandithemepark.bandicore.server.essentials

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TimeManagement: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage(
                "no-permission",
                BandiColors.RED.toString()
            )
            return true
        }
        if (sender !is Player) return false
        if (command.name.equals("day", true) || command.name.equals("sun", true)) {
            sender.world.time = 6000
            sender.sendTranslatedMessage("time-change", BandiColors.YELLOW.toString(),
                MessageReplacement("time", "DAY"))
        }
        if (command.name.equals("night", true)) {
            sender.world.time = 18000
            sender.sendTranslatedMessage("time-change", BandiColors.YELLOW.toString(),
                MessageReplacement("time", "NIGHT"))
        }
        return true
    }
}