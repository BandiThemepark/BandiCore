package net.bandithemepark.bandicore.park.attractions.tracks.segments.types

import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil

class TestSegment: SegmentType("test", true, "DISPATCH_SPEED_KMH") {
    var pastMiddle = false

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        vehicle.physicsType = TrackVehicle.PhysicsType.NONE

        pastMiddle = false
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        if(pastMiddle) {
            if(isNextBlockClear()) {
                vehicle.speedKMH = metadata[0].toDouble()
            }
        } else {
            if(TrackUtil.isPastMiddle(parent, vehicle)) {
                pastMiddle = true
                vehicle.speed = 0.0
            }
        }
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
        vehicle.physicsType = TrackVehicle.PhysicsType.ALL
    }
}