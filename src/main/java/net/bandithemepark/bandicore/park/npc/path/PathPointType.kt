package net.bandithemepark.bandicore.park.npc.path

import org.bukkit.Color

enum class PathPointType(val precalculate: Boolean, val color: Color) {
    DEFAULT(false, Color.YELLOW),
    ATTRACTION_QUEUE(true, Color.AQUA),
    ATTRACTION_STATION(false, Color.LIME);

    fun next(): PathPointType {
        return if (this.ordinal == PathPointType.values().size - 1)
            PathPointType.values()[0]
        else
            PathPointType.values()[this.ordinal + 1]
    }
}