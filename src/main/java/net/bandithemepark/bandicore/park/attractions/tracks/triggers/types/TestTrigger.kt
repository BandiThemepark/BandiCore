package net.bandithemepark.bandicore.park.attractions.tracks.triggers.types

import net.bandithemepark.bandicore.park.attractions.tracks.triggers.TrackTriggerType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class TestTrigger: TrackTriggerType("test", "MESSAGE") {
    override fun onActivation(vehicle: TrackVehicle) {
        Bukkit.broadcast(Component.text(metadata[0]))
    }
}