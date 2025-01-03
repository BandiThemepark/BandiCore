package net.bandithemepark.bandicore.server.essentials.coins.boosters

import org.bukkit.entity.Player
import java.util.UUID

data class CoinBooster(
    val id: UUID,
    val name: String,
    val perMinute: Int,
    val durationMillis: Long,
    val activator: Player? = null
) {
}