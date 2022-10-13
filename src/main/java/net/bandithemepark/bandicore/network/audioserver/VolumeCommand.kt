package net.bandithemepark.bandicore.network.audioserver

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.audioserver.events.AudioServerEventListeners
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class VolumeCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!command.name.equals("volume", true)) return false
        if(sender !is Player) return false

        if(!AudioServerEventListeners.connectedPlayers.contains(sender)) {
            sender.sendTranslatedMessage("audioserver-not-connected", BandiColors.RED.toString())
            return false
        }

        if(args.size != 1) {
            sender.sendTranslatedMessage("audioserver-volume-invalid-args", BandiColors.RED.toString())
            return false
        }

        if(!isInt(args[0])) {
            sender.sendTranslatedMessage("audioserver-incorrect-volume", BandiColors.RED.toString())
            return false
        }

        val volume = args[0].toInt()
        if(volume < 0 || volume > 100) {
            sender.sendTranslatedMessage("audioserver-incorrect-volume", BandiColors.RED.toString())
            return false
        }

        val messageJson = JsonObject()
        messageJson.addProperty("volume", volume)
        BandiCore.instance.mqttConnector.sendMessage("/audioclient/player/${sender.uniqueId}/volume/server", messageJson.toString())

        sender.sendTranslatedMessage("audioserver-volume-changed", BandiColors.YELLOW.toString(), MessageReplacement("volume", volume.toString()))

        return false
    }

    private fun isInt(str: String): Boolean {
        return str.toIntOrNull() != null
    }
}