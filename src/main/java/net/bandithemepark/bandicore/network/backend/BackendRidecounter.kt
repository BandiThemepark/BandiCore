package net.bandithemepark.bandicore.network.backend

import com.google.gson.JsonArray
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
import java.io.IOException

class BackendRidecounter {
    companion object {
        fun getAllOfPlayer(player: Player, callback: (JsonArray) -> Unit) {
            val client = BandiCore.instance.okHttpClient

            val request = Request.Builder()
                .url("https://api.bandithemepark.net/ridecounters/player/${player.uniqueId}")
                .method("GET", null)
                .header("Authorization", BandiCore.instance.server.apiKey)
                .build()

            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseJson = JsonParser().parse(response.body!!.string()).asJsonObject
                    if(responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                        val data = responseJson.getAsJsonArray("data")
                        callback.invoke(data)
                    } else {
                        BandiCore.instance.logger.severe("An attempt was made at retrieving ridecounters of player ${player.name}, but no data was found. The following message was given: ${responseJson.get("message")}")
                    }
                }
            })
        }

        fun getTopOfRide(rideId: String, callback: (JsonArray) -> Unit) {
            val client = BandiCore.instance.okHttpClient

            val request = Request.Builder()
                .url("https://api.bandithemepark.net/ridecounters/leaderboard/$rideId")
                .method("GET", null)
                .header("Authorization", BandiCore.instance.server.apiKey)
                .build()

            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseJson = JsonParser().parse(response.body!!.string()).asJsonObject
                    if(responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                        val data = responseJson.getAsJsonArray("data")
                        callback.invoke(data)
                    } else {
                        BandiCore.instance.logger.severe("An attempt was made at retrieving top ridecounters of ride $rideId, but no data was found. The following message was given: ${responseJson.get("message")}")
                    }
                }
            })
        }

        fun increase(player: Player, rideId: String, callback: (JsonObject) -> Unit) {
            val client = BandiCore.instance.okHttpClient
            val mediaType = "application/json".toMediaTypeOrNull()

            val request = Request.Builder()
                .url("https://api.bandithemepark.net/ridecounters/increase/${player.uniqueId}/$rideId")
                .method("POST", "{}".toRequestBody(mediaType))
                .header("Authorization", BandiCore.instance.server.apiKey)
                .build()

            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseJson = JsonParser().parse(response.body!!.string()).asJsonObject
                    if(responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                        val data = responseJson.getAsJsonObject("data")
                        callback.invoke(data)
                    } else {
                        BandiCore.instance.logger.severe("An attempt was made at increase the ridecounter of ${player.name} on ride $rideId, but no data was found. The following message was given: ${responseJson.get("message")}")
                    }
                }
            })
        }
    }
}