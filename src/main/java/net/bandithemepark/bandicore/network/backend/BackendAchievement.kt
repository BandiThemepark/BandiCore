package net.bandithemepark.bandicore.network.backend

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.achievements.Achievement
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.bukkit.entity.Player
import java.io.IOException

object BackendAchievement {
    fun getAllCategories(callback: (JsonArray) -> Unit) {
        val client = BandiCore.instance.okHttpClient

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/achievements/categories")
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
                    BandiCore.instance.logger.severe("An attempt was made at loading all achievement categories, but no data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }

    fun getOwnedOf(player: Player, callback: (JsonArray) -> Unit) {
        val client = BandiCore.instance.okHttpClient

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/achievements/players/${player.uniqueId}")
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
                    BandiCore.instance.logger.severe("An attempt was made at loading all achievements of player ${player.name}, but no data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }

    fun giveAchievement(player: Player, achievement: Achievement, callback: () -> Unit) {
        val client = BandiCore.instance.okHttpClient
        val mediaType = "application/json".toMediaTypeOrNull()

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/achievements/grant/${player.uniqueId}/${achievement.id}")
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
                    callback.invoke()
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at giving ${player.name} the achievement ${achievement.displayName}, but no data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }
}