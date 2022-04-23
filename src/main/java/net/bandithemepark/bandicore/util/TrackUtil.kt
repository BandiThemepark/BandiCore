package net.bandithemepark.bandicore.util

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.park.attractions.tracks.TrackNode
import net.bandithemepark.bandicore.util.math.MathUtil
import org.bukkit.Location

object TrackUtil {
    fun getLength(lengthPoints: ArrayList<TrackNode>): Double {
        var finalDouble = 0.0
        for (i in 0 until lengthPoints.size - 2) {
            val from: TrackNode = lengthPoints[i]
            val to: TrackNode = lengthPoints[i + 1]
            finalDouble += MathUtil.getDistanceBetween(from.asVector(), to.asVector())
        }
        return finalDouble
    }

    fun getNodeBeforeNode(layout: TrackLayout, node: TrackNode): TrackNode? {
        return layout.nodes.find {
            if(it.connectedTo != null) {
                it.connectedTo!!.id == node.id
            } else {
                false
            }
        }
    }

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

    fun connectTo(layout: TrackLayout, from: TrackNode, to: TrackNode) {
        if(from.connectedTo != null) {
            disconnect(layout, from)
        }

        from.connectedTo = to
        updateMovedNode(layout, from)
        updateMovedNode(layout, to)
    }

    fun disconnect(layout: TrackLayout, node: TrackNode) {
        val previouslyConnectedTo = node.connectedTo
        node.connectedTo = null

        updateMovedNode(layout, node)
        if(previouslyConnectedTo != null) updateMovedNode(layout, previouslyConnectedTo)
    }

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
}