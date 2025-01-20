package net.bandithemepark.bandicore.network.backend.discord

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

object BackendDiscordConnector {
    fun connect(
        playerUUID: UUID,
        token: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val client = BandiCore.instance.okHttpClient
        val mediaType = "application/json".toMediaTypeOrNull()

        val json = JsonObject()
        json.addProperty("playerUuid", playerUUID.toString())
        json.addProperty("connectionToken", token)

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/discord/connect")
            .method("POST", json.toString().toRequestBody(mediaType))
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser().parse(response.body!!.string()).asJsonObject

                val message = responseJson.get("message").asString
                when(message.lowercase()) {
                    "not found" -> onError(PlayerWithUUIDNotFoundException())
                    "already exists" -> onError(DiscordAlreadyConnectedException())
                    "invalid" -> onError(DiscordTokenExpiredException())
                    "success" -> onSuccess.invoke()
                    else -> onError(DiscordConnectFailedException())
                }
            }
        })
    }
}