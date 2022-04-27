package net.bandithemepark.bandicore.park.attractions.tracks.runnables

import net.bandithemepark.bandicore.BandiCore

class TrackVehicleActionUpdater {
    fun onTick() {
        for(vehicle in BandiCore.instance.trackManager.vehicleManager.vehicles) {
            for(action in vehicle.actions.toList()) {
                if(action.deSchedule) {
                    vehicle.actions.remove(action)
                } else {
                    action.tick++
                    action.update()
                }
            }
        }
    }
}