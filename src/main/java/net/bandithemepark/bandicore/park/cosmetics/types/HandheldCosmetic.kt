package net.bandithemepark.bandicore.park.cosmetics.types

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.park.cosmetics.Cosmetic
import net.bandithemepark.bandicore.park.cosmetics.CosmeticType
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.ItemFactory.Companion.getPersistentData
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.EquipmentSlot
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
        val itemStack = if(colorable) ItemFactory(Material.LEATHER_HORSE_ARMOR).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).setArmorColor(color).setKeyInPersistentStorage("handheld", cosmetic.name).build()
        else ItemFactory(material).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).setKeyInPersistentStorage("handheld", cosmetic.name).build()

        player.inventory.setItem(EquipmentSlot.OFF_HAND, itemStack)
    }

    override fun onUnEquip(player: Player) {
        player.inventory.setItem(EquipmentSlot.OFF_HAND, ItemStack(Material.AIR))
    }

    override fun getDressingRoomItem(player: Player, color: Color?, cosmetic: Cosmetic): ItemStack {
        return if(colorable) ItemFactory(Material.LEATHER_HORSE_ARMOR).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).setArmorColor(color).setKeyInPersistentStorage("handheld", cosmetic.name).build()
        else ItemFactory(material).setCustomModelData(customModelData).setDisplayName(cosmetic.getItemName()).setLore(cosmetic.getItemStackDescription()).setKeyInPersistentStorage("handheld", cosmetic.name).build()
    }

    override fun onJoin(player: Player) {
        onUnEquip(player)
    }

    class Events: Listener {
        @EventHandler
        fun onPlayerPunch(event: EntityDamageByEntityEvent) {
            if(event.entity !is Player || event.damager !is Player) return
            if((event.damager as Player).inventory.itemInMainHand.getPersistentData("handheld") != "ban_hammer") return

            Bukkit.getOnlinePlayers().forEach { it.playSound(it.location, "custom.ban", SoundCategory.MASTER, 1f, 1f) }
            event.entity.world.strikeLightning(event.entity.location)
        }
    }
}