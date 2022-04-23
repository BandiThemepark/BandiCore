package net.bandithemepark.bandicore.park.attractions.tracks.vehicles

import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment

class TrackVehicle(val ridingOn: TrackLayout, var position: TrackPosition, val id: String) {
    val members = mutableListOf<TrackVehicleMember>()
    var speed = 0.0
    var frictionMultiplier = 1.0
    var physicsType = PhysicsType.ALL
    // TODO Keep track of vehicle actions

    var speedMS: Double
        get() = speed * 20.0
        set(value) {
            speed = value / 20.0
        }

    var speedKMH: Double
        get() = speed * 72
        set(value) {
            speed = value / 72
        }

    /**
     * Returns all attachments in this vehicle
     */
    fun getAllAttachments(): List<Attachment> {
        val attachments = mutableListOf<Attachment>()

        for(member in members) {
            attachments.addAll(member.getAllAttachments())
        }

        return attachments
    }

    /**
     * Despawns all attachments in this vehicle
     */
    fun deSpawn() {
        for(attachment in getAllAttachments()) {
            attachment.type.onDeSpawn()
        }
    }

    // TODO Save train as

    enum class PhysicsType {
        ALL, DOWN, UP, NONE
    }
}