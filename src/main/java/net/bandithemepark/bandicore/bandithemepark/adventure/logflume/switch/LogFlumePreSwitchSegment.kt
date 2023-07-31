package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.switch

import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop.LogFlumeRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil

class LogFlumePreSwitchSegment: SegmentType("logflumepreswitch", true, "speedKMH") {
    lateinit var rideOP: LogFlumeRideOP
    var needsToStop = false
    var pastMiddle = false
    var stopped = false
    var dispatched = false

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        rideOP = RideOP.get("logflume")!! as LogFlumeRideOP

        vehicle.physicsType = TrackVehicle.PhysicsType.NONE
        vehicle.speedKMH = -metadata[0].toDouble()

        needsToStop = !rideOP.isSwitchClear()
        pastMiddle = false
        stopped = false
        dispatched = false

        if(!needsToStop) rideOP.vehicleMovingOnToSwitch = true
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        if(needsToStop) {
            if(!pastMiddle) {
                if(!TrackUtil.isPastMiddle(parent, vehicle)) {
                    pastMiddle = true

                    if(!rideOP.isSwitchClear()) {
                        vehicle.speed = 0.0
                        stopped = true
                    }
                }
            } else {
                if(stopped && !dispatched) {
                    if(rideOP.isSwitchClear()) {
                        dispatched = true
                        vehicle.speedKMH = -metadata[0].toDouble()
                        rideOP.vehicleMovingOnToSwitch = true
                        rideOP.updateMenu()
                    }
                }
            }
        }
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {

    }
}