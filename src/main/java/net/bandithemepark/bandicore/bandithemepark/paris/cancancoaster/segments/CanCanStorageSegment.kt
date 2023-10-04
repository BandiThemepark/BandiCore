package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.segments

import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil

class CanCanStorageSegment: SegmentType("cancanstorage", false, "ID, DISPATCH_SPEED_KMH") {
    var stopped = false
    var dispatched = false
    var currentVehicle: TrackVehicle? = null

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        stopped = false
        dispatched = false
        currentVehicle = vehicle
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        if(!stopped && !TrackUtil.isPastMiddle(parent, vehicle)) {
            stopped = true
            vehicle.speedKMH = 0.0

            val rideOP = RideOP.get("cancancoaster") as CanCanRideOP
            rideOP.resetTransferState()
        }
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
        currentVehicle = null
    }

    fun retrieve() {
        dispatched = true
        currentVehicle!!.speedKMH = metadata[1].toDouble()
    }
}