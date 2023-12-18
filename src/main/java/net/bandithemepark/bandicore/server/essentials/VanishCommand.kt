package net.bandithemepark.bandicore.server.essentials

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag.Companion.getNameTag
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
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
            player.getNameTag()?.hidden = true
            for(player2 in Bukkit.getOnlinePlayers()) player2.hidePlayer(BandiCore.instance, player)
            hideTabList(player, Bukkit.getOnlinePlayers().filter { it != player })
        }

        fun unVanish(player: Player) {
            currentlyHidden.remove(player)
            player.getNameTag()?.hidden = false
            for(player2 in Bukkit.getOnlinePlayers()) player2.showPlayer(BandiCore.instance, player)
            showTabList(player, Bukkit.getOnlinePlayers().filter { it != player })
        }

        fun hideTabList(player: Player, forPlayers: List<Player>) {
            val packet = ClientboundPlayerInfoRemovePacket(mutableListOf((player as CraftPlayer).handle.uuid))
            for(player2 in forPlayers) (player2 as CraftPlayer).handle.connection.send(packet)
        }

        fun showTabList(player: Player, forPlayers: List<Player>) {
            val packet = ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, (player as CraftPlayer).handle)
            for(player2 in forPlayers) (player2 as CraftPlayer).handle.connection.send(packet)
        }
    }

    class Events: Listener {
        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            currentlyHidden.forEach {
                event.player.hidePlayer(BandiCore.instance, it)
                hideTabList(it, listOf(event.player))
            }
        }
    }
}