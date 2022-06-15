package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.switch

import net.bandithemepark.bandicore.park.attractions.tracks.TrackLayout
import net.bandithemepark.bandicore.park.attractions.tracks.TrackNode
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentSeparator
import net.bandithemepark.bandicore.park.attractions.tracks.splines.BezierSpline
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.TrackUtil
import net.bandithemepark.bandicore.util.entity.armorstand.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.math.MathUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.util.Vector

class LogFlumeSwitch(val layout: TrackLayout, val segment: SegmentSeparator, val storageConnector: TrackNode, val switchPart1: TrackNode, val switchPart2: TrackNode, val switchConnector1: TrackNode, val switchConnector2: TrackNode, val part1Start: Vector, val part1End: Vector, val part2Start: Vector, val part2End: Vector) {
    val center: Vector
    val radius: Double
    val firstNodeStartRotation: Double
    val firstNodeEndRotation: Double
    val secondNodeStartRotation: Double
    val secondNodeEndRotation: Double

    init {
        // Prepare certain variables
        center = Vector((switchPart1.x + switchPart2.x)/2.0, (switchPart1.y + switchPart2.y)/2.0, (switchPart1.z + switchPart2.z)/2.0)
        radius = switchPart1.asVector().clone().subtract(center).length()
        secondNodeStartRotation = 90.0
        secondNodeEndRotation = 180.0
        firstNodeStartRotation = -90.0
        firstNodeEndRotation = 0.0
    }

    val armorStand: PacketEntityArmorStand = PacketEntityArmorStand()
    val armorStandYOffset = -1.0 - (11.0 / 16.0)

    fun spawnModel() {
        val position = part1Start.clone().add(part2Start).multiply(0.5).add(Vector(0.0, armorStandYOffset, 0.0)).add(layout.origin)

        armorStand.spawn(position.toLocation(layout.world, 90.0F, 0.0F))
        armorStand.handle!!.isInvisible = true
        armorStand.updateMetadata()
        armorStand.helmet = ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(11).build()
    }

    fun setToStart() {
        switchPart1.x = part1Start.x
        switchPart1.y = part1Start.y
        switchPart1.z = part1Start.z

        switchPart2.x = part2Start.x
        switchPart2.y = part2Start.y
        switchPart2.z = part2Start.z

        switchPart2.connectTo(layout, switchConnector1)
        storageConnector.connectTo(layout, switchPart1)

        updatePathAndSegment()
    }

    fun prepareForwards() {
        storageConnector.disconnect(layout)
        switchPart2.disconnect(layout)
        updatePathAndSegment()
    }

    fun setToEnd() {
        switchPart1.x = part1End.x
        switchPart1.y = part1End.y
        switchPart1.z = part1End.z

        switchPart2.x = part2End.x
        switchPart2.y = part2End.y
        switchPart2.z = part2End.z

        switchPart2.connectTo(layout, switchConnector2)

        updatePathAndSegment()
    }

    fun prepareBackwards() {
        switchPart2.disconnect(layout)
        updatePathAndSegment()
    }

    fun moveTo(progress: Double) {
        val t = MathUtil.cosineInterpolation(progress, 0.0, 1.0)
        val firstNodeRotation = BezierSpline().linear(firstNodeStartRotation, firstNodeEndRotation, t)
        val secondNodeRotation = BezierSpline().linear(secondNodeStartRotation, secondNodeEndRotation, t)

        val firstNodeLocation = MathUtil.getPointOnCircleXZ(radius, firstNodeRotation).add(center)
        switchPart1.x = firstNodeLocation.x
        switchPart1.y = firstNodeLocation.y
        switchPart1.z = firstNodeLocation.z

        val secondNodeLocation = MathUtil.getPointOnCircleXZ(radius, secondNodeRotation).add(center)
        switchPart2.x = secondNodeLocation.x
        switchPart2.y = secondNodeLocation.y
        switchPart2.z = secondNodeLocation.z

        updatePathAndSegment()

        val position = center.clone().add(Vector(0.0, armorStandYOffset, 0.0)).add(layout.origin)
        armorStand.teleport(position.toLocation(layout.world, (-firstNodeRotation + 0.0).toFloat(), 0.0F))
    }

    fun updatePathAndSegment() {
        TrackUtil.updateNodePath(layout, switchPart1)
        segment.curve = switchPart1.curve.toList()
    }
}