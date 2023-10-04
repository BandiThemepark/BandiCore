package net.bandithemepark.bandicore.park.attractions.tracks.triggers.types

import net.bandithemepark.bandicore.network.audioserver.ride.SpecialAudioManagement
import net.bandithemepark.bandicore.park.attractions.tracks.triggers.TrackTriggerType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle

class OnrideAudioTrigger: TrackTriggerType("onrideaudio", "SOURCE_ID") {
    override fun onActivation(vehicle: TrackVehicle) {
        vehicle.getPlayerPassengers().forEach {
            SpecialAudioManagement.playOnrideAudio(it, metadata[0], 0)
        }
    }
}