package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.switch

import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop.LogFlumeRideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil
import org.bukkit.Bukkit

class LogFlumeStorageSegment: SegmentType("logflumestorage", true, "index, speedKMH") {
    var mode = Mode.PASSTHROUGH
    var currentVehicle: TrackVehicle? = null

    override fun onVehicleEnter(vehicle: TrackVehicle) {
        if(vehicle.speed == 0.0) return
        currentVehicle = vehicle
    }

    override fun onVehicleUpdate(vehicle: TrackVehicle) {
        if(mode == Mode.STORING) {
            if(!TrackUtil.isPastMiddle(parent, vehicle)) {
                vehicle.speed = 0.0
                val rideOP = (RideOP.get("logflume") as LogFlumeRideOP)
                rideOP.storageState = LogFlumeRideOP.StorageState.NONE
                rideOP.boatsInStorage++
                mode = Mode.PASSTHROUGH
                rideOP.updateMenu()
            }
        }
    }

    override fun onVehicleLeave(vehicle: TrackVehicle) {
        if(vehicle.speed == 0.0) return
        currentVehicle = null
    }

    fun retrieve() {
        currentVehicle!!.speedKMH = metadata[1].toDouble()
    }

    enum class Mode {
        PASSTHROUGH, STORING
    }
}