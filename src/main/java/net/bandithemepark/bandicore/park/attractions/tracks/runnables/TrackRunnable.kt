package net.bandithemepark.bandicore.park.attractions.tracks.runnables

import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.util.coroutines.Scheduler

class TrackRunnable {
    private val rideOpTimer = RideOP.Timer()
    private val trackVisualiser = TrackVisualiser()
    private val trackVehicleUpdater = TrackVehicleUpdater()
    private val trackActionUpdater = TrackVehicleActionUpdater()

    private fun run() {
        rideOpTimer.onTick()
        trackVisualiser.onTick()
        trackVehicleUpdater.onTick()
        trackActionUpdater.onTick()
    }

    fun startTimer() {
        Scheduler.loopAsync(50) {
            run()
        }
    }
}