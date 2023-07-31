package net.bandithemepark.bandicore.network.audioserver.events

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.audioserver.AudioServerTimer
import net.bandithemepark.bandicore.network.audioserver.SpatialAudioSource
import net.bandithemepark.bandicore.network.backend.audioserver.BackendAudioServerCredentials
import net.bandithemepark.bandicore.network.mqtt.MQTTListener
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.server.regions.BandiRegion
import net.bandithemepark.bandicore.server.regions.events.PlayerPriorityRegionEnterEvent
import net.bandithemepark.bandicore.server.regions.events.PlayerPriorityRegionLeaveEvent
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class AudioServerEventListeners {
    companion object {
        val currentRegion = hashMapOf<Player, BandiRegion>()

        fun BandiRegion.convertToAudioClientJson(): JsonObject {
            val json = JsonObject()
            json.addProperty("id", this.uuid.toString())
            json.addProperty("name", this.name)
            json.addProperty("displayName", this.displayName)
            json.addProperty("priority", this.priority)

            return json
        }

        val connectedPlayers = mutableListOf<Player>()
    }

    class BukkitEventListeners: Listener {
        // ALL INTERNAL EVENTS

        @EventHandler
        fun onPlayerQuit(event: PlayerQuitEvent) {
            if(!connectedPlayers.contains(event.player)) return

            // Send message to AudioClient to disconnect player
            val messageJson = JsonObject()
            messageJson.addProperty("uuid", event.player.uniqueId.toString())
            BandiCore.instance.mqttConnector.sendMessage("/audioclient/player/${event.player.uniqueId}/connection/disconnectserver", messageJson.toString())

            connectedPlayers.remove(event.player)
        }

        @EventHandler
        fun onPriorityRegionEnter(event: PlayerPriorityRegionEnterEvent) {
            if(!connectedPlayers.contains(event.player)) return

            val messageJson = JsonObject()
            messageJson.addProperty("uuid", event.player.uniqueId.toString())
            messageJson.add("toRegion", event.toRegion.convertToAudioClientJson())

            if(event.fromRegion != null) {
                messageJson.addProperty("action", "switch")
                messageJson.add("fromRegion", event.fromRegion!!.convertToAudioClientJson())
            } else {
                messageJson.addProperty("action", "enter")
            }

            BandiCore.instance.mqttConnector.sendMessage("/audioclient/player/${event.player.uniqueId}/regions", messageJson.toString())

            // Update current region of player
            currentRegion[event.player] = event.toRegion
        }

        @EventHandler
        fun onPriorityRegionLeave(event: PlayerPriorityRegionLeaveEvent) {
            if(!connectedPlayers.contains(event.player)) return
            if(event.toRegion != null) return // The event is switching a region, which is also triggered by the enter event, so we stop here.

            val messageJson = JsonObject()
            messageJson.addProperty("uuid", event.player.uniqueId.toString())
            messageJson.addProperty("action", "leave")
            messageJson.add("fromRegion", event.fromRegion.convertToAudioClientJson())

            BandiCore.instance.mqttConnector.sendMessage("/audioclient/player/${event.player.uniqueId}/regions", messageJson.toString())

            // Update current region of player
            currentRegion.remove(event.player)
        }

        // ALL EXTERNAL EVENT HANDLING

        @EventHandler
        fun onAudioServerVolumeChange(event: AudioServerVolumeChangeEvent) {
            event.player.sendTranslatedActionBar("audioserver-volume-changed-client", BandiColors.YELLOW.toString(), MessageReplacement("volume", event.newVolume.toString()))
        }
    }

    class ListenerMQTT: MQTTListener("/audioclient/#") {
        override fun onMessage(topic: String, message: String) {
            if(topic.endsWith("/connection/connect")) {

                // Call event
                val json = JsonParser().parse(message).asJsonObject
                val player = Bukkit.getPlayer(UUID.fromString(json.get("uuid").asString))!!

                val event = AudioServerConnectEvent(player)
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { Bukkit.getPluginManager().callEvent(event) })

                // Send return info
                val messageJson = JsonObject()
                messageJson.addProperty("uuid", player.uniqueId.toString())
                messageJson.addProperty("name", player.name)
                messageJson.addProperty("vip", player.hasPermission("bandithemepark.vip"))

                val spatialAudioSourceArray = JsonArray()
                SpatialAudioSource.active.forEach {
                    spatialAudioSourceArray.add(it.toJson())
                }

                messageJson.add("sources", spatialAudioSourceArray)
                messageJson.add("players", AudioServerTimer().getCurrentPlayerInfo())
                messageJson.addProperty("startTime", BandiCore.instance.startTime)

                if(currentRegion[player] != null) {
                    messageJson.add("currentRegion", currentRegion[player]!!.convertToAudioClientJson())
                }

                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                    BandiCore.instance.mqttConnector.sendMessage("/audioclient/player/${player.uniqueId}/connection/connectserver", messageJson.toString())
                })
                if(!connectedPlayers.contains(event.player)) connectedPlayers.add(event.player)
            }

            if(topic.endsWith("/connection/disconnect")) {
                // Call event
                val json = JsonParser().parse(message).asJsonObject
                val player = Bukkit.getPlayer(UUID.fromString(json.get("uuid").asString))!!

                connectedPlayers.remove(player)

                val event = AudioServerDisconnectEvent(player)
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { Bukkit.getPluginManager().callEvent(event) })
            }

            if(topic.endsWith("/volume/client")) {
                // Call event
                val json = JsonParser().parse(message).asJsonObject
                val player = Bukkit.getPlayer(UUID.fromString(json.get("uuid").asString))!!
                val volume = json.get("volume").asInt

                val event = AudioServerVolumeChangeEvent(player, volume)
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { Bukkit.getPluginManager().callEvent(event) })
            }

            if(topic.startsWith("/audioclient/rideop") && topic.endsWith("/press")) {
                val json = JsonParser().parse(message).asJsonObject
                val player = Bukkit.getPlayer(UUID.fromString(json.get("uuid").asString))!!
                val playerAuthCode = BackendAudioServerCredentials.getLink(player) ?: return

                // Check auth code etc.

                val id = json.get("rideop").asString
                val rideOP = RideOP.rideOPs.find { it.id == id } ?: return
                val page = json.get("page").asString
                val button = json.get("button").asInt

                rideOP.pressButton(player, page, button)
            }
        }
    }
}