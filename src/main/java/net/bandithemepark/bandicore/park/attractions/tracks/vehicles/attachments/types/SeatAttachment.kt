package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types

import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentPosition
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentType
import net.bandithemepark.bandicore.util.entity.PacketEntitySeat
import net.bandithemepark.bandicore.util.entity.marker.PacketEntityMarker
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class SeatAttachment: AttachmentType("seat", "ATTRACTION_ID") {
    val BODY_HEIGHT = 1.1
    val ARMOR_STAND_HEIGHT = 1.4375

    lateinit var parent: Attachment
    var seat: PacketEntitySeat? = null
    lateinit var marker: PacketEntityMarker

    override fun onSpawn(location: Location, parent: Attachment) {
        this.parent = parent
        marker = PacketEntityMarker(location.world)

        seat = PacketEntitySeat()
        seat!!.spawn(location)
        seat!!.handle!!.isInvisible = true
        seat!!.handle!!.isNoGravity = true
        seat!!.updateMetadata()
        updateSecondPosition()
    }

    override fun onUpdate(mainPosition: Vector, mainRotation: Quaternion, secondaryPositions: HashMap<Vector, Quaternion>, rotationDegrees: Vector) {
        // TODO Use secondary position to lock head of player.
        marker.moveEntity(mainPosition)

        val seatPosition = secondaryPositions.keys.toList()[0]
        val seatRotation = secondaryPositions[seatPosition]!!
        val editedPosition = seatPosition.clone()
        editedPosition.y = editedPosition.y - (BODY_HEIGHT+ARMOR_STAND_HEIGHT)
        seat!!.moveEntity(editedPosition, seatRotation, rotationDegrees)

//        val editedPosition = mainPosition.clone()
//        editedPosition.y = editedPosition.y - ARMOR_STAND_HEIGHT
//        seat!!.moveEntity(editedPosition, mainRotation, rotationDegrees)
    }

    override fun onDeSpawn() {
        seat!!.deSpawn()
        seat = null
    }

    override fun onMetadataLoad(metadata: List<String>) {
        // TODO Load attraction and it's exiting location and apply it to the seat
    }

    override fun markFor(player: Player) {
        if(!marker.viewers.contains(player)) marker.addViewer(player)
    }

    override fun unMarkFor(player: Player) {
        marker.removeViewer(player)
    }

    fun updateSecondPosition() {
        parent.secondaryPositions.clear()
        val pos = parent.position
        parent.secondaryPositions.add(AttachmentPosition(pos.x, pos.y + BODY_HEIGHT, pos.z, pos.pitch, pos.yaw, pos.roll))
    }

    // TODO Add necessary event listeners for hiding the player and placing their custom skin
    // TODO Create class for coaster rider. To add support for NPCs
}