package net.bandithemepark.bandicore.server.essentials.teleport

import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class BackCommand : CommandExecutor {

    companion object {
        val lastPosition = HashMap<Player, Location>()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("back", true)) return false
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage(
                "no-permission",
                BandiColors.RED.toString()
            )
            return true
        }
        if (sender !is Player) return false

        lastPosition[sender]?.let { sender.teleport(it) }
        sender.sendTranslatedMessage("teleport-back", BandiColors.YELLOW.toString())

        return true;
    }

    class Events: Listener {

        @EventHandler
        fun onTeleport(event: PlayerTeleportEvent) {
            lastPosition[event.player] = event.from
        }
    }
}