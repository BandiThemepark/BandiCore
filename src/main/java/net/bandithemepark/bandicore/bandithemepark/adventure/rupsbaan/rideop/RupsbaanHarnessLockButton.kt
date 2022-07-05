package net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.SwitchButton
import org.bukkit.entity.Player

class RupsbaanHarnessLockButton: SwitchButton(2, "rideop-rupsbaan-harness-title", "rideop-rupsbaan-harness-description") {
    override fun isActivated(): Boolean {
        return (rideOP as RupsbaanRideOP).ride.harnessesLocked
    }

    override fun onClick(player: Player) {
        if((rideOP as RupsbaanRideOP).rideSchedule.active) return

        (rideOP as RupsbaanRideOP).ride.setHarnesses((rideOP as RupsbaanRideOP).ride.harnessesLocked)
        rideOP.updateMenu()
    }
}