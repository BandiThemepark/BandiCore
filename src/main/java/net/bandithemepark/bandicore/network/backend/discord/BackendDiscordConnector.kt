package net.bandithemepark.bandicore.network.backend.discord

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import net.bandithemepark.bandicore.BandiCore.Companion.pluginScope
import net.bandithemepark.bandicore.network.Network
import java.util.UUID

object BackendDiscordConnector {
    fun connect(
        playerUUID: UUID,
        token: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val json = JsonObject()
        json.addProperty("playerUuid", playerUUID.toString())
        json.addProperty("connectionToken", token)

        pluginScope.launch {
            val response = Network.post(
                "/discord/connect",
                json,
            )

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
    }
}