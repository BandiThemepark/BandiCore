package net.bandithemepark.bandicore.park.attractions.rideop.util.buttons

import net.bandithemepark.bandicore.park.attractions.rideop.RideOPButton
import net.bandithemepark.bandicore.server.translations.LanguageUtil.getTranslatedMessage
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class EStopButton(slot: Int): RideOPButton(slot) {
    override fun getItemStack(player: Player): ItemStack {
        return if(isActive()) {
            ItemFactory(Material.PAPER)
                .setCustomModelData(1012)
                .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${player.getTranslatedMessage("rideop-estop-title")}"))
                .setLore(mutableListOf(Util.color("<!i><${BandiColors.LIGHT_GRAY}>${player.getTranslatedMessage("rideop-estop-description")}")))
                .build()
        } else {
            ItemFactory(Material.PAPER)
                .setCustomModelData(1013)
                .setDisplayName(Util.color("<!i><${BandiColors.YELLOW}>${player.getTranslatedMessage("rideop-estop-title")}"))
                .setLore(mutableListOf(Util.color("<!i><${BandiColors.LIGHT_GRAY}>${player.getTranslatedMessage("rideop-estop-description")}")))
                .build()
        }
    }

    override fun onClick(player: Player) {
        if(player.hasPermission("bandithemepark.crew")) {
            setActive(!isActive())
            rideOP.updateMenu()
        }
    }

    abstract fun setActive(active: Boolean)
    abstract fun isActive(): Boolean
}