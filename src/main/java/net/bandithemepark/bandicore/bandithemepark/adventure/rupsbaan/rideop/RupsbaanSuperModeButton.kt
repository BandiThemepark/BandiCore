package net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.SwitchButton
import org.bukkit.entity.Player

class RupsbaanSuperModeButton: SwitchButton(12, "rideop-rupsbaan-supermode-title", "rideop-rupsbaan-supermode-description") {
    override fun isActivated(): Boolean {
        return (rideOP as RupsbaanRideOP).getSuperMode()
    }

    override fun onClick(player: Player) {
        if(!player.hasPermission("bandithemepark.crew")) return
        if((rideOP as RupsbaanRideOP).rideSchedule.active) return

        (rideOP as RupsbaanRideOP).setSuperMode(!(rideOP as RupsbaanRideOP).getSuperMode())
        rideOP.updateMenu()
    }
}