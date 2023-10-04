package net.bandithemepark.bandicore.util.track

import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.park.attractions.tracks.TrackNode

abstract class TrackFork(val track: TrackLayout, val originNode: TrackNode, val targetNodes: List<TrackNode>) {
    var current = 0

    init {
        //switchTo(0)
    }

    abstract fun switchTo(target: Int)
}