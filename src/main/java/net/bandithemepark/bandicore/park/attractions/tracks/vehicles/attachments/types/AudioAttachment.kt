package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.audioserver.SpatialAudioSource
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentType
import net.bandithemepark.bandicore.util.entity.marker.PacketEntityMarker
import net.bandithemepark.bandicore.util.math.MathUtil
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs

class AudioAttachment: AttachmentType("audio", "AUDIO_SOURCE_ID, INNER_RANGE, OUTER_RANGE, MIN_VOLUME_SPEED, MAX_VOLUME_SPEED, MIN_PITCH_SPEED, MAX_PITCH_SPEED") {
    lateinit var audioSourceId: String
    var innerRange: Double = 0.0
    var outerRange: Double = 0.0
    var minVolumeSpeed: Double = 0.0
    var maxVolumeSpeed: Double = 0.0
    var minPitchSpeed: Double = 0.0
    var maxPitchSpeed: Double = 0.0

    private lateinit var marker: PacketEntityMarker
    private lateinit var parent: Attachment
    var parentVehicle: TrackVehicle? = null

    var spatialAudio: SpatialAudioSource? = null
    override fun onSpawn(location: Location, parent: Attachment) {
        this.parent = parent

        marker = PacketEntityMarker(location.world)

        spatialAudio = SpatialAudioSource(
            UUID.randomUUID(),
            location,
            audioSourceId,
            true,
            innerRange,
            outerRange
        )
    }

    override fun onUpdate(mainPosition: Vector, mainRotation: Quaternion, secondaryPositions: HashMap<Vector, Quaternion>, rotationDegrees: Vector) {
        if(parentVehicle == null) parentVehicle = BandiCore.instance.trackManager.vehicleManager.vehicles.find { it.getAllAttachments().contains(parent) }!!
        marker.moveEntity(mainPosition)

        var volume = (abs(parentVehicle!!.speedKMH) - minVolumeSpeed) / (maxVolumeSpeed - minVolumeSpeed)
        if (volume > 1.0) volume = 1.0
        if (volume < 0.0) volume = 0.0

        var pitch = (abs(parentVehicle!!.speedKMH) - minPitchSpeed) / (maxPitchSpeed - minPitchSpeed)
        if(pitch > 2.0) pitch = 2.0
        if(pitch < 0.4) pitch = 0.4

        spatialAudio?.setLocation(mainPosition.toLocation(spatialAudio!!.world), volume, pitch)
    }

    override fun onDeSpawn() {
        marker.viewers.forEach { marker.removeViewer(it) }

        spatialAudio?.remove()
        spatialAudio = null
    }

    override fun onMetadataLoad(metadata: List<String>) {
        audioSourceId = metadata[0]
        innerRange = metadata[1].toDouble()
        outerRange = metadata[2].toDouble()
        minVolumeSpeed = metadata[3].toDouble()
        maxVolumeSpeed = metadata[4].toDouble()
        minPitchSpeed = metadata[5].toDouble()
        maxPitchSpeed = metadata[6].toDouble()
    }

    override fun markFor(player: Player) {
        if(!marker.viewers.contains(player)) marker.addViewer(player)
    }

    override fun unMarkFor(player: Player) {
        marker.removeViewer(player)
    }
}