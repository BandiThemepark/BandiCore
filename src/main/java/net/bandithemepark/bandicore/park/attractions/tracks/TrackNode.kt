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

    fun asVector(): Vector {
        return Vector(x, y, z)
    }

    fun Vector.toNode(): TrackNode {
        return TrackNode(this.x, this.y, this.z)
    }

    fun updateMovedNode(layout: TrackLayout) {
        TrackUtil.updateMovedNode(layout, this)
    }

    fun connectTo(layout: TrackLayout, to: TrackNode) {
        TrackUtil.connectTo(layout, this, to)
    }

    fun disconnect(layout: TrackLayout) {
        TrackUtil.disconnect(layout, this)
    }

    fun getBefore(layout: TrackLayout): TrackNode? {
        return TrackUtil.getNodeBeforeNode(layout, this)
    }
}