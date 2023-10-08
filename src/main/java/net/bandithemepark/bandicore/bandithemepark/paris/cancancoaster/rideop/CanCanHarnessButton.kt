package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.SimpleHarnessButton
import net.bandithemepark.bandicore.util.TrackUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class CanCanHarnessButton: SimpleHarnessButton(2) {
    override fun setOpen() {
        val rideOP = rideOP as CanCanRideOP
        rideOP.harnessesLocked = false
        rideOP.updateLockedState()
        rideOP.getAllHarnesses().forEach { it.startUpwardsInterpolation() }
    }

    override fun setClosed() {
        val rideOP = rideOP as CanCanRideOP
        rideOP.harnessesLocked = true
        rideOP.updateLockedState()

        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
            if(rideOP.operator != null) {
                rideOP.getAllHarnesses().forEach {
                    if (it.spawned) it.markFor(rideOP.operator!!)
                }
            }
        }, 2)
    }

    override fun canOpen(): Boolean {
        val rideOP = rideOP as CanCanRideOP

        if(!rideOP.isTrainInStation()) return false
        if(rideOP.transferMode) return false

        return true
    }
}