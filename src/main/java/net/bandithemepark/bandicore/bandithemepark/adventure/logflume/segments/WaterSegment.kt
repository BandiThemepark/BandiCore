package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.segments

import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle

class WaterSegment: SegmentType("water", false, "waterSpeed, brakeSpeed (Default 1.0)") {
    override fun onVehicleEnter(vehicle: TrackVehicle) {
        vehicle.physicsType = TrackVehicle.PhysicsType.NONE
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        val waterSpeed = metadata[0].toDouble()

        if(vehicle.speed >= 0) {
            if(vehicle.speedKMH == waterSpeed) return

            if(vehicle.speedKMH < waterSpeed) {
                vehicle.speedKMH += 0.04
                if(vehicle.speedKMH > waterSpeed) vehicle.speedKMH = waterSpeed
            } else {
                vehicle.speedKMH -= metadata[1].toDouble()
                if(vehicle.speedKMH < waterSpeed) vehicle.speedKMH = waterSpeed
                // TODO Splash
            }
        } else {
            if(vehicle.speedKMH == -waterSpeed) return

            if(vehicle.speedKMH > -waterSpeed) {
                vehicle.speedKMH -= 0.04
                if(vehicle.speedKMH < -waterSpeed) vehicle.speedKMH = -waterSpeed
            } else {
                vehicle.speedKMH += metadata[1].toDouble()
                if(vehicle.speedKMH > -waterSpeed) vehicle.speedKMH = -waterSpeed
                // TODO Splash (aan de achterkant)
            }
        }
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
        vehicle.physicsType = TrackVehicle.PhysicsType.ALL
    }
}