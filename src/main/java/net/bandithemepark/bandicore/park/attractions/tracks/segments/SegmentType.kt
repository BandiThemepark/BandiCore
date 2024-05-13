package net.bandithemepark.bandicore.park.attractions.tracks.segments

import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.util.TrackUtil
import net.bandithemepark.bandicore.util.Util

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
    fun isNextBlockClear(debug: Boolean = false): Boolean {
        if(debug) Util.debug("IsNextBlockClear", "Starting check of if next block is clear")
        if(TrackUtil.getTrack(parent)!!.eStop) {
            if(debug) Util.debug("IsNextBlockClear", "E-Stop is active")
            return false
        }

        val track = TrackUtil.getTrack(parent)!!

        var clear = true
        var current: SegmentSeparator? = parent

        while(current!!.next  != null) {
            if(debug) Util.debug("IsNextBlockClear", "Next block found")
            current = current.next
            if(debug) Util.debug("IsNextBlockClear", "Checking block with index ${track.segmentSeparators.indexOf(current)}")

            if(current!!.vehicles.isNotEmpty()) {
                if(debug) Util.debug("IsNextBlockClear", "Block is not clear because it has vehicles")
                clear = false
                break
            }

            if(current.type != null) {
                if(current.type!!.isBlockSection) {
                    if(debug) Util.debug("IsNextBlockClear", "Block is clear, because next empty block is a block section")
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