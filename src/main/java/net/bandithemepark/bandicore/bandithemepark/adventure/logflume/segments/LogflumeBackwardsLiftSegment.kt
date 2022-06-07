package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.segments

import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class LogflumeBackwardsLiftSegment: SegmentType("logflumebackwardslift", true, "speed") {
    val slowDown = hashMapOf<TrackVehicle, Boolean>()

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        // Whether the boat should be slowed down or sped up.
        slowDown[vehicle] = vehicle.speedKMH < -metadata[0].toDouble()

        // If it doesn't need to slow down (so speed up), then set the physics type
        if(!slowDown[vehicle]!!) {
            vehicle.physicsType = TrackVehicle.PhysicsType.UP
        }
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        if(slowDown.containsKey(vehicle)) {
            val shouldSlowDown = slowDown[vehicle]!!

            if(shouldSlowDown) {
                if(vehicle.speedKMH > -metadata[0].toDouble()) {
                    vehicle.speedKMH = -metadata[0].toDouble()
                    vehicle.physicsType = TrackVehicle.PhysicsType.UP
                    slowDown.remove(vehicle)
                }
            } else {
                vehicle.speedKMH += -metadata[0].toDouble()/30.0

                if(vehicle.speedKMH < -metadata[0].toDouble()) {
                    vehicle.speedKMH = -metadata[0].toDouble()
                    slowDown.remove(vehicle)
                }
            }
        }
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
        slowDown.remove(vehicle)
        vehicle.physicsType = TrackVehicle.PhysicsType.ALL
    }
}