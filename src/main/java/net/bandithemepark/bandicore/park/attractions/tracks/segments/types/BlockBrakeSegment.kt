package net.bandithemepark.bandicore.park.attractions.tracks.segments.types

import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil

class BlockBrakeSegment: SegmentType("blockbrake", true, "MAX_SPEED_KMH, TRIM_RATE_KMH/S, RELEASE_SPEED_KMH") {
    var shouldStop = false
    var stopped = false
    var released = false

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        shouldStop = !isNextBlockClear()
        stopped = false
        released = false
        vehicle.physicsType = TrackVehicle.PhysicsType.NONE
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        if(shouldStop && !stopped && isNextBlockClear()) shouldStop = false

        if(vehicle.speedKMH > metadata[0].toDouble()) {
            vehicle.speedKMH -= metadata[1].toDouble() / 20.0
            if(vehicle.speedKMH < metadata[0].toDouble()) vehicle.speedKMH = metadata[0].toDouble()
        }

        if(shouldStop) {
            if(stopped) {
                if(!released) {
                    if(isNextBlockClear()) {
                        vehicle.speedKMH = metadata[2].toDouble()
                        released = true
                    }
                }
            } else {
                if(TrackUtil.isPastMiddle(parent, vehicle)) {
                    vehicle.speed = 0.0
                    stopped = true
                }
            }
        }
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
        shouldStop = false
        vehicle.physicsType = TrackVehicle.PhysicsType.ALL
    }
}