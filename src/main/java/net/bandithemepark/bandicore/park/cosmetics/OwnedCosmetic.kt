package net.bandithemepark.bandicore.park.cosmetics

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import org.bukkit.Color
import java.util.*

data class OwnedCosmetic(val cosmetic: Cosmetic, var equipped: Boolean, val amount: Int, var color: Color?) {
    companion object {
        fun fromJson(json: JsonObject): OwnedCosmetic {
            val cosmeticId = UUID.fromString(json.get("cosmeticId").asString)
            val equipped = json.get("equipped").asBoolean
            val amount = json.get("amount").asInt
            val color = if(json.get("color").asInt != 0) Color.fromRGB(json.get("color").asInt) else null

            val cosmetic = BandiCore.instance.cosmeticManager.cosmetics.find { it.id == cosmeticId }!!

            return OwnedCosmetic(cosmetic, equipped, amount, color)
        }
    }
}