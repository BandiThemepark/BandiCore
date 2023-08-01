package net.bandithemepark.bandicore.park.cosmetics.types

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.park.cosmetics.Cosmetic
import net.bandithemepark.bandicore.park.cosmetics.CosmeticType
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag.Companion.getNameTag
import net.bandithemepark.bandicore.util.ItemFactory
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player

class HatCosmetic: CosmeticType("hat") {
    lateinit var material: Material
    var customModelData = 0
    var colorable = false
    var heightOffset = 0.0

    override fun onMetadataLoad(metadata: JsonObject) {
        material = Material.matchMaterial(metadata.get("material").asString.uppercase())!!
        customModelData = metadata.get("customModelData").asInt
        colorable = metadata.get("colorable").asBoolean
        heightOffset = metadata.get("heightOffset").asDouble
    }

    override fun isColorable(): Boolean {
        return colorable
    }

    override fun onEquip(player: Player, color: Color?, cosmetic: Cosmetic) {
        val itemStack = if(colorable) ItemFactory(material).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).setArmorColor(color).build()
                        else ItemFactory(material).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).build()

        player.equipment.helmet = itemStack
        player.getNameTag()!!.heightOffset = heightOffset
    }

    override fun onUnEquip(player: Player) {
        player.equipment.helmet = null
        player.getNameTag()!!.heightOffset = 0.0
    }
}