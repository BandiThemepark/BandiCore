package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop

import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.DispatchButton
import org.bukkit.entity.Player

class LogFlumeDispatchButton: DispatchButton(4) {
    override fun onDispatch(player: Player) {
        val rideOP = RideOP.get("logflume") as LogFlumeRideOP
        rideOP.station.dispatch()
        rideOP.dispatchDelay = 20
    }

    override fun isAvailable(): Boolean {
        val rideOP = RideOP.get("logflume") as LogFlumeRideOP
        val gatesButton = rideOP.loadedPages[0].loadedButtons.find { it is LogFlumeGatesButton } as LogFlumeGatesButton
        val harnessButton = rideOP.loadedPages[0].loadedButtons.find { it is LogFlumeHarnessButton } as LogFlumeHarnessButton
        return rideOP.dispatchDelay == 0 && !rideOP.layout.eStop && rideOP.station.currentStopped != null && !gatesButton.open && !harnessButton.open
    }
}