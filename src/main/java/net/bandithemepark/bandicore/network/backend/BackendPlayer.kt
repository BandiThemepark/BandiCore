package net.bandithemepark.bandicore.network.backend

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.BandiCore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.io.IOException
import java.util.Date
import java.util.UUID

class BackendPlayer(val player: OfflinePlayer) {
    /**
     * Gets the player's data.
     * @param callback The callback to be called when the request is complete.
     */
    fun get(callback: (JsonObject) -> Unit) {
        val client = BandiCore.instance.okHttpClient

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/players/${player.uniqueId.toString()}")
            .method("GET", null)
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser.parseString(response.body!!.string()).asJsonObject
                if(responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    val data = responseJson.getAsJsonObject("data")
                    callback.invoke(data)
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at retrieving data of player ${player.name}, but no data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }

    /**
     * Updates data about the player.
     * @param updatePlayerName Whether to update the player's name
     * @param updateCoins Whether to update the player's coins
     * @param coins What to set the coins to if they are updated. Set to whatever you want if you don't want to update the coins
     * @param rank The rank to set the player to. Set to null to do nothing
     * @param discordId The discord id to set the player to. Set to null to do nothing
     * @param lang The language to set the player to. Set to null to do nothing
     * @param updateLastJoined Whether to update the player's last joined date
     * @param callback What to do when the update is complete, with the player's data
     */
    fun updatePlayer(updatePlayerName: Boolean = false, updateCoins: Boolean = false, coins: Int, rank: String?, discordId: String?, lang: String?, updateLastJoined: Boolean = false, callback: (JsonObject) -> Unit) {
        val data = JsonObject()
        if(updatePlayerName) data.addProperty("playername", player.name)
        if(updateCoins) data.addProperty("coins", coins)
        if(rank != null) data.addProperty("rank", rank)
        if(discordId != null) data.addProperty("discordId", discordId)
        if(lang != null) data.addProperty("lang", lang)
        if(updateLastJoined) data.addProperty("lastJoined", Date().toString())

        val client = BandiCore.instance.okHttpClient
        val mediaType = "application/json".toMediaTypeOrNull()

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/players/${player.uniqueId.toString()}")
            .method("PUT", data.toString().toRequestBody(mediaType))
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser.parseString(response.body!!.string()).asJsonObject
                 if (responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    val returnData = responseJson.getAsJsonObject("data")
                     callback.invoke(returnData)
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at changing data of player ${player.name}, but no response data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }

    /**
     * Updates data about the player.
     * @param data JSON data to set. You can find available data on the documentation
     * @param callback What to do when the update is complete, with the player's data
     */
    fun updatePlayer(data: JsonObject, callback: (JsonObject) -> Unit) {
        val client = BandiCore.instance.okHttpClient
        val mediaType = "application/json".toMediaTypeOrNull()

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/players/${player.uniqueId.toString()}")
            .method("PUT", data.toString().toRequestBody(mediaType))
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser.parseString(response.body!!.string()).asJsonObject
                if (responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    val returnData = responseJson.getAsJsonObject("data")
                    callback.invoke(returnData)
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at changing data of player ${player.name}, but no response data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }
}