package net.bandithemepark.bandicore.park.attractions.tracks.splines

import net.bandithemepark.bandicore.park.attractions.tracks.TrackNode

abstract class SplineType {
    abstract fun interpolate(before: TrackNode?, node: TrackNode, connectedTo: TrackNode?, connectedToConnectedTo: TrackNode?): MutableList<TrackNode>
}