package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments

import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

abstract class AttachmentType(val id: String, val howToConfigure: String): Cloneable {
    lateinit var metadata: List<String>

    override fun clone(): AttachmentType {
        return super.clone() as AttachmentType
    }

    fun register() {
        types.add(this)
    }

    abstract fun onSpawn(location: Location, parent: Attachment)
    abstract fun onUpdate(mainPosition: Vector, mainRotation: Quaternion, secondaryPositions: HashMap<Vector, Quaternion>, rotationDegrees: Vector)
    abstract fun onDeSpawn()
    abstract fun onMetadataLoad(metadata: List<String>)
    abstract fun markFor(player: Player)
    abstract fun unMarkFor(player: Player)

    companion object {
        val types = mutableListOf<AttachmentType>()

        /**
         * Gets the attachment type by id
         * @param id The id of the attachment type
         * @return The attachment type if any is found
         */
        fun get(id: String): AttachmentType? {
            return types.find { it.id == id }
        }

        /**
         * Gets the attachment type by id and metadata which is automatically applied
         * @param id The id of the attachment type
         * @param metadata The metadata to apply
         * @return The attachment type with the metadata applied
         */
        fun get(id: String, metadata: List<String>): AttachmentType? {
            val type = get(id)
            if(type != null) {
                val newType = type.clone()
                newType.metadata = metadata
                newType.onMetadataLoad(metadata)
                return newType
            }

            return null
        }
    }
}
