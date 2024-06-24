package net.bandithemepark.bandicore.park.shops

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.cosmetics.Cosmetic
import net.bandithemepark.bandicore.server.essentials.warps.Warp
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

class Shop(
    val id: UUID,
    val name: String,
    val displayName: String,
    val icon: ItemStack,
    val warp: Warp?,
    val cosmetics: List<Cosmetic>
) {
    companion object {
        fun fromJson(json: JsonObject): Shop {
            val id = UUID.fromString(json.get("id").asString)
            val name = json.get("name").asString
            val displayName = json.get("displayName").asString

            val iconData = JsonParser().parse(json.get("iconData").asString).asJsonObject
            val itemFactory = ItemFactory(Material.matchMaterial(iconData.get("material").asString.uppercase()))
            if(iconData.has("customModelData")) itemFactory.setCustomModelData(iconData.get("customModelData").asInt)

            var warp: Warp? = null
            if(json.has("warp")) warp = BandiCore.instance.server.warpManager.warps.find { it.uuid === UUID.fromString(json.getAsJsonObject("warp").get("id").asString) }

            val cosmeticsArray = json.get("cosmetics").asJsonArray
            val cosmetics = mutableListOf<Cosmetic>()
            for(element in cosmeticsArray) {
                val cosmetic = BandiCore.instance.cosmeticManager.cosmetics.find { it.id.toString() == element.asJsonObject.get("id").asString }
                if(cosmetic != null) cosmetics.add(cosmetic)
                else BandiCore.instance.logger.warning("Shop $name has a cosmetic that does not seem to exist (ID ${element.asJsonObject.get("id").asString})")
            }

            return Shop(id, name, displayName, itemFactory.build(), warp, cosmetics)
        }
    }
}