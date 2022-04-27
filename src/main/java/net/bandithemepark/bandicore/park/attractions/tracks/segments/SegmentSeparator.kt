package net.bandithemepark.bandicore.park.attractions.tracks.segments

import net.bandithemepark.bandicore.park.attractions.tracks.TrackNode
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle

class SegmentSeparator(var position: TrackPosition, var type: SegmentType?) {
    var curve = listOf<TrackNode>()
    var vehicles = mutableListOf<TrackVehicle>()
    var next: SegmentSeparator? = null
}