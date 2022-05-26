package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.segments

import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle

class LogflumeDropSegment: SegmentType("logflumedrop", false, "gravityMultiplier") {
    val previousMultiplier = hashMapOf<TrackVehicle, Double>()

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        previousMultiplier[vehicle] = vehicle.gravityMultiplier
        vehicle.gravityMultiplier = metadata[0].toDouble()
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {

    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
        vehicle.gravityMultiplier = previousMultiplier[vehicle]!!
        previousMultiplier.remove(vehicle)
    }
}