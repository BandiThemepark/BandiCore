package net.bandithemepark.bandicore.park.attractions.tracks.runnables

import org.bukkit.scheduler.BukkitRunnable

class TrackRunnable: BukkitRunnable() {
    private val trackVisualiser = TrackVisualiser()
    private val trackVehicleUpdater = TrackVehicleUpdater()

    override fun run() {
        trackVisualiser.onTick()
        trackVehicleUpdater.onTick()
    }
}