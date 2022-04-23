package net.bandithemepark.bandicore.park.attractions.tracks

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.TrackUtil
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

    private fun updateRoll() {
        // TODO update roll nodes
    }

    fun updateMovedNode(node: TrackNode) {
        TrackUtil.updateMovedNode(this, node)
    }

    fun connectTo(from: TrackNode, to: TrackNode) {
        TrackUtil.connectTo(this, from, to)
    }

    fun disconnect(node: TrackNode) {
        TrackUtil.disconnect(this, node)
    }

    fun getNearestNode(location: Location): TrackNode? {
        return TrackUtil.getNearestNode(this, location)
    }

    fun getBefore(node: TrackNode): TrackNode? {
        return TrackUtil.getNodeBeforeNode(this, node)
    }

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
            fm.getConfig("tracks/$id.trck").get().set("rollNodes.$index.node", rollNode.nodePosition.id)
            fm.getConfig("tracks/$id.trck").get().set("rollNodes.$index.position", rollNode.position)
            fm.getConfig("tracks/$id.trck").get().set("rollNodes.$index.roll", rollNode.roll)
        }
        fm.saveConfig("tracks/$id.trck")

        // TODO Save segment separators, track triggers
    }
}