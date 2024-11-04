package net.bandithemepark.bandicore.park.attractions

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class AttractionAppearance(val displayName: String, val description: List<String>, val material: Material, val customModelData: Int) {
    fun getItemStack(player: Player, attraction: Attraction): ItemStack? {
        val itemFactory = ItemFactory(material).setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>$displayName")).setCustomModelData(customModelData)

        val lore = mutableListOf<Component>()
        lore.add(Util.color("<!i><${attraction.mode.textColor}>${attraction.mode.text}"))
        description.forEach { lore.add(Util.color("<!i><${BandiColors.LIGHT_GRAY}>$it")) }
        lore.add(Component.text(" "))
        lore.add(Util.color("<!i><${BandiColors.YELLOW}>You have ridden this attraction ${BandiCore.instance.server.ridecounterManager.getRidecountOnOf(player, attraction.id)} times")) // TODO translatable
        lore.add(Util.color("<!i><${BandiColors.YELLOW}>Click here to warp to this attraction")) // TODO Translatable
        itemFactory.setLore(lore)

        if(attraction.mode.glow) {
            itemFactory.addGlow()
        }

        itemFactory.setKeyInPersistentStorage("attraction", attraction.id)
        return itemFactory.build()
    }
}