package net.bandithemepark.bandicore.util.track

import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.park.attractions.tracks.TrackNode

class TrackForkSplit(track: TrackLayout, from: TrackNode, to: List<TrackNode>): TrackFork(track, from, to) {

    override fun switchTo(target: Int) {
        originNode.connectTo(track, targetNodes[target])
        track.updateSegments()
        current = target
    }
}