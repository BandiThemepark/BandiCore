package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.segments

import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class LogflumeStationSegment: SegmentType("logflumestation", true, "MOVEMENT_SPEED, STOP_POINT") {
    var currentStopped = null as TrackVehicle?
    val hasBeenDispatched = mutableListOf<TrackVehicle>()

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        vehicle.physicsType = TrackVehicle.PhysicsType.COLLISION_ONLY
        vehicle.speedKMH = metadata[0].toDouble()
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        if(currentStopped != vehicle) {
            if(vehicle.speed != 0.0 && vehicle.speedKMH != metadata[0].toDouble()) vehicle.speedKMH = metadata[0].toDouble()

            if(!hasBeenDispatched.contains(vehicle)) {
                if (TrackUtil.isPast(parent, vehicle, metadata[1].toDouble())) {
                    vehicle.speed = 0.0
                    vehicle.physicsType = TrackVehicle.PhysicsType.NONE
                    currentStopped = vehicle
                    RideOP.get("logflume")!!.updateMenu()
                }
            }
        }
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
        hasBeenDispatched.remove(vehicle)
        vehicle.physicsType = TrackVehicle.PhysicsType.ALL
    }

    fun dispatch() {
        if(currentStopped == null) return

        parent.vehicles.forEach {
            it.speedKMH = metadata[0].toDouble()
        }

        hasBeenDispatched.add(currentStopped!!)
        currentStopped!!.physicsType = TrackVehicle.PhysicsType.COLLISION_ONLY
        currentStopped = null
    }
}