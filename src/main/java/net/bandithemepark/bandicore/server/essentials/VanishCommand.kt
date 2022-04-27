package net.bandithemepark.bandicore.server.essentials

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class VanishCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("vanish", true)) return false
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
            return false
        }
        if(sender !is Player) return false

        if(currentlyHidden.contains(sender)) {
            unVanish(sender)
            sender.sendTranslatedMessage("vanish-disabled", BandiColors.YELLOW.toString())
        } else {
            vanish(sender)
            sender.sendTranslatedMessage("vanish-enabled", BandiColors.YELLOW.toString())
        }

        return false
    }

    companion object {
        val currentlyHidden = mutableListOf<Player>()

        fun vanish(player: Player) {
            currentlyHidden.add(player)
            for(player2 in Bukkit.getOnlinePlayers().filter { it != player }) player2.hidePlayer(BandiCore.instance, player)
        }

        fun unVanish(player: Player) {
            currentlyHidden.remove(player)
            for(player2 in Bukkit.getOnlinePlayers().filter { it != player }) player2.showPlayer(BandiCore.instance, player)
        }
    }

    class Events: Listener {
        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            currentlyHidden.forEach { event.player.hidePlayer(BandiCore.instance, it) }
        }
    }
}