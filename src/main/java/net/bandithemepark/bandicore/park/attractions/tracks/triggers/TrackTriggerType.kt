package net.bandithemepark.bandicore.park.attractions.tracks.triggers

import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle

abstract class TrackTriggerType(val id: String, val howToUse: String): Cloneable {
    override fun clone(): TrackTriggerType {
        return super.clone() as TrackTriggerType
    }

    fun register() {
        types.add(this)
    }

    lateinit var parent: TrackTrigger
    lateinit var metadata: List<String>

    abstract fun onActivation(vehicle: TrackVehicle)

    companion object {
        val types  = mutableListOf<TrackTriggerType>()

        /**
         * Generates a new track trigger type with the given ID and metadata, to be added to the parent. You still have to set it yourself though!
         * @param id The ID of the new track trigger type
         * @param parent The parent of the new track trigger type
         * @param metadata The metadata of the new track trigger type
         * @return The new track trigger type, null if no track trigger type with the given ID exists
         */
        fun getNew(id: String, parent: TrackTrigger, metadata: List<String>): TrackTriggerType? {
            val type = types.find { it.id == id }
            return if (type != null) getNew(type, parent, metadata) else null
        }

        /**
         * Generates a new track trigger type instance with the given type and metadata, to be added to the parent. You still have to set it yourself though!
         * @param type The type to use
         * @param parent The parent of the new track trigger type
         * @param metadata The metadata of the new track trigger type
         * @return The new track trigger type
         */
        fun getNew(type: TrackTriggerType, parent: TrackTrigger, metadata: List<String>): TrackTriggerType {
            val newType = type.clone()
            newType.parent = parent
            newType.metadata = metadata
            return newType
        }
    }
}