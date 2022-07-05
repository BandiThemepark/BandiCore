package net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.NormalButton
import org.bukkit.entity.Player

class RupsbaanPushDownAllButton: NormalButton(3, "rideop-rupsbaan-push-title", "rideop-rupsbaan-push-description") {
    override fun isAvailable(): Boolean {
        if(!(rideOP as RupsbaanRideOP).ride.harnessesLocked) return false

        for(cart in (rideOP as RupsbaanRideOP).ride.carts) {
            if(cart.harnessPosition != 0.0 && cart.currentProgressDown >= 30) return true
        }

        return false
    }

    override fun onClick(player: Player) {
        for(cart in (rideOP as RupsbaanRideOP).ride.carts) {
            cart.startDownwardsInterpolation()
        }
        rideOP.updateMenu()
    }
}