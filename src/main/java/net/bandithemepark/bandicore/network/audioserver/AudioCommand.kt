package net.bandithemepark.bandicore.network.audioserver

import net.bandithemepark.bandicore.network.backend.audioserver.BackendAudioServerCredentials
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
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

        if(BackendAudioServerCredentials.getLink(sender) == null) {
            BackendAudioServerCredentials.generateNew(sender) {
                sender.sendMessage(Util.color(
                    "<#dbc835>♫ <click:open_url:${BackendAudioServerCredentials.getLink(sender)!!}>${sender.getTranslatedMessage("audio-server-message")}</click>"
                ))
            }
        } else {
            sender.sendMessage(Util.color(
                "<#dbc835>♫ <click:open_url:${BackendAudioServerCredentials.getLink(sender)!!}>${sender.getTranslatedMessage("audio-server-message")}</click>"
            ))
        }

        return false
    }

    class Events: Listener {
        @EventHandler
        fun onPlayerJoin(event: PlayerJoinEvent) {
            if(BackendAudioServerCredentials.getLink(event.player) == null) {
                BackendAudioServerCredentials.generateNew(event.player) {
                    event.player.sendMessage(Util.color(
                        "<#dbc835>♫ <click:open_url:${BackendAudioServerCredentials.getLink(event.player)!!}>${event.player.getTranslatedMessage("audio-server-message")}</click>"
                    ))
                }
            } else {
                event.player.sendMessage(Util.color(
                    "<#dbc835>♫ <click:open_url:${BackendAudioServerCredentials.getLink(event.player)!!}>${event.player.getTranslatedMessage("audio-server-message")}</click>"
                ))
            }
        }
    }
}