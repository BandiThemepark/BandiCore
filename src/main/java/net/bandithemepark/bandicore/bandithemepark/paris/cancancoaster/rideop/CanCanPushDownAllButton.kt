package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop

import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.NormalButton
import org.bukkit.entity.Player

class CanCanPushDownAllButton: NormalButton(3, "rideop-rupsbaan-push-title", "rideop-rupsbaan-push-description") {
    override fun isAvailable(): Boolean {
        val rideOP = rideOP as CanCanRideOP

        if(!rideOP.harnessesLocked) return false

        for(harness in rideOP.getAllHarnesses()) {
            if(harness.harnessPosition != 0.0 && harness.currentProgress >= 30) return true
        }

        return false
    }

    override fun onClick(player: Player) {
        if(!isAvailable()) return

        val rideOP = rideOP as CanCanRideOP
        for(harness in rideOP.getAllHarnesses()) {
            if(harness.harnessPosition != 0.0 && harness.currentProgress >= 30) {
                harness.startDownwardsInterpolation()
            }
        }

        rideOP.updateMenu()
    }
}