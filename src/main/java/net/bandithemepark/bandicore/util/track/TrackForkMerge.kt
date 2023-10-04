package net.bandithemepark.bandicore.util.track

import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.park.attractions.tracks.TrackNode

class TrackForkMerge(track: TrackLayout, from: List<TrackNode>, to: TrackNode): TrackFork(track, to, from) {

    override fun switchTo(target: Int) {
        val previousNode = targetNodes[current]
        val targetNode = targetNodes[target]
        current = target

        previousNode.disconnect(track)
        targetNode.connectTo(track, originNode)

        track.updateSegments()
    }
}