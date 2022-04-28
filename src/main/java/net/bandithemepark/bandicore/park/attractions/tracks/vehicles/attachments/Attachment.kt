package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments

import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.util.Vector

class Attachment(var id: String, val position: AttachmentPosition, val secondaryPositions: MutableList<AttachmentPosition>, var type: AttachmentType, val children: MutableList<Attachment>) {
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

    /**
     * Updates the attachment and it's children with a given position and rotation.
     * @param position A vector representing the position of the attachment
     * @param rotation A quaternion representing the rotation of the attachment
     * @param originalRotation A vector containing the original pitch, yaw and roll
     */
    fun update(position: Vector, rotation: Quaternion, originalRotation: Vector) {
        // Updating the personal position of this attachment
        val newPosition = position.clone()
        newPosition.add(this.position.getPosition(rotation))
        //Bukkit.broadcast(Component.text("Updating main position, offset: ${this.position.toVector()}, before: $position, after: $newPosition"))
        val newRotation = rotation.clone()
        newRotation.multiply(Quaternion.fromYawPitchRoll(this.position.pitch, this.position.yaw, this.position.roll))

        // Updating for secondary positions
        val secondPositions = hashMapOf<Vector, Quaternion>()
        for(secondaryPosition in secondaryPositions) {
            val newSecondaryPosition = position.clone()
            newSecondaryPosition.add(secondaryPosition.getPosition(rotation))
            //Bukkit.broadcast(Component.text("Updating secondary position, before: ${secondaryPosition.toVector().clone().add(position)}, after: $newSecondaryPosition"))
            val newSecondaryRotation = rotation.clone()
            newSecondaryRotation.multiply(Quaternion.fromYawPitchRoll(secondaryPosition.pitch, secondaryPosition.yaw, secondaryPosition.roll))

            secondPositions[newSecondaryPosition] = newSecondaryRotation
        }

        // Updating the type
        type.onUpdate(newPosition.clone(), newRotation.clone(), secondPositions, originalRotation.clone())

        // Also updating children
        for(child in children) {
            child.update(newPosition.clone(), newRotation.clone(), originalRotation.clone())
        }
    }
}