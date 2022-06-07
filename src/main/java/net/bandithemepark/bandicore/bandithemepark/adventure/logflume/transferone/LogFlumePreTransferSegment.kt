package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.transferone

import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop.LogFlumeRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil

class LogFlumePreTransferSegment: SegmentType("logflumepretransfer", true, "speedKMH") {
    lateinit var rideOP: LogFlumeRideOP
    var needsToStop = false
    var pastMiddle = false
    var stopped = false
    var dispatched = false

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        rideOP = RideOP.get("logflume")!! as LogFlumeRideOP

        vehicle.physicsType = TrackVehicle.PhysicsType.NONE
        vehicle.speedKMH = metadata[0].toDouble()

        needsToStop = !rideOP.isTransferClear()
        pastMiddle = false
        stopped = false
        dispatched = false
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        if(needsToStop) {
            if(!pastMiddle) {
                if(TrackUtil.isPastMiddle(parent, vehicle)) {
                    pastMiddle = true

                    if(!rideOP.isTransferClear()) {
                        vehicle.speed = 0.0
                        stopped = true
                    }
                }
            } else {
                if(stopped && !dispatched) {
                    if(rideOP.isTransferClear()) {
                        dispatched = true
                        vehicle.speedKMH = metadata[0].toDouble()
                    }
                }
            }
        }
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
        vehicle.physicsType = TrackVehicle.PhysicsType.ALL
    }

}