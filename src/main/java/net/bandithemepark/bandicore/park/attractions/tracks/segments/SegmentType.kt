package net.bandithemepark.bandicore.park.attractions.tracks.segments

import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil

abstract class SegmentType(val id: String, val isBlockSection: Boolean, val howToUse: String): Cloneable {
    override fun clone(): SegmentType {
        return super.clone() as SegmentType
    }

    fun register() {
        types.add(this)
    }

    lateinit var parent: SegmentSeparator
    lateinit var metadata: List<String>

    abstract fun onVehicleEnter(vehicle: TrackVehicle)
    abstract fun onVehicleUpdate(vehicle: TrackVehicle)
    abstract fun onVehicleLeave(vehicle: TrackVehicle)

    /**
     * Tells you if the next block section is clear
     * @return True if the next block section is clear
     */
    fun isNextBlockClear(): Boolean {
        if(TrackUtil.getTrack(parent)!!.eStop) {
            return false
        }

        var clear = true
        var current: SegmentSeparator? = parent

        while(current!!.next  != null) {
            current = current.next

            if(current!!.vehicles.isNotEmpty()) {
                clear = false
                break
            }

            if(current.type != null) {
                if(current.type!!.isBlockSection) {
                    break
                }
            }
        }

        return clear
    }

    companion object {
        val types = mutableListOf<SegmentType>()

        /**
         * Generates a new segment type with the given ID and metadata, to be added to the parent. You still have to set it yourself though!
         * @param id The ID of the new segment type
         * @param parent The parent of the new segment type
         * @param metadata The metadata of the new segment type
         * @return The new segment type, null if no segment type with the given ID exists
         */
        fun getNew(id: String, parent: SegmentSeparator, metadata: List<String>): SegmentType? {
            val type = types.find { it.id == id }
            return if (type != null) getNew(type, parent, metadata) else null
        }

        /**
         * Generates a new segment type instance with the given type and metadata, to be added to the parent. You still have to set it yourself though!
         * @param type The type to use
         * @param parent The parent of the new segment type
         * @param metadata The metadata of the new segment type
         * @return The new segment type
         */
        fun getNew(type: SegmentType, parent: SegmentSeparator, metadata: List<String>): SegmentType {
            val newType = type.clone()
            newType.parent = parent
            newType.metadata = metadata
            return newType
        }
    }
}