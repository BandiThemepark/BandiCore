package net.bandithemepark.bandicore.park.attractions.tracks.segments.types

import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle

class TestLiftHillSegment: SegmentType("testlifthill", true, "SPEED_KMH") {
    var stopped = false

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        stopped = !isNextBlockClear()

        if(stopped) {
            vehicle.speedKMH = 0.0
            vehicle.physicsType = TrackVehicle.PhysicsType.NONE
        }
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        if(stopped) {
            if(isNextBlockClear()) {
                vehicle.speedKMH = metadata[0].toDouble()
                vehicle.physicsType = TrackVehicle.PhysicsType.DOWN
            }
        } else {
            if(vehicle.speedKMH < metadata[0].toDouble()) {
                vehicle.speedKMH = metadata[0].toDouble()
                vehicle.physicsType = TrackVehicle.PhysicsType.DOWN
            }
        }
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
        vehicle.physicsType = TrackVehicle.PhysicsType.ALL
    }
}