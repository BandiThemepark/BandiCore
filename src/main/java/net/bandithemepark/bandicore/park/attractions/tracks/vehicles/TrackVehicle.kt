package net.bandithemepark.bandicore.park.attractions.tracks.vehicles

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.actions.TrackVehicleAction
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.Attachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.SeatAttachment
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.TrackUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.File

class TrackVehicle(val ridingOn: TrackLayout, var position: TrackPosition, val id: String) {
    val members = mutableListOf<TrackVehicleMember>()
    var speed = 0.0
    var frictionMultiplier = 1.0
    var gravityMultiplier = 1.0
    var physicsType = PhysicsType.ALL
    var actions = mutableListOf<TrackVehicleAction>()

    var collidedInFront: TrackVehicle? = null
    var collidedBehind: TrackVehicle? = null

    fun hasCollided(): Boolean {
        return collidedInFront != null || collidedBehind != null
    }

    fun getFinalCollidedForwards(): TrackVehicle {
        if(collidedInFront == null) return this
        return collidedInFront!!.getFinalCollidedForwards()
    }

    fun getFinalCollidedBehind(): TrackVehicle {
        if(collidedBehind == null) return this
        return collidedBehind!!.getFinalCollidedBehind()
    }

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
     * Gets the vehicle behind this vehicle
     * @return The vehicle behind this vehicle, null if there is none behind it
     */
    fun getPreviousVehicle(): TrackVehicle? {
        return ridingOn.getVehicles().filter { it != this }.find { it.getNextVehicle() == this }
    }

    /**
     * Checks whether this vehicle overlaps with another
     * @param otherVehicle The other vehicle
     * @return Whether this vehicle overlaps with another
     */
    fun overlaps(otherVehicle: TrackVehicle): Boolean {
        val front = getFront()
        val back = getBack()

        val otherFront = otherVehicle.getFront()
        val otherBack = otherVehicle.getBack()

        if(front.getDistanceTo(otherBack) < 0.1) return true
        if(otherFront.getDistanceTo(back) < 0.1) return true

        return false

//        // Get front and back of both vehicles
//        val front = getFront()
//        val back = getBack()
//        val nodesBetween = TrackUtil.getCurveBetweenPositions(back, front)
//
//        val otherFront = otherVehicle.getFront()
//        val otherBack = otherVehicle.getBack()
//        val otherNodesBetween = TrackUtil.getCurveBetweenPositions(otherBack, otherFront)
//
//        return nodesBetween.any { otherNodesBetween.contains(it) }
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

    /**
     * Gets all passengers on this vehicle that are players (so excluding NPCs)
     */
    fun getPlayerPassengers(): List<Player> {
        val list = mutableListOf<Player>()
        getAllAttachments().filter { it.type is SeatAttachment }.forEach {
            (it.type as SeatAttachment).seat?.getPassengers()?.filterIsInstance<Player>()?.forEach {
                    entity -> list.add(entity)
            }
        }
        return list
    }

    enum class PhysicsType {
        ALL, DOWN, UP, NONE, COLLISION_ONLY
    }
}