package net.bandithemepark.bandicore.park.attractions.tracks

class TrackPosition(var nodePosition: TrackNode, position: Int): Cloneable {
    var position = position.toDouble()

    /**
     * Moves the track position by the given amount.
     * @param layout The TrackLayout this position is on
     * @param amount The amount to move the track position by. If you want to go backwards, make it negative
     * @throws IllegalStateException if there is no more track left
     */
    fun move(layout: TrackLayout, amount: Double) {
        var currentNode = nodePosition
        var newPosition = position + amount

        // Moving forward
        while(newPosition >= currentNode.curve.size) {
            if(currentNode.connectedTo != null) {
                newPosition -= currentNode.curve.size
                currentNode = currentNode.connectedTo!!
            } else {
                throw IllegalStateException("Cannot move forward, no more track available")
            }
        }

        // Moving backwards (just like humanity)
        while(newPosition < 0) {
            if(currentNode.getBefore(layout) != null) {
                currentNode = currentNode.getBefore(layout)!!
                newPosition += currentNode.curve.size
            } else {
                throw IllegalStateException("Cannot move backwards, no more track available")
            }
        }

        nodePosition = currentNode
        position = newPosition
    }

    /**
     * Gets the path point this TrackPosition represents
     * @return The path point this TrackPosition represents
     */
    fun getPathPoint(): TrackNode {
        return nodePosition.curve[position.toInt()]
    }

    /**
     * Gets all nodes between this TrackPosition and the given TrackPosition
     * @param other The other boundary TrackPosition
     * @return A list of all nodes between this TrackPosition and the given TrackPosition
     */
    fun getNodesBetween(other: TrackPosition): List<TrackNode> {
        val nodes = mutableListOf<TrackNode>(nodePosition)

        var currentNode = nodePosition
        while(currentNode.connectedTo != null) {
            currentNode = currentNode.connectedTo!!
            nodes.add(currentNode)

            if(currentNode == other.nodePosition) break
        }

        return nodes
    }

    /**
     * Gets the distance in curve points to the next position
     * @param other The other boundary TrackPosition
     * @return The distance in curve points to the next position
     */
    fun getDistanceTo(other: TrackPosition): Double {
        if(nodePosition == other.nodePosition) return other.position - position

        var currentNode = nodePosition
        var distance = currentNode.curve.size - position

        while(currentNode.connectedTo != null) {
            currentNode = currentNode.connectedTo!!

            if(currentNode == other.nodePosition) {
                distance += other.position
                break
            }

            distance += currentNode.curve.size
        }

        return distance
    }

    public override fun clone(): TrackPosition {
        return TrackPosition(nodePosition, position.toInt())
    }
}