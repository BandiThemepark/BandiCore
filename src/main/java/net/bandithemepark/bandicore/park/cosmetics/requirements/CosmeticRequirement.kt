package net.bandithemepark.bandicore.park.cosmetics.requirements

import com.google.gson.JsonObject
import org.bukkit.entity.Player

class CosmeticRequirement(val type: CosmeticRequirementType, val settings: String) {
    fun check(player: Player): Boolean {
        return type.check(player, settings)
    }

    companion object {
        fun fromJson(json: JsonObject): CosmeticRequirement {
            val type = CosmeticRequirementType.getType(json.get("type").asString)!!
            val settings = if(json.has("settings")) json.get("settings").asString else ""
            return CosmeticRequirement(type, settings)
        }
    }
}