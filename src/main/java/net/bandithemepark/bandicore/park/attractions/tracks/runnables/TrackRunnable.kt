package net.bandithemepark.bandicore.park.attractions.tracks.runnables

import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import org.bukkit.scheduler.BukkitRunnable

class TrackRunnable: BukkitRunnable() {
    private val rideOpTimer = RideOP.Timer()
    private val trackVisualiser = TrackVisualiser()
    private val trackVehicleUpdater = TrackVehicleUpdater()
    private val trackActionUpdater = TrackVehicleActionUpdater()

    override fun run() {
        rideOpTimer.onTick()
        trackVisualiser.onTick()
        trackVehicleUpdater.onTick()
        trackActionUpdater.onTick()
    }
}