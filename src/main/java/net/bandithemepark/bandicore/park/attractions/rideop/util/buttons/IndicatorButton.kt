package net.bandithemepark.bandicore.park.attractions.rideop.util.buttons

import net.bandithemepark.bandicore.park.attractions.rideop.RideOPButton
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class IndicatorButton(slot: Int, val titleTranslationId: String, val descriptionTranslationId: String): RideOPButton(slot) {
    override fun onClick(player: Player) {

    }

    override fun getItemStack(player: Player): ItemStack {
        return if(isAvailable()) {
            ItemFactory(Material.PAPER)
                .setCustomModelData(1018)
                .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${player.getTranslatedMessage(titleTranslationId)}"))
                .setLore(mutableListOf(Util.color("<!i><${BandiColors.LIGHT_GRAY}>${player.getTranslatedMessage(descriptionTranslationId)}")))
                .build()
        } else {
            ItemFactory(Material.PAPER)
                .setCustomModelData(1019)
                .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${player.getTranslatedMessage(titleTranslationId)}"))
                .setLore(mutableListOf(Util.color("<!i><${BandiColors.LIGHT_GRAY}>${player.getTranslatedMessage(descriptionTranslationId)}")))
                .build()
        }
    }

    abstract fun isAvailable(): Boolean
}