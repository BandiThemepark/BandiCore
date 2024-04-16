package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.segments

import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class CanCanFinalBrakeSegment: SegmentType("cancanfinalblockbrake", true, "MAX_SPEED_KMH, TRIM_RATE_KMH/S, RELEASE_SPEED_KMH") {
    var shouldStop = false
    var stopped = false
    var released = false

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        shouldStop = !isNextBlockClear() || (RideOP.get("cancancoaster")!! as CanCanRideOP).transferMode
        stopped = false
        released = false
        if(!shouldStop) (RideOP.get("cancancoaster")!! as CanCanRideOP).dispatchedFromFinal = true
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        if(shouldStop && !stopped && isNextBlockClear() && !(RideOP.get("cancancoaster")!! as CanCanRideOP).transferMode) shouldStop = false

        if(vehicle.speedKMH > metadata[0].toDouble()) {
            vehicle.speedKMH -= metadata[1].toDouble() / 20.0
            if(vehicle.speedKMH < metadata[0].toDouble()) vehicle.speedKMH = metadata[0].toDouble()
        }

        if(shouldStop) {
            if(stopped) {
                if(!released) {
                    if(isNextBlockClear() && !(RideOP.get("cancancoaster")!! as CanCanRideOP).transferMode) {
                        vehicle.speedKMH = metadata[2].toDouble()
                        (RideOP.get("cancancoaster")!! as CanCanRideOP).dispatchedFromFinal = true
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
    }
}