package net.bandithemepark.bandicore.park.attractions.tracks.triggers.types

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.triggers.TrackTriggerType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.server.effects.Effect
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class EffectTrigger: TrackTriggerType("effect", "EFFECT_NAME, ONLY_FOR_RIDERS (true/false)") {
    override fun onActivation(vehicle: TrackVehicle) {
        if(BandiCore.instance.effectManager.playingEffects.any { it.fileName == metadata[0] }) {
            val alreadyPlayingEffect = BandiCore.instance.effectManager.playingEffects.find { it.fileName == metadata[0] }!!
            if(!alreadyPlayingEffect.forwards) return
        }

        var players: List<Player>? = null
        if(metadata.size > 1 && metadata[1].equals("true", true)) players = vehicle.getPlayerPassengers()

        val effect = Effect(metadata[0], players)
        effect.play()
    }
}