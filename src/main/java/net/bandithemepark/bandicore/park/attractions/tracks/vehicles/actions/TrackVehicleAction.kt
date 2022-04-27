package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.actions

import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle

abstract class TrackVehicleAction(val parent: TrackVehicle) {
    var deSchedule = false
    var tick = 0
    abstract fun update()
}