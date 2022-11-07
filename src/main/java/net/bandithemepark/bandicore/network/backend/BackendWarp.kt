package net.bandithemepark.bandicore.network.backend

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.essentials.warps.Warp
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.bukkit.Bukkit
import java.io.IOException

class BackendWarp(val warp: Warp) {
    fun create(callback: (JsonObject) -> Unit) {
        val data = JsonObject()
        data.addProperty("name", warp.name)
        data.addProperty("world", warp.location.world!!.name)
        data.addProperty("x", warp.location.x)
        data.addProperty("y", warp.location.y)
        data.addProperty("z", warp.location.z)
        data.addProperty("pitch", warp.location.pitch)
        data.addProperty("yaw", warp.location.yaw)
        data.addProperty("permission", warp.permission)

        val client = BandiCore.instance.okHttpClient
        val mediaType = "application/json".toMediaTypeOrNull()

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/warps/")
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
                    val returnData = responseJson.getAsJsonObject("data")
                    callback.invoke(returnData)
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at creating a warp named ${warp.name}, but no response data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }

    fun delete(callback: () -> Unit) {
        val request = Request.Builder()
            .url("https://api.bandithemepark.net/warps?id=${warp.uuid}")
            .delete(null)
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        val client = BandiCore.instance.okHttpClient

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser().parse(response.body!!.string()).asJsonObject
                if (responseJson.has("message") && responseJson.get("message").asString == "success") {
                    callback.invoke()
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at deleting warp ${warp.name}, but the attempt failed. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }

    fun update(callback: (JsonObject) -> Unit) {
        val data = JsonObject()
        data.addProperty("name", warp.name)
        data.addProperty("world", warp.location.world!!.name)
        data.addProperty("x", warp.location.x)
        data.addProperty("y", warp.location.y)
        data.addProperty("z", warp.location.z)
        data.addProperty("pitch", warp.location.pitch)
        data.addProperty("yaw", warp.location.yaw)
        data.addProperty("permission", warp.permission)

        val client = BandiCore.instance.okHttpClient
        val mediaType = "application/json".toMediaTypeOrNull()

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/warps?id=${warp.uuid}")
            .method("PUT", data.toString().toRequestBody(mediaType))
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser().parse(response.body!!.string()).asJsonObject
                if (responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    val returnData = responseJson.getAsJsonObject("data")
                    callback.invoke(returnData)
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at updating warp ${warp.name}, but no response data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }

    companion object {
        fun getAllWarpsData(callback: (JsonArray) -> Unit) {
            val client = BandiCore.instance.okHttpClient

            val request = Request.Builder()
                .url("https://api.bandithemepark.net/warps")
                .get()
                .header("Authorization", BandiCore.instance.server.apiKey)
                .build()

            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseJson = JsonParser().parse(response.body!!.string()).asJsonObject
                    if (responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                        val returnData = responseJson.getAsJsonArray("data")
                        callback.invoke(returnData)
                    } else {
                        BandiCore.instance.logger.severe("An attempt was made at getting all warps, but no response data was found. The following message was given: ${responseJson.get("message")}")
                    }
                }
            })
        }
    }
}