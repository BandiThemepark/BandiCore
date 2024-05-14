package net.bandithemepark.bandicore.network.backend

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.BandiCore
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.bukkit.entity.Player
import java.io.IOException

object BackendCosmetic {
    fun getAll(callback: (JsonArray) -> Unit) {
        val client = BandiCore.instance.okHttpClient

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/cosmetics&limit=10000")
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
                    val data = responseJson.getAsJsonObject("cosmetics").getAsJsonArray("data")
                    callback.invoke(data)
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at loading all cosmetics, but no data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }

    fun getOwned(player: Player, callback: (JsonArray) -> Unit) {
        val client = BandiCore.instance.okHttpClient

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/cosmetics/GetPlayerCosmetics/${player.uniqueId}")
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
                    BandiCore.instance.logger.severe("An attempt was made at loading owned cosmetics of player ${player.name}, but no data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }
}