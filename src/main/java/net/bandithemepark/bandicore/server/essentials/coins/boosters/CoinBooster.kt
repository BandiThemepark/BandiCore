package net.bandithemepark.bandicore.server.essentials.coins.boosters

import com.google.gson.JsonObject
import org.bukkit.entity.Player
import java.util.UUID

data class CoinBooster(
    val id: UUID,
    val name: String,
    val perMinute: Int,
    val durationMillis: Long,
    val activator: Player? = null
) {
    var timeLeft: Long = durationMillis

    fun toJson(): JsonObject {
        val json = JsonObject()

        json.addProperty("id", id.toString())
        json.addProperty("name", name)
        json.addProperty("perMinute", perMinute)
        json.addProperty("durationMillis", durationMillis)
        json.addProperty("timeLeft", timeLeft)
        if(activator != null) json.addProperty("activator", activator.uniqueId.toString())

        return json
    }
}