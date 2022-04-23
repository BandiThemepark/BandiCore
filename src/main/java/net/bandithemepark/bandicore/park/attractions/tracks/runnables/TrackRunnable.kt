package net.bandithemepark.bandicore.park.attractions.tracks.runnables

import org.bukkit.scheduler.BukkitRunnable

class TrackRunnable: BukkitRunnable() {
    private val trackVisualiser = TrackVisualiser()

    override fun run() {
        trackVisualiser.onTick()
    }
}