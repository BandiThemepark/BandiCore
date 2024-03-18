package net.bandithemepark.bandicore.park.attractions.tracks.triggers.types

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.triggers.TrackTriggerType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.server.effects.Effect
import org.bukkit.Bukkit

class EffectTrigger: TrackTriggerType("effect", "EFFECT_NAME") {
    override fun onActivation(vehicle: TrackVehicle) {
        if(BandiCore.instance.effectManager.playingEffects.any { it.fileName == metadata[0] }) {
            val alreadyPlayingEffect = BandiCore.instance.effectManager.playingEffects.find { it.fileName == metadata[0] }!!
            if(!alreadyPlayingEffect.forwards) return
        }

        val effect = Effect(metadata[0])
        effect.play()
    }
}