package net.bandithemepark.bandicore.park.attractions.tracks.vehicles

import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment

class TrackVehicleMember(var parent: TrackVehicle, var size: Int) {
    var attachments = mutableListOf<Attachment>()

    /**
     * Gets all attachments in this member
     * @return All attachments in this member including their children
     */
    fun getAllAttachments(): List<Attachment> {
        val attachments2 = mutableListOf<Attachment>()

        for(attachment in attachments) {
            attachments2.addAll(attachment.getAllAttachments())
        }

        return attachments2
    }
}