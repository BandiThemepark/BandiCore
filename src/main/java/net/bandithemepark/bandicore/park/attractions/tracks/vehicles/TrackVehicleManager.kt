package net.bandithemepark.bandicore.park.attractions.tracks.vehicles

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentSeparator
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentPosition
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.AttachmentType
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.commands.*
import net.bandithemepark.bandicore.util.FileManager
import org.bukkit.Bukkit
import org.bukkit.Location

class TrackVehicleManager {
    val vehicles = mutableListOf<TrackVehicle>()
    val lastSegments = hashMapOf<TrackVehicle, SegmentSeparator>()

    init {
        // Setting up all commands
        VehicleCommandList().register()
        VehicleCommandSpeed().register()
        VehicleCommandSpawn().register()
        VehicleCommandDeSpawn().register()
        VehicleCommandEdit().register()
        VehicleCommandOpenHarnesses().register()
    }

    /**
     * Gets a spawned vehicle by its ID
     * @param id The ID of the vehicle
     * @return The vehicle, or null if it doesn't exist
     */
    fun getVehicle(id: String): TrackVehicle? {
        return vehicles.find { it.id == id }
    }

    /**
     * Despawns all spawned vehicle
     */
    fun deSpawnAllVehicles() {
        for (vehicle in vehicles) {
            vehicle.deSpawnAttachments()
        }
        vehicles.clear()
    }

    /**
     * Loads a train from a file in the trains folder, and spawns it
     * @param id The id that is used to find the file to load, and also used to name the train
     * @param layout The layout to spawn the train on
     * @param position The position to spawn the train at
     * @param speedKMH The speed of the train in km/h
     */
    fun loadTrain(id: String, layout: TrackLayout, position: TrackPosition, speedKMH: Double) {
        // Getting the ID (add numbers if there are already multiple with the same name)
        var vehicleName = id

        while(getVehicle(vehicleName) != null) {
            val numberText = vehicleName.replace(id, "")

            if(numberText.isEmpty()) {
                vehicleName += "1"
            } else {
                vehicleName = id + (numberText.toInt() + 1).toString()
            }
        }

        // Creating the instance to add to later
        val vehicle = TrackVehicle(layout, position, vehicleName)
        vehicle.speedKMH = speedKMH

        // Preparing the loading
        val fm = FileManager()
        fm.reloadConfig("trains/$id.yml")
        fm.saveConfig("trains/$id.yml")

        // Friction multiplier
        val frictionMultiplier = fm.getConfig("trains/$id.yml").get().getDouble("frictionMultiplier")
        vehicle.frictionMultiplier = frictionMultiplier

        // Getting all the members
        for(memberId in fm.getConfig("trains/$id.yml").get().getConfigurationSection("members")!!.getKeys(false)) {
            val sizeBlocks = fm.getConfig("trains/$id.yml").get().getDouble("members.$memberId.size")
            val attachments = mutableListOf<Attachment>()
            val children = hashMapOf<Attachment, String>()

            // Getting all the attachments
            for(attachmentId in fm.getConfig("trains/$id.yml").get().getConfigurationSection("members.$memberId.attachments")!!.getKeys(false)) {
                val positionData = fm.getConfig("trains/$id.yml").get().getDoubleList("members.$memberId.attachments.$attachmentId.position")
                val attachmentPosition = AttachmentPosition(positionData[0], positionData[1], positionData[2], positionData[3], positionData[4], positionData[5])

                val typeId = fm.getConfig("trains/$id.yml").get().getString("members.$memberId.attachments.$attachmentId.type")!!
                val metadata = fm.getConfig("trains/$id.yml").get().getStringList("members.$memberId.attachments.$attachmentId.metadata")
                val type = AttachmentType.get(typeId, metadata)!!

                val secondaryPositions = mutableListOf<AttachmentPosition>()
                if(fm.getConfig("trains/$id.yml").get().contains("members.$memberId.attachments.$attachmentId.secondaryPositions")) {
                    for(secondaryPositionId in fm.getConfig("trains/$id.yml").get().getConfigurationSection("members.$memberId.attachments.$attachmentId.secondaryPositions")!!.getKeys(false)) {
                        val secondaryPositionData = fm.getConfig("trains/$id.yml").get().getDoubleList("members.$memberId.attachments.$attachmentId.secondaryPositions.$secondaryPositionId")
                        secondaryPositions.add(AttachmentPosition(secondaryPositionData[0], secondaryPositionData[1], secondaryPositionData[2], secondaryPositionData[3], secondaryPositionData[4], secondaryPositionData[5]))
                    }
                }

                if(fm.getConfig("trains/$id.yml").get().contains("members.$memberId.attachments.$attachmentId.parent")) {
                    val parentId = fm.getConfig("trains/$id.yml").get().getString("members.$memberId.attachments.$attachmentId.parent")!!
                    children[Attachment(attachmentId, attachmentPosition, secondaryPositions, type, mutableListOf())] = parentId
                } else {
                    attachments.add(Attachment(attachmentId, attachmentPosition, secondaryPositions, type, mutableListOf()))
                }
            }

            // Assigning the children
            for(child in children.keys) {
                val parentId = children[child]!!
                val childParent = children.keys.find { it.id == parentId }

                if(childParent != null) {
                    childParent.children.add(child)
                } else {
                    val attachmentParent = attachments.find { it.id == parentId }

                    if(attachmentParent != null) {
                        attachmentParent.children.add(child)
                    } else {
                        BandiCore.instance.logger.severe("An attempt was made at assigning attachment ${child.id} of train $id to a parent, but the given parent with ID $parentId was not found. It will now not be added")
                    }
                }
            }

            // Creating the member
            val member = TrackVehicleMember(vehicle, (sizeBlocks * BandiCore.instance.trackManager.pointsPerMeter).toInt())
            member.attachments = attachments
            vehicle.members.add(member)

            member.getAllAttachments().forEach { it.parent = member }

            for(attachment in member.getAllAttachments()) {
                attachment.type.onSpawn(Location(layout.world, layout.origin.x + position.nodePosition.x, layout.origin.y + position.nodePosition.y, layout.origin.z + position.nodePosition.z), attachment)
            }
        }

        // Adding the vehicle
        vehicles.add(vehicle)
    }
}