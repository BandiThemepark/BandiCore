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
import java.io.IOException
import java.util.UUID

object BackendParkour {
    fun saveEntry(playerUUID: UUID, parkourId: String, time: Long, finished: Boolean, callback: (success: Boolean) -> Unit) {
        val client = BandiCore.instance.okHttpClient
        val mediaType = "application/json".toMediaTypeOrNull()

        val json = JsonObject()
        json.addProperty("playerId", playerUUID.toString())
        json.addProperty("parkourId", parkourId)
        json.addProperty("timeToComplete", time)
        json.addProperty("isCompleted", finished)

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/timeseries/parkour")
            .method("POST", json.toString().toRequestBody(mediaType))
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser().parse(response.body!!.string()).asJsonObject
                if(responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    callback.invoke(true)
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at saving the parkour time of $playerUUID, but the attempt failed. The following message was given: ${responseJson.get("message")}")
                    callback.invoke(false)
                }
            }
        })
    }

    fun getTopTenOfParkour(parkourId: String, callback: (data: JsonArray) -> Unit) {
        val client = BandiCore.instance.okHttpClient
        val request = Request.Builder()
            .url("https://api.bandithemepark.net/timeseries/parkour/top/$parkourId")
            .method("GET", null)
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser().parse(response.body!!.string()).asJsonObject
                if (responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    callback.invoke(responseJson.getAsJsonArray("data"))
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at getting the top of the parkour $parkourId, but the attempt failed. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }
}