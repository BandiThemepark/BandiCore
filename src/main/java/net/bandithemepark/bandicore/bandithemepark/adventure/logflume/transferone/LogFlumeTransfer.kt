package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.transferone

import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.park.attractions.tracks.TrackNode
import net.bandithemepark.bandicore.park.attractions.tracks.splines.BezierSpline
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.TrackUtil
import net.bandithemepark.bandicore.util.entity.armorstand.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.math.MathUtil
import org.bukkit.Material
import org.bukkit.util.Vector

class LogFlumeTransfer(val layout: TrackLayout, val connector1: TrackNode, val connector2: TrackNode, val part1: TrackNode, val part2: TrackNode, val part1Start: Vector, val part1End: Vector, val part2Start: Vector, val part2End: Vector) {
    val armorStand: PacketEntityArmorStand = PacketEntityArmorStand()
    val armorStandYOffset = -1.0 - (11.0 / 16.0)

    fun spawnModel() {
        val position = part1Start.clone().add(part2Start).multiply(0.5).add(Vector(0.0, armorStandYOffset, 0.0)).add(layout.origin)

        armorStand.spawn(position.toLocation(layout.world, -90.0F, 0.0F))
        armorStand.handle!!.isInvisible = true
        armorStand.updateMetadata()
        armorStand.helmet = ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(11).build()
    }

    fun setToStart() {
        if(connector2.connectedTo != null) connector2.disconnect(layout)

        part1.x = part1Start.x
        part1.y = part1Start.y
        part1.z = part1Start.z

        part2.x = part2Start.x
        part2.y = part2Start.y
        part2.z = part2Start.z

        moveTo(0.0)

        updatePath()
        connector1.connectTo(layout, part1)
        layout.updateSegments()
    }

    fun prepareForward() {
        connector1.disconnect(layout)
        updatePath()
        layout.updateSegments()
    }

    fun setToEnd() {
        if(connector1.connectedTo != null) connector1.disconnect(layout)

        part1.x = part1End.x
        part1.y = part1End.y
        part1.z = part1End.z

        part2.x = part2End.x
        part2.y = part2End.y
        part2.z = part2End.z

        updatePath()
        connector2.connectTo(layout, part1)
        layout.updateSegments()
    }

    fun prepareBackward() {
        connector2.disconnect(layout)
        updatePath()
        layout.updateSegments()
    }

    fun moveTo(progress: Double) {
        val t = MathUtil.cosineInterpolation(progress, 0.0, 1.0)
        part1.x = BezierSpline().linear(part1Start.x, part1End.x, t)
        part1.y = BezierSpline().linear(part1Start.y, part1End.y, t)
        part1.z = BezierSpline().linear(part1Start.z, part1End.z, t)

        part2.x = BezierSpline().linear(part2Start.x, part2End.x, t)
        part2.y = BezierSpline().linear(part2Start.y, part2End.y, t)
        part2.z = BezierSpline().linear(part2Start.z, part2End.z, t)

        val averageX = (part1.x + part2.x) / 2.0
        val averageY = (part1.y + part2.y) / 2.0
        val averageZ = (part1.z + part2.z) / 2.0
        val position = Vector(averageX, averageY+armorStandYOffset, averageZ).add(layout.origin)
        armorStand.teleport(position.toLocation(layout.world, -90.0F, 0.0F))

        updatePath()
        updateBetter(BezierSpline().linear(part1Start.z, part1End.z, t))
    }

    private fun updateBetter(z: Double) {
        part1.curve.forEach { it.z = z }
    }

    private fun updatePath() {

//        TrackUtil.updateNodePath(layout, part1)
//        layout.updateSegments()
    }
}