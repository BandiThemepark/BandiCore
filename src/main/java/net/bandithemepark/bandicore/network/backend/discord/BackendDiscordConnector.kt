package net.bandithemepark.bandicore.network.backend.discord

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.network.Network
import java.util.UUID

object BackendDiscordConnector {
    suspend fun connect(
        playerUUID: UUID,
        token: String
    ) {
        val json = JsonObject()
        json.addProperty("playerUuid", playerUUID.toString())
        json.addProperty("connectionToken", token)

        val response = Network.post(
            "/discord/connect",
            json,
        )

        val responseJson = JsonParser().parse(response.body!!.string()).asJsonObject
        val message = responseJson.get("message").asString

        when(message.lowercase()) {
            "not found" -> throw PlayerWithUUIDNotFoundException()
            "already exists" -> throw DiscordAlreadyConnectedException()
            "invalid" -> throw DiscordTokenExpiredException()
            "success" -> {}
            else -> throw DiscordConnectFailedException()
        }
    }
}