package net.bandithemepark.bandicore.network.audioserver.ride

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.audioserver.events.AudioServerConnectEvent
import net.bandithemepark.bandicore.network.audioserver.events.AudioServerEventListeners
import net.bandithemepark.bandicore.network.audioserver.events.AudioServerEventListeners.Companion.convertToAudioClientJson
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class SpecialAudioManagement: Listener {

    @EventHandler
    fun onAudioServerConnect(event: AudioServerConnectEvent) {
        if(currentlyPlayingOnrideAudio.containsKey(event.player)) {
            catchUpOnrideAudio(event.player)
        }
    }

    companion object {
        var currentlyPlayingOnrideAudio = hashMapOf<Player, OnrideAudioSessionData>()

        // Function to play onride audio for a player, including source and start time
        fun playOnrideAudio(player: Player, sourceId: String, startTimeMillis: Long) {
            currentlyPlayingOnrideAudio[player] = OnrideAudioSessionData(sourceId, startTimeMillis, System.currentTimeMillis())
            sendOnrideAudio(player, sourceId, startTimeMillis)
        }

        private fun sendOnrideAudio(player: Player, sourceId: String, startTimeMillis: Long) {
            val messageJson = JsonObject()
            messageJson.addProperty("uuid", player.uniqueId.toString())
            messageJson.addProperty("source", sourceId)
            messageJson.addProperty("startTimeMillis", startTimeMillis)

            BandiCore.instance.mqttConnector.sendMessage("/audioclient/player/${player.uniqueId}/ride/start", messageJson.toString())
        }

        // Function to stop onride audio for a player
        fun stopOnrideAudio(player: Player) {
            val messageJson = JsonObject()
            messageJson.addProperty("uuid", player.uniqueId.toString())

            if(AudioServerEventListeners.currentRegion[player] != null) {
                messageJson.add("currentRegion", AudioServerEventListeners.currentRegion[player]!!.convertToAudioClientJson())
            }

            BandiCore.instance.mqttConnector.sendMessage("/audioclient/player/${player.uniqueId}/ride/stop", messageJson.toString())
        }

        // Function to play a normal sound effect for a player
        fun playSoundEffect(player: Player, sourceId: String) {
            val messageJson = JsonObject()
            messageJson.addProperty("uuid", player.uniqueId.toString())
            messageJson.addProperty("source", sourceId)

            BandiCore.instance.mqttConnector.sendMessage("/audioclient/player/${player.uniqueId}/soundeffect", messageJson.toString())
        }

        // Function to make player catch up when connecting to the audioserver after starting the ride
        fun catchUpOnrideAudio(player: Player) {
            val startTime = System.currentTimeMillis() - currentlyPlayingOnrideAudio[player]!!.startedListening + currentlyPlayingOnrideAudio[player]!!.startTimeMillis
            sendOnrideAudio(player, currentlyPlayingOnrideAudio[player]!!.sourceId, startTime)
        }
    }

    data class OnrideAudioSessionData(val sourceId: String, val startTimeMillis: Long, val startedListening: Long)

}