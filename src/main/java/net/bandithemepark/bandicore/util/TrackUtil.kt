package net.bandithemepark.bandicore.util

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.RollNode
import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.park.attractions.tracks.TrackNode
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentSeparator
import net.bandithemepark.bandicore.park.attractions.tracks.triggers.TrackTrigger
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.SeatAttachment
import net.bandithemepark.bandicore.util.math.MathUtil
import org.bukkit.Location

object TrackUtil {
    /**
     * Gets the length of a given list of path nodes
     * @param lengthPoints The list of path points
     * @return The length of the path as a double
     */
    fun getLength(lengthPoints: ArrayList<TrackNode>): Double {
        var finalDouble = 0.0
        for (i in 0 until lengthPoints.size - 2) {
            val from: TrackNode = lengthPoints[i]
            val to: TrackNode = lengthPoints[i + 1]
            finalDouble += MathUtil.getDistanceBetween(from.asVector(), to.asVector())
        }
        return finalDouble
    }

    /**
     * Gets the node that is connected to another node
     * @param layout The layout that the node is on
     * @param node The node to get the node before of
     * @return The node before the given node if it exists, null otherwise
     */
    fun getNodeBeforeNode(layout: TrackLayout, node: TrackNode): TrackNode? {
        return layout.nodes.find {
            if(it.connectedTo != null) {
                it.connectedTo!!.id == node.id
            } else {
                false
            }
        }
    }

    /**
     * Updates a moved node and it's neighbours. Use when that node's position has been changed.
     * @param layout The layout that the node is on
     * @param node The node to update
     */
    fun updateMovedNode(layout: TrackLayout, node: TrackNode) {
        updateNodePath(layout, node)

        val beforeNode = getNodeBeforeNode(layout, node)
        if(beforeNode != null) {
            updateNodePath(layout, beforeNode)

            val beforeBeforeNode = getNodeBeforeNode(layout, beforeNode)
            if(beforeBeforeNode != null) {
                updateNodePath(layout, beforeBeforeNode)
            }
        }
    }

    /**
     * Updates a node's path
     * @param layout The layout that the node is on
     * @param node The node to update
     */
    fun updateNodePath(layout: TrackLayout, node: TrackNode) {
        node.curve.clear()

        if(node.connectedTo != null) node.curve = BandiCore.instance.trackManager.splineType.interpolate(
            getNodeBeforeNode(layout, node),
            node,
            node.connectedTo,
            node.connectedTo!!.connectedTo
        )
    }

    /**
     * Connects one node to another
     * @param layout The layout that the nodes are on
     * @param from The node to connect to the to node
     * @param to The node to connect to
     */
    fun connectTo(layout: TrackLayout, from: TrackNode, to: TrackNode) {
        if(from.connectedTo != null) {
            disconnect(layout, from)
        }

        from.connectedTo = to
        updateMovedNode(layout, from)
        if(!to.strict) updateMovedNode(layout, to)
    }

    /**
     * Disconnects a node from it's connected node
     * @param layout The layout that the node is on
     * @param node The node to disconnect
     */
    fun disconnect(layout: TrackLayout, node: TrackNode) {
        val previouslyConnectedTo = node.connectedTo
        node.connectedTo = null

        updateMovedNode(layout, node)
        if(previouslyConnectedTo != null) updateMovedNode(layout, previouslyConnectedTo)
    }

    /**
     * Gets the node that is closest to a given location
     * @param layout The layout that should be checked for
     * @param location The location to check for
     * @return The node that is closest to the given location
     */
    fun getNearestNode(layout: TrackLayout, location: Location): TrackNode {
        var nearest = layout.nodes[0]
        var currentDistance = 9999.0

        for(node in layout.nodes) {
            val nodeLocation = Location(layout.world, node.x, node.y, node.z)
            nodeLocation.add(layout.origin)
            val distance = MathUtil.getDistanceBetween(location.toVector(), nodeLocation.toVector())

            if(distance < currentDistance) {
                currentDistance = distance
                nearest = node
            }
        }

        return nearest
    }

    /**
     * Gets the roll node that is closest to a given location
     * @param layout The layout that should be checked for
     * @param location The location to check for
     * @return The roll node that is closest to the given location
     */
    fun getNearestRollNode(layout: TrackLayout, location: Location): RollNode? {
        if(layout.rollNodes.size > 0) {
            var nearest = layout.rollNodes[0]
            var currentDistance = 9999.0

            for(node in layout.rollNodes) {
                val rollNodeLocation = node.position.getPathPoint().asVector()
                rollNodeLocation.add(layout.origin)
                val distance = MathUtil.getDistanceBetween(location.toVector(), rollNodeLocation)

                if(distance < currentDistance) {
                    currentDistance = distance
                    nearest = node
                }
            }

            return nearest
        } else {
            return null
        }
    }

