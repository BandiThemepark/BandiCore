package net.bandithemepark.bandicore.park.cosmetics.types

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.park.cosmetics.Cosmetic
import net.bandithemepark.bandicore.park.cosmetics.CosmeticType
import net.bandithemepark.bandicore.park.cosmetics.balloons.Balloon
import net.bandithemepark.bandicore.park.cosmetics.balloons.BalloonTrailPart
import net.bandithemepark.bandicore.util.ItemFactory
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BalloonCosmetic: CosmeticType("balloon") {
    var material: Material = Material.DIAMOND_SWORD
    var customModelData = 0
    var colorable = false
    var trailParts = hashMapOf<ItemStack, Double>()

    override fun onMetadataLoad(metadata: JsonObject) {
        if(metadata.has("material")) material = Material.matchMaterial(metadata.get("material").asString.uppercase())!!
        if(metadata.has("customModelData")) customModelData = metadata.get("customModelData").asInt
        if(metadata.has("colorable")) colorable = metadata.get("colorable").asBoolean

        if(metadata.has("trail")) {
            val array = metadata.getAsJsonArray("trail")

            for(i in 0 until array.size()) {
                val obj = array.get(i).asJsonObject
                val trailMaterial = if(obj.has("material")) Material.matchMaterial(obj.get("material").asString.uppercase())!! else Material.DIAMOND_SWORD
                val trailModelData = if(obj.has("customModelData")) obj.get("customModelData").asInt else 0
                val item = ItemFactory(trailMaterial).setCustomModelData(trailModelData).build()
                val length = if(obj.has("length")) obj.get("length").asDouble else 1.0
                trailParts[item] = length
            }
        }
    }

    override fun isColorable(): Boolean {
        return colorable
    }

    override fun onEquip(player: Player, color: Color?, cosmetic: Cosmetic) {
        val balloon = Balloon(ItemFactory(material).setCustomModelData(customModelData).build(), player.world, player)

        balloon.trailParts.clear()
        for((item, length) in trailParts) {
            balloon.trailParts.add(BalloonTrailPart(item, player.world, length))
        }
        
        balloon.spawn(balloon.getPlayerAttachmentPosition(player))
        balloons[player] = balloon
    }

    override fun onUnEquip(player: Player) {
        balloons[player]?.deSpawn()
        balloons.remove(player)
    }

    override fun getDressingRoomItem(player: Player, color: Color?, cosmetic: Cosmetic): ItemStack {
        return if(colorable) ItemFactory(Material.LEATHER_HORSE_ARMOR).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).setArmorColor(color).build()
        else ItemFactory(material).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).build()
    }

    override fun onJoin(player: Player) {

    }

    companion object {
        val balloons = hashMapOf<Player, Balloon>()

        fun Player.getBalloon(): Balloon? {
            return balloons[this]
        }
    }
}