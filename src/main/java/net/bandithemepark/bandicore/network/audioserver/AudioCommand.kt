package net.bandithemepark.bandicore.network.audioserver

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.audioserver.events.AudioServerConnectEvent
import net.bandithemepark.bandicore.network.audioserver.events.AudioServerDisconnectEvent
import net.bandithemepark.bandicore.network.backend.audioserver.BackendAudioServerCredentials
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class AudioCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("audio", true)) return false
        if(sender !is Player) return false

        sendMessage(sender)

        return false
    }

    class Events: Listener {
        @EventHandler
        fun onPlayerJoin(event: PlayerJoinEvent) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, Runnable {
                sendMessage(event.player)
            }, 20)
        }

        @EventHandler
        fun onAudioServerConnect(event: AudioServerConnectEvent) {
            event.player.sendTranslatedActionBar("audio-server-connected", BandiColors.GREEN.toString())
        }

        @EventHandler
        fun onAudioServerDisconnect(event: AudioServerDisconnectEvent) {
            event.player.sendTranslatedActionBar("audio-server-disconnected", BandiColors.RED.toString())
        }
    }

    companion object {
        fun sendMessage(player: Player) {
            if(connectedPlayers.contains(player)) {
                player.sendTranslatedMessage("audio-server-already-connected", BandiColors.RED.toString())
                return
            }

            if(BackendAudioServerCredentials.getLink(player) == null) {
                BackendAudioServerCredentials.generateNew(player) {
                    sendLink(player)
                }
            } else {
                sendLink(player)
            }
        }

        private fun sendLink(player: Player) {
            val link = BackendAudioServerCredentials.getLink(player)!!
            player.sendMessage(Util.color(
                "<#dbc835>♫ <click:open_url:$link><hover:show_text:'<#dbc835>$link'>${player.getTranslatedMessage("audio-server-message")}</hover></click>"
            ))
        }

        val connectedPlayers = mutableListOf<Player>()
    }
}