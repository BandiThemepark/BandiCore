package net.bandithemepark.bandicore.park.attractions.tracks.segments.types

import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil

class TestBrakeSegment: SegmentType("testbrake", true, "RELEASE_SPEED_KMH") {
    private var shouldStop = false
    private var pastMiddle = false
    private var proceed = false

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        shouldStop = !isNextBlockClear()
        pastMiddle = false
        proceed = false

        vehicle.speedKMH = metadata[0].toDouble()
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        if(shouldStop) {
            if(pastMiddle) {
                if(!proceed && isNextBlockClear()) {
                    proceed = true
                    vehicle.speedKMH = metadata[0].toDouble()
                }
            } else {
                if(TrackUtil.isPastMiddle(parent, vehicle)) {
                    pastMiddle = true
                    vehicle.speed = 0.0
                }
            }
        }
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {

    }
}