    /**
     * Gets the segment separator that is closest to a given location
     * @param layout The layout that should be checked for
     * @param location The location to check for
     * @return The segment separator that is closest to the given location
     */
    fun getNearestSegmentSeparator(layout: TrackLayout, location: Location): SegmentSeparator? {
        if(layout.segmentSeparators.size > 0) {
            var nearest = layout.segmentSeparators[0]
            var currentDistance = 9999.0

            for(node in layout.segmentSeparators) {
                val rollNodeLocation = node.position.getPathPoint().asVector()
                rollNodeLocation.add(layout.origin)
                val distance = MathUtil.getDistanceBetween(location.toVector(), rollNodeLocation)

                if(distance < currentDistance) {
                    currentDistance = distance
                    nearest = node
                }
            }

            return nearest
        } else {
            return null
        }
    }

    /**
     * Gets the track trigger that is closest to a given location
     * @param layout The layout that should be checked for
     * @param location The location to check for
     * @return The track trigger that is closest to the given location
     */
    fun getNearestTrigger(layout: TrackLayout, location: Location): TrackTrigger? {
        if(layout.triggers.size > 0) {
            var nearest = layout.triggers[0]
            var currentDistance = 9999.0

            for(node in layout.triggers) {
                val rollNodeLocation = node.position.getPathPoint().asVector()
                rollNodeLocation.add(layout.origin)
                val distance = MathUtil.getDistanceBetween(location.toVector(), rollNodeLocation)

                if(distance < currentDistance) {
                    currentDistance = distance
                    nearest = node
                }
            }

            return nearest
        } else {
            return null
        }
    }

    /**
     * Gets all path points between two TrackPositions
     * @param position1 The first position
     * @param position2 The second position
     * @return List of path points between the two positions
     */
    fun getCurveBetweenPositions(position1: TrackPosition, position2: TrackPosition): List<TrackNode> {
        val node1 = position1.nodePosition
        val pos1 = position1.position.toInt()

        val node2 = position2.nodePosition
        val pos2 = position2.position.toInt()

        if(node1 != node2) {
            val curve = mutableListOf<TrackNode>()
            for(i in pos1 until node1.curve.size) curve.add(node1.curve[i])

            var currentNode = node1.connectedTo
            while(currentNode != null && currentNode != node2) {
                curve.addAll(currentNode.curve)
                currentNode = currentNode.connectedTo
            }

            if(currentNode != null) {
                for(i in 0 until pos2) curve.add(node2.curve[i])
            }

            return curve.distinct()
        } else {
            val curve = mutableListOf<TrackNode>()
            for(i in pos1 until pos2) curve.add(node1.curve[i])
            return curve.distinct()
        }
    }

    /**
     * Similar to getCurveBetweenPositions, but goes backwards. Use this when speed of vehicle is negative or when position1 comes after position2.
     * @param position1 The first position
     * @param position2 The second position
     * @return List of path points between the two positions
     */
    fun getCurveBetweenPositionsBackwards(position1: TrackPosition, position2: TrackPosition): List<TrackNode> {
        return listOf()
    }

    /**
     * Finds the track a segment separator is part of
     * @param segmentSeparator To find the track of
     * @return The track the segment separator is part of, null if not found
     */
    fun getTrack(segmentSeparator: SegmentSeparator): TrackLayout? {
        return BandiCore.instance.trackManager.loadedTracks.find { it.segmentSeparators.contains(segmentSeparator) }
    }

    /**
     * Tells you whether a given vehicle is past the middle of a given segment
     * @param vehicle The vehicle to check
     * @param segment The segment to check
     * @return Whether the vehicle is past the middle of the segment
     */
    fun isPastMiddle(segment: SegmentSeparator, vehicle: TrackVehicle): Boolean {
        return isPast(segment, vehicle, 0.5)
    }

    /**
     * Tells you whether a given vehicle is past a given point on a segment
     * @param vehicle The vehicle to check
     * @param segment The segment to check
     * @param t The percentage of how far the point is
     * @return Whether the vehicle is past the given percentage of the segment
     */
    fun isPast(segment: SegmentSeparator, vehicle: TrackVehicle, t: Double): Boolean {
        val vehicleCurvePoint = vehicle.position.getPathPoint()
        val middleIndex = (segment.curve.size * t).toInt()
        val vehicleIndex = segment.curve.indexOf(vehicleCurvePoint)
        return vehicleIndex >= middleIndex
    }

    /**
     * Tells you whether a given vehicle is before a given point on a segment
     * @param vehicle The vehicle to check
     * @param segment The segment to check
     * @param t The percentage of how far the point is
     * @return Whether the vehicle is before the given percentage of the segment
     */
    fun isBefore(segment: SegmentSeparator, vehicle: TrackVehicle, t: Double): Boolean {
        val vehicleCurvePoint = vehicle.position.getPathPoint()
        val middleIndex = (segment.curve.size * t).toInt()
        val vehicleIndex = segment.curve.indexOf(vehicleCurvePoint)
        return vehicleIndex <= middleIndex
    }

    /**
     * Opens or closes the harnesses of a vehicle
     * @param trackVehicle The vehicle to open or close the harnesses of
     * @param open Whether to open or close the harnesses
     */
    fun setHarnessOpen(trackVehicle: TrackVehicle, open: Boolean) {
        trackVehicle.getAllAttachments().forEach {
            if(it.type is SeatAttachment) {
                (it.type as SeatAttachment).seat?.harnessesOpen = open
            }
        }
    }
}