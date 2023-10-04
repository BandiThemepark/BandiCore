package net.bandithemepark.bandicore.park.attractions.tracks.segments.types

import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle

class TrimBrakeSegment: SegmentType("trimbrake", false, "MAX_SPEED_KMH, TRIM_RATE_KMH/S") {
    override fun onVehicleEnter(vehicle: TrackVehicle) {

    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        if(vehicle.speedKMH > metadata[0].toDouble()) {
            vehicle.speedKMH -= metadata[1].toDouble() / 20.0
            if(vehicle.speedKMH < metadata[0].toDouble()) vehicle.speedKMH = metadata[0].toDouble()
        }
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {

    }
}