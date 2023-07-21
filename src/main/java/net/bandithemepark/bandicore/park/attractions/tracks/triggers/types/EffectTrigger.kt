package net.bandithemepark.bandicore.park.attractions.tracks.triggers.types

import net.bandithemepark.bandicore.park.attractions.tracks.triggers.TrackTriggerType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.server.effects.Effect

class EffectTrigger: TrackTriggerType("effect", "EFFECT_NAME") {
    override fun onActivation(vehicle: TrackVehicle) {
        val effect = Effect(metadata[0])
        effect.play()
    }
}