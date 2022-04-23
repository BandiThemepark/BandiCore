package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments

class Attachment(val id: String, val position: AttachmentPosition, val secondaryPositions: List<AttachmentPosition>, var type: AttachmentType, val children: MutableList<Attachment>) {
    // TODO Update method die ook alle children update

    /**
     * @return Returns all the attachments inside this attachment, including itself
     */
    fun getAllAttachments(): List<Attachment> {
        val attachments = mutableListOf<Attachment>()

        attachments.add(this)
        children.forEach {
            attachments.addAll(it.getAllAttachments())
        }

        return attachments
    }
}