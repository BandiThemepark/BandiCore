package net.bandithemepark.bandicore.park.attractions.tracks

import net.bandithemepark.bandicore.util.TrackUtil
import org.bukkit.util.Vector

class TrackNode(var id: String?, var x: Double, var y: Double, var z: Double, var strict: Boolean) {
    var roll = 0.0
    var connectedTo: TrackNode? = null
    var curve = mutableListOf<TrackNode>()
    var connectedToId: String? = null

    constructor(id: String?, x: Double, y: Double, z: Double, strict: Boolean, connectedToId: String?) : this(id, x, y, z, strict) {
        this.connectedToId = connectedToId
    }

    constructor(x: Double, y: Double, z: Double) : this(null, x, y, z, false)

    /**
     * Returns the node as a Bukkit Vector
     * @return Bukkit Vector instance
     */
    fun asVector(): Vector {
        return Vector(x, y, z)
    }

    /**
     * Converts a vector to a TrackNode
     * @return TrackNode instance
     */
    fun Vector.toNode(): TrackNode {
        return TrackNode(this.x, this.y, this.z)
    }

    /**
     * Updates this node and it's neighbour. Use if it has been moved
     * @param layout Layout that the node is on
     */
    fun updateMovedNode(layout: TrackLayout) {
        TrackUtil.updateMovedNode(layout, this)
    }

    /**
     * Connects this node to another node
     * @param layout Layout that this node is on
     * @param to Node to connect to
     */
    fun connectTo(layout: TrackLayout, to: TrackNode) {
        TrackUtil.connectTo(layout, this, to)
    }

    /**
     * Disconnects this node from the one it is connected to
     * @param layout The layout that this node is on
     */
    fun disconnect(layout: TrackLayout) {
        TrackUtil.disconnect(layout, this)
    }

    /**
     * Retrieves the node that is connected to this node (before it)
     * @param layout The layout that this node is on
     * @return The node that is connected to this node, null if none is found
     */
    fun getBefore(layout: TrackLayout): TrackNode? {
        return TrackUtil.getNodeBeforeNode(layout, this)
    }
}