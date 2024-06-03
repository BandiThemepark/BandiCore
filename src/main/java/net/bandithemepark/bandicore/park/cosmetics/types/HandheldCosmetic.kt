package net.bandithemepark.bandicore.park.cosmetics.types

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.park.cosmetics.Cosmetic
import net.bandithemepark.bandicore.park.cosmetics.CosmeticType
import net.bandithemepark.bandicore.util.ItemFactory
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class HandheldCosmetic: CosmeticType("handheld") {
    var material: Material = Material.DIAMOND_SWORD
    var customModelData = 0
    var colorable = false

    override fun onMetadataLoad(metadata: JsonObject) {
        if(metadata.has("material")) material = Material.matchMaterial(metadata.get("material").asString.uppercase())!!
        if(metadata.has("customModelData")) customModelData = metadata.get("customModelData").asInt
        if(metadata.has("colorable")) colorable = metadata.get("colorable").asBoolean
    }

    override fun isColorable(): Boolean {
        return colorable
    }

    override fun onEquip(player: Player, color: Color?, cosmetic: Cosmetic) {
        val itemStack = if(colorable) ItemFactory(Material.LEATHER_HORSE_ARMOR).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).setArmorColor(color).build()
        else ItemFactory(material).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).build()

        player.inventory.setItem(EquipmentSlot.OFF_HAND, itemStack)
    }

    override fun onUnEquip(player: Player) {
        player.inventory.setItem(EquipmentSlot.OFF_HAND, ItemStack(Material.AIR))
    }

    override fun getDressingRoomItem(player: Player, color: Color?, cosmetic: Cosmetic): ItemStack {
        return if(colorable) ItemFactory(Material.LEATHER_HORSE_ARMOR).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).setArmorColor(color).build()
        else ItemFactory(material).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).build()

    }

    override fun onJoin(player: Player) {
        onUnEquip(player)
    }
}