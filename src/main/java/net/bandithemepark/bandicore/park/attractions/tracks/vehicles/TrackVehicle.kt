package net.bandithemepark.bandicore.park.attractions.tracks.vehicles

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.actions.TrackVehicleAction
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.TrackUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.io.File

class TrackVehicle(val ridingOn: TrackLayout, var position: TrackPosition, val id: String) {
    val members = mutableListOf<TrackVehicleMember>()
    var speed = 0.0
    var frictionMultiplier = 1.0
    var gravityMultiplier = 1.0
    var physicsType = PhysicsType.ALL
    var actions = mutableListOf<TrackVehicleAction>()
    var isCollidingThisTick = false

    var speedMS: Double
        get() = speed * 20.0
        set(value) {
            speed = value / 20.0
        }

    var speedKMH: Double
        get() = speed * 72
        set(value) {
            speed = value / 72
        }

    /**
     * Returns all attachments in this vehicle
     */
    fun getAllAttachments(): List<Attachment> {
        val attachments = mutableListOf<Attachment>()

        for(member in members) {
            attachments.addAll(member.getAllAttachments())
        }

        return attachments
    }

    /**
     * Despawns all attachments in this vehicle
     */
    fun deSpawnAttachments() {
        for(attachment in getAllAttachments()) {
            attachment.type.onDeSpawn()
        }
    }

    /**
     * Gets the total length of the vehicle in points. Divide by TrackManager#pointsPerMeter to get the length in meters. Mainly used internally
     * @return Length of the vehicle in points
     */
    fun getLengthInPoints(): Int {
        var totalSize = 0
        for(member in members) totalSize += member.size
        return totalSize
    }

    /**
     * Saves the train at trains/<id>.yml
     * @param id The id to save to
     */
    fun saveAs(id: String) {
        val fm = FileManager()

        // Creating the file if it doesn't exist
        val file = File(BandiCore.instance.dataFolder, "trains/$id.yml")
        if(!file.exists()) {
            file.createNewFile()
        }

        // Save friction and clearing
        fm.getConfig("trains/$id.yml").get().set("members", null)
        fm.getConfig("trains/$id.yml").get().set("frictionMultiplier", frictionMultiplier)

        // Saving all members
        for((index, member) in members.withIndex()) {
            // Saving the size
            fm.getConfig("trains/$id.yml").get().set("members.$index.size", member.size.toDouble() / BandiCore.instance.trackManager.pointsPerMeter.toDouble())

            // Saving all attachments
            for(attachment in member.getAllAttachments()) {
                fm.getConfig("trains/$id.yml").get().set("members.$index.attachments.${attachment.id}.type", attachment.type.id)
                fm.getConfig("trains/$id.yml").get().set("members.$index.attachments.${attachment.id}.metadata", attachment.type.metadata)
                fm.getConfig("trains/$id.yml").get().set("members.$index.attachments.${attachment.id}.position", attachment.position.getList())

                // Secondary positions
                for((positionIndex, position) in attachment.secondaryPositions.withIndex()) {
                    fm.getConfig("trains/$id.yml").get().set("members.$index.attachments.${attachment.id}.secondaryPositions.$positionIndex", position.getList())
                }

                // Parent (if there is one)
                val parent = member.getAllAttachments().find { it.children.contains(attachment) }
                if(parent != null) {
                    fm.getConfig("trains/$id.yml").get().set("members.$index.attachments.${attachment.id}.parent", parent.id)
                }
            }
        }

        // Saving the file
        fm.saveConfig("trains/$id.yml")
    }

    /**
     * Gets the vehicle in front of this vehicle
     * @return The vehicle in front of this vehicle, null if there is none in front of it
     */
    fun getNextVehicle(): TrackVehicle? {
        val vehiclesOnTrack = ridingOn.getVehicles().filter { it != this }

        var currentNode = position.nodePosition
        var nextVehicle = null as TrackVehicle?

        while(currentNode.connectedTo != null) {
            val vehiclesOnNode = vehiclesOnTrack.filter { it.position.nodePosition == currentNode }.filter { it.position.position >= position.position }

            if(vehiclesOnNode.isNotEmpty()) {
                nextVehicle = vehiclesOnNode.sortedBy { it.position.position }[0]
                break
            }

            currentNode = currentNode.connectedTo!!
        }

        return nextVehicle
    }

    /**
     * Checks whether this vehicle overlaps with another
     * @param otherVehicle The other vehicle
     * @return Whether this vehicle overlaps with another
     */
    fun overlaps(otherVehicle: TrackVehicle): Boolean {
        // Get front and back of both vehicles
        val front = getFront()
        val back = getBack()
        val nodesBetween = TrackUtil.getCurveBetweenPositions(back, front)

        val otherFront = otherVehicle.getFront()
        val otherBack = otherVehicle.getBack()
        val otherNodesBetween = TrackUtil.getCurveBetweenPositions(otherBack, otherFront)

        return nodesBetween.any { otherNodesBetween.contains(it) }
    }

    /**
     * Gets the front of this vehicle
     */
    fun getFront(): TrackPosition {
        val totalLength = getLengthInPoints()
        val front = position.clone()

        front.move(ridingOn, totalLength / 2.0)
        return front
    }

    /**
     * Gets the back of this vehicle
     */
    fun getBack(): TrackPosition {
        val totalLength = getLengthInPoints()
        val back = position.clone()

        back.move(ridingOn, -totalLength / 2.0)
        return back
    }

    enum class PhysicsType {
        ALL, DOWN, UP, NONE, COLLISION_ONLY
    }
}