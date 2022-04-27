package net.bandithemepark.bandicore.util

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.RollNode
import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.park.attractions.tracks.TrackNode
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
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

    private fun updateNodePath(layout: TrackLayout, node: TrackNode) {
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
        updateMovedNode(layout, to)
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
}