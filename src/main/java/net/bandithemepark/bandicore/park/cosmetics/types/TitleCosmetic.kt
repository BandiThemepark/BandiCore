package net.bandithemepark.bandicore.park.cosmetics.types

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.park.cosmetics.Cosmetic
import net.bandithemepark.bandicore.park.cosmetics.CosmeticType
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag.Companion.getNameTag
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TitleCosmetic: CosmeticType("title") {
    var text: String? = null

    override fun onMetadataLoad(metadata: JsonObject) {
        if(metadata.has("text")) text = metadata.get("text").asString
    }

    override fun isColorable(): Boolean {
        return false
    }

    override fun onEquip(player: Player, color: Color?, cosmetic: Cosmetic) {
        if(text != null) player.getNameTag()?.title = Util.color(text!!)
    }

    override fun onUnEquip(player: Player) {
        player.getNameTag()?.title = null
    }

    override fun getDressingRoomItem(player: Player, color: Color?, cosmetic: Cosmetic): ItemStack {
        val lore = cosmetic.getItemStackDescription()
        if(text != null) {
            lore.add(Util.color(" "))
            lore.add(Util.color("<${BandiColors.LIGHT_GRAY}>Displays the following text: "))
            lore.add(Util.color(text!!))
        }

        return ItemFactory(Material.NAME_TAG).setDisplayName(cosmetic.getItemName()).setLore(lore).build()
    }

    override fun onJoin(player: Player) {
        onUnEquip(player)
    }
}