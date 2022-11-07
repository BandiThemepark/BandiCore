package net.bandithemepark.bandicore.network.backend.audioserver

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.BandiCore
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.io.IOException

class BackendAudioServerCredentials {
    companion object {
        private val links = mutableMapOf<Player, String>()

        fun generateNew(player: Player, callback: () -> Unit) {
            val data = JsonObject()
            data.addProperty("playerUuid", player.uniqueId.toString())
            data.addProperty("host", "audio.bandithemepark.net")

            val client = BandiCore.instance.okHttpClient
            val mediaType = "application/json".toMediaTypeOrNull()

            val request = Request.Builder()
                .url("https://api.bandithemepark.net/audio/credentials/create")
                .method("POST", data.toString().toRequestBody(mediaType))
                .header("Authorization", BandiCore.instance.server.apiKey)
                .build()

            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseJson = JsonParser().parse(response.body!!.string()).asJsonObject
                    if (responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                        val link = responseJson.get("data").asString
                        links[player] = link
                        callback.invoke()
                    } else {
                        BandiCore.instance.logger.severe("An attempt was made at generating the AudioServer link player ${player.name}, but no response data was found. The following message was given: ${responseJson.get("message")}")
                    }
                }
            })
        }

        fun getLink(player: Player): String? {
            return links[player]
        }
    }

    class Events: Listener {
        @EventHandler
        fun onPlayerQuit(event: PlayerQuitEvent) {
            links.remove(event.player)
        }
    }
}