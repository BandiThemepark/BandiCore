package net.bandithemepark.bandicore.park.attractions.tracks

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.TrackUtil
import net.bandithemepark.bandicore.util.math.MathUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

class TrackLayout(val id: String, var origin: Vector, var world: World, var nodes: MutableList<TrackNode>, var rollNodes: MutableList<RollNode>) {
    // TODO Add segment separators, track triggers, e-stop

    init {
        // TODO updating the segments
        updateConnections()
        updatePath()
        updateRoll()
    }

    private fun updateConnections() {
        for(node in nodes) {
            if(node.connectedToId != null) {
                node.connectedTo = nodes.find { it.id == node.connectedToId }
            }
        }
    }

    private fun updatePath() {
        val splineType = BandiCore.instance.trackManager.splineType

        for(node in nodes) {
            if(node.connectedTo != null) {
                val connectedTo = node.connectedTo!!
                val before = TrackUtil.getNodeBeforeNode(this, node)
                node.curve = splineType.interpolate(before, node, connectedTo, connectedTo.connectedTo)
            }
        }
    }

    /**
     * Updates all roll nodes on the track
     */
    fun updateRoll() {
        if(rollNodes.isEmpty()) return

        if(rollNodes.size == 1) {
            val roll = rollNodes[0].roll
            for(node in getAllPathPoints()) node.roll = roll
            return
        }

        for(rollNode in rollNodes) {
            var currentNode = rollNode.position.nodePosition
            var done = false

            while(currentNode.connectedTo != null && !done) {
                val rollNodesOnNode = rollNodes.filter { it.position.nodePosition == currentNode }
                if(rollNodesOnNode.isNotEmpty() && !rollNodesOnNode.contains(rollNode)) {
                    val next = rollNodesOnNode[0]
                    val curve = TrackUtil.getCurveBetweenPositions(rollNode.position, next.position)

                    if(rollNode.roll != 0.0 && next.roll != 0.0) {
                        for((index, curvePoint) in curve.withIndex()) {
                            var roll = MathUtil.interpolateAngles(rollNode.roll, next.roll, index.toDouble()/curve.size.toDouble())
                            if(roll > 180) {
                                roll -= 360
                            } else if(roll < -180) {
                                roll += 360
                            }
                            //Bukkit.broadcast(Component.text("Calculating roll. From: ${rollNode.roll}, to: ${next.roll}, T: ${index.toDouble()/curve.size.toDouble()}, roll: $roll"))
                            curvePoint.roll = roll
                        }
                    }

                    done = true
                }

                if(!done) currentNode = currentNode.connectedTo!!
            }

            if(currentNode.connectedTo == null && !done) {
                val curve = TrackUtil.getCurveBetweenPositions(rollNode.position, TrackPosition(currentNode, currentNode.curve.size-1))
                for(point in curve) point.roll = rollNode.roll
            }
        }

        //getAllPathPoints().forEach { Bukkit.broadcast(Component.text("Roll: ${it.roll}")) }
    }

    /**
     * Gets all path points on this track
     */
    fun getAllPathPoints(): List<TrackNode> {
        val pathPoints = mutableListOf<TrackNode>()
        for(node in nodes) {
            pathPoints.addAll(node.curve)
        }
        return pathPoints
    }

    /**
     * Updates a node and it's neighbours
     * @param node The node to update
     */
    fun updateMovedNode(node: TrackNode) {
        TrackUtil.updateMovedNode(this, node)
    }

    /**
     * Connects a node to another node
     * @param from The node that will be connected
     * @param to The node that from will be connected to
     */
    fun connectTo(from: TrackNode, to: TrackNode) {
        TrackUtil.connectTo(this, from, to)
    }

    /**
     * Disconnects a node if it is connected
     * @param node The node to disconnect
     */
    fun disconnect(node: TrackNode) {
        TrackUtil.disconnect(this, node)
    }

    /**
     * Gives the node nearest to a certain location
     * @param location The location to check from
     * @return The node nearest to the location
     */
    fun getNearestNode(location: Location): TrackNode {
        return TrackUtil.getNearestNode(this, location)
    }

    /**
     * Gets the node before another node
     * @param node The node to get the node before
     * @return The node before the node, null if not found
     */
    fun getBefore(node: TrackNode): TrackNode? {
        return TrackUtil.getNodeBeforeNode(this, node)
    }

    /**
     * Gets the roll node nearest to a certain location
     * @param location The location to check from
     * @return The roll node nearest to the location
     */
    fun getNearestRollNode(location: Location): RollNode? {
        return TrackUtil.getNearestRollNode(this, location)
    }

    /**
     * Saves a track to it's file
     */
    fun save() {
        val fm = FileManager()

        // Clearing the old config
        fm.getConfig("tracks/$id.trck").get().set("nodes", null)
        fm.getConfig("tracks/$id.trck").get().set("rollNodes", null)
        fm.getConfig("tracks/$id.trck").get().set("segmentSeparators", null)
        fm.getConfig("tracks/$id.trck").get().set("trackTriggers", null)
        fm.saveConfig("tracks/$id.trck")

        // Saving the default settings
        fm.getConfig("tracks/$id.trck").get().set("world", world.name)
        fm.getConfig("tracks/$id.trck").get().set("origin.x", origin.x)
        fm.getConfig("tracks/$id.trck").get().set("origin.y", origin.y)
        fm.getConfig("tracks/$id.trck").get().set("origin.z", origin.z)
        fm.saveConfig("tracks/$id.trck")

        // Saving all nodes
        for(node in nodes) {
            fm.getConfig("tracks/$id.trck").get().set("nodes.${node.id}.x", node.x)
            fm.getConfig("tracks/$id.trck").get().set("nodes.${node.id}.y", node.y)
            fm.getConfig("tracks/$id.trck").get().set("nodes.${node.id}.z", node.z)
            fm.getConfig("tracks/$id.trck").get().set("nodes.${node.id}.strict", node.strict)
            if(node.connectedTo != null) fm.getConfig("tracks/$id.trck").get().set("nodes.${node.id}.connectedTo", node.connectedTo!!.id)
        }
        fm.saveConfig("tracks/$id.trck")

        // Saving roll nodes
        for((index, rollNode) in rollNodes.withIndex()) {
            fm.getConfig("tracks/$id.trck").get().set("rollNodes.$index.nodeId", rollNode.position.nodePosition.id)
            fm.getConfig("tracks/$id.trck").get().set("rollNodes.$index.position", rollNode.position.position.toInt())
            fm.getConfig("tracks/$id.trck").get().set("rollNodes.$index.roll", rollNode.roll)
        }
        fm.saveConfig("tracks/$id.trck")

        // TODO Save segment separators, track triggers
    }
}