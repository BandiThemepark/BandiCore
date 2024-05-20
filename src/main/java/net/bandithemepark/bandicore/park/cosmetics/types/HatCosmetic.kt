package net.bandithemepark.bandicore.park.cosmetics.types

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.cosmetics.Cosmetic
import net.bandithemepark.bandicore.park.cosmetics.CosmeticType
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag.Companion.getNameTag
import net.bandithemepark.bandicore.util.ItemFactory
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class HatCosmetic: CosmeticType("hat") {
    var material: Material = Material.DIAMOND_SWORD
    var customModelData = 0
    var colorable = false
    var heightOffset = 0.0

    override fun onMetadataLoad(metadata: JsonObject) {
        if(metadata.has("material")) material = Material.matchMaterial(metadata.get("material").asString.uppercase())!!
        if(metadata.has("customModelData")) customModelData = metadata.get("customModelData").asInt
        if(metadata.has("colorable")) colorable = metadata.get("colorable").asBoolean
        if(metadata.has("heightOffset")) heightOffset = metadata.get("heightOffset").asDouble
    }

    override fun isColorable(): Boolean {
        return colorable
    }

    override fun onEquip(player: Player, color: Color?, cosmetic: Cosmetic) {
        val itemStack = if(colorable) ItemFactory(Material.LEATHER_HORSE_ARMOR).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).setArmorColor(color).build()
                        else ItemFactory(material).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).build()

        player.equipment.helmet = itemStack
        player.getNameTag()!!.heightOffset = heightOffset
    }

    override fun onUnEquip(player: Player) {
        player.equipment.helmet = null

        if(player.getNameTag() != null) {
            player.getNameTag()!!.heightOffset = 0.0
        } else {
            Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
                player.getNameTag()!!.heightOffset = 0.0
            }, 20)
        }
    }

    override fun getDressingRoomItem(player: Player, color: Color?, cosmetic: Cosmetic): ItemStack {
        return if(colorable) ItemFactory(Material.LEATHER_HORSE_ARMOR).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).setArmorColor(color).build()
               else ItemFactory(material).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).build()
    }

    override fun onJoin(player: Player) {
        onUnEquip(player)
    }
}