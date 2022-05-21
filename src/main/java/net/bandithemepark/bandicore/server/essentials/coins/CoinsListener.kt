package net.bandithemepark.bandicore.server.essentials.coins

import com.google.gson.JsonParser
import net.bandithemepark.bandicore.network.mqtt.MQTTListener
import net.bandithemepark.bandicore.server.essentials.ranks.RankManager
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.util.*

class CoinsListener: MQTTListener("/core/coins/trigger") {
    override fun onMessage(message: String) {
        val json = JsonParser.parseString(message).asJsonObject
        val player = Bukkit.getPlayer(UUID.fromString(json.get("uuid").asString))!!
        CoinManager.reloadBalance(player)
    }
}