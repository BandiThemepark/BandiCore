package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.actions.types

import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.actions.TrackVehicleAction

class AccelerateAction private constructor(vehicle: TrackVehicle, var acceleration: Double, var accelerationTime: Int): TrackVehicleAction(vehicle) {
    override fun update() {
        if(tick >= accelerationTime) deSchedule = true
        parent.speed += acceleration
    }

    companion object {
        fun to(vehicle: TrackVehicle, target: Double, inTime: Int) {
            val dif = (target - vehicle.speedKMH)/72.0
            vehicle.actions.add(AccelerateAction(vehicle, dif / inTime, inTime))
        }
    }
}