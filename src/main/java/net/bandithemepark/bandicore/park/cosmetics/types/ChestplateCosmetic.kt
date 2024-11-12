package net.bandithemepark.bandicore.park.cosmetics.types

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.park.cosmetics.Cosmetic
import net.bandithemepark.bandicore.park.cosmetics.CosmeticType
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ChestplateCosmetic: CosmeticType("chestplate") {
    var material: Material = Material.LEATHER_CHESTPLATE
    var customModelData = 0
    var color: Color? = null

    override fun onMetadataLoad(metadata: JsonObject) {
        if(metadata.has("material")) material = Material.matchMaterial(metadata.get("material").asString.uppercase())!!
        if(metadata.has("customModelData")) customModelData = metadata.get("customModelData").asInt
        if(metadata.has("color")) color = Util.hexToColor(metadata.get("color").asString)
    }

    override fun isColorable(): Boolean {
        return false
    }

    override fun onEquip(player: Player, color: Color?, cosmetic: Cosmetic) {
        player.equipment.chestplate = if(color != null) {
            ItemFactory(Material.LEATHER_CHESTPLATE).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).setArmorColor(color).build()
        } else {
            ItemFactory(material).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).build()
        }
    }

    override fun onUnEquip(player: Player) {
        player.equipment.chestplate = null
    }

    override fun getDressingRoomItem(player: Player, color: Color?, cosmetic: Cosmetic): ItemStack {
        return if(color != null) {
            ItemFactory(Material.LEATHER_CHESTPLATE).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).setArmorColor(color).build()
        } else {
            ItemFactory(material).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).build()
        }
    }

    override fun onJoin(player: Player) {
        onUnEquip(player)
    }
}