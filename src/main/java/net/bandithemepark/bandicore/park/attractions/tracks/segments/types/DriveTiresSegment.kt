package net.bandithemepark.bandicore.park.attractions.tracks.segments.types

import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle

class DriveTiresSegment: SegmentType("drivetires", false, "SPEED_KMH") {
    override fun onVehicleEnter(vehicle: TrackVehicle) {
        if(vehicle.speedKMH < metadata[0].toDouble()) {
            vehicle.speedKMH = metadata[0].toDouble()
            vehicle.physicsType = TrackVehicle.PhysicsType.NONE
        }
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        if(vehicle.speedKMH < metadata[0].toDouble()) {
            vehicle.speedKMH = metadata[0].toDouble()
            vehicle.physicsType = TrackVehicle.PhysicsType.NONE
        }
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
        vehicle.physicsType = TrackVehicle.PhysicsType.ALL
    }
}