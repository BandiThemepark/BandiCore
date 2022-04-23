package net.bandithemepark.bandicore.park.attractions.tracks.splines

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.TrackNode
import net.bandithemepark.bandicore.util.TrackUtil
import net.bandithemepark.bandicore.util.math.MathUtil

class BezierSpline: SplineType() {


    // NOTE FOR HECKI!!!!!!!!!!!!!!
    // I'm not even going to try to explain this. This is just the magic of the Bezier curve.


    override fun interpolate(before: TrackNode?, node: TrackNode, connectedTo: TrackNode?, connectedToConnectedTo: TrackNode?): MutableList<TrackNode> {
        return if(before == null) {
            if(connectedToConnectedTo == null) {
                finalLinear(node, connectedTo)
            } else {
                if(!connectedTo!!.strict) {
                    val halfNode = getHalfwayNode(connectedTo, connectedToConnectedTo)
                    finalCubic(node, connectedTo, halfNode)
                } else {
                    finalLinear(node, connectedTo)
                }
            }
        } else {
            if(!node.strict) {
                if(connectedTo!!.strict) {
                    val halfNode = getHalfwayNode(node, connectedTo)
                    finalLinear(halfNode, connectedTo)
                } else {
                    if (connectedToConnectedTo == null) {
                        val halfNode = getHalfwayNode(node, connectedTo)
                        finalLinear(halfNode, connectedTo)
                    } else {
                        val halfNode1: TrackNode = getHalfwayNode(node, connectedTo)
                        val halfNode2: TrackNode = getHalfwayNode(connectedTo, connectedToConnectedTo)
                        finalCubic(halfNode1, connectedTo, halfNode2)
                    }
                }
            } else {
                if(connectedToConnectedTo == null) {
                    finalLinear(node, connectedTo)
                } else {
                    if(!connectedTo!!.strict) {
                        val halfNode = getHalfwayNode(connectedTo, connectedToConnectedTo)
                        finalCubic(node, connectedTo, halfNode)
                    } else {
                        finalLinear(node, connectedTo)
                    }
                }
            }
        }
    }

    // Utilities
    private fun getHalfwayNode(p0: TrackNode?, p1: TrackNode?): TrackNode {
        val x = linear(p0!!.x, p1!!.x, 0.5)
        val y = linear(p0.y, p1.y, 0.5)
        val z = linear(p0.z, p1.z, 0.5)
        return TrackNode(null, x, y, z, false)
    }

    fun linear(p0: Double, p1: Double, t: Double): Double {
        return p0 + (p1 - p0) * t
    }

    fun finalLinear(p0: TrackNode, p1: TrackNode?): MutableList<TrackNode> {
        val curvePoints: MutableList<TrackNode> = ArrayList()
        val length: Double = MathUtil.getDistanceBetween(p0.asVector(), p1!!.asVector())
        val amountOfPoints: Double = length * BandiCore.instance.trackManager.pointsPerMeter
        val step = 1.0 / amountOfPoints
        var i = 0
        while (i < amountOfPoints) {
            val x = linear(p0.x, p1.x, i.toDouble() * step)
            val y = linear(p0.y, p1.y, i.toDouble() * step)
            val z = linear(p0.z, p1.z, i.toDouble() * step)
            curvePoints.add(TrackNode(null, x, y, z, false))
            i++
        }

        // FILTERING THE CURVE POINTS TO AVOID DUPLICATES
        val finalPoints: MutableList<TrackNode> = mutableListOf()
        curvePoints.forEach {
            if(!finalPoints.contains(it)) {
                finalPoints.add(it)
            }
        }
        return finalPoints
    }

    fun finalCubic(p0: TrackNode?, p1: TrackNode?, p2: TrackNode): MutableList<TrackNode> {
        val lengthPoints: ArrayList<TrackNode> = ArrayList()
        val step = 1.0 / 50.0
        for (i in 0..49) {
            val x = cubic(p0!!.x, p1!!.x, p2.x, i.toDouble() * step)
            val y = cubic(p0.y, p1.y, p2.y, i.toDouble() * step)
            val z = cubic(p0.z, p1.z, p2.z, i.toDouble() * step)
            lengthPoints.add(TrackNode(null, x, y, z, false))
        }
        val arcLength: Double = TrackUtil.getLength(lengthPoints)
        val finalPointCount: Double = arcLength * BandiCore.instance.trackManager.pointsPerMeter

        // ONTO THE ACTUAL POINT LENGTHS
        val arcLengthPoints: ArrayList<TrackNode> = ArrayList()
        val step2 = 1.0 / finalPointCount
        run {
            var i = 0
            while (i < finalPointCount) {
                val x = cubic(p0!!.x, p1!!.x, p2.x, i.toDouble() * step2)
                val y = cubic(p0.y, p1.y, p2.y, i.toDouble() * step2)
                val z = cubic(p0.z, p1.z, p2.z, i.toDouble() * step2)
                arcLengthPoints.add(TrackNode(null, x, y, z, false))
                i++
            }
        }

        // GETTING THE LIST WITH ALL CURRENT LENGTHS
        val arcLengths = ArrayList<Double>()
        var currentLength = 0.0
        for (i in 0 until arcLengthPoints.size - 1) {
            val from: TrackNode = arcLengthPoints[i]
            val to: TrackNode = arcLengthPoints[i + 1]
            arcLengths.add(currentLength)
            currentLength += MathUtil.getDistanceBetween(from.asVector(), to.asVector())
        }

        // GETTING THE FINAL LIST
        val curvePoints = mutableListOf<TrackNode>()
        val step3 = 1.0 / finalPointCount
        var i = 0
        while (i < finalPointCount) {
            val t = getParameterizedT(i * step3, arcLengths)
            val x = cubic(p0!!.x, p1!!.x, p2.x, t)
            val y = cubic(p0.y, p1.y, p2.y, t)
            val z = cubic(p0.z, p1.z, p2.z, t)
            curvePoints.add(TrackNode(null, x, y, z, false))
            i++
        }
        return curvePoints
    }

    private fun getParameterizedT(beforeT: Double, arcLengths: ArrayList<Double>): Double {
        val t: Double
        val targetArcLength = beforeT * arcLengths[arcLengths.size - 1]
        val index = indexOfLargestValueSmallerThan(arcLengths, targetArcLength)
        t = if (arcLengths[index] == targetArcLength) {
            index / (arcLengths.size - 1).toDouble()
        } else {
            val lengthBefore = arcLengths[index]
            val lengthAfter = arcLengths[index + 1]
            val segmentLength = lengthAfter - lengthBefore
            val segmentFraction = (targetArcLength - lengthBefore) / segmentLength
            (index + segmentFraction) / (arcLengths.size + 1).toDouble()
        }
        return t
    }

    private fun indexOfLargestValueSmallerThan(arcLengths: ArrayList<Double>, targetArcLength: Double): Int {
        var index = 0
        var largest = 0.0
        for (i in arcLengths.indices) {
            if (arcLengths[i] > largest && arcLengths[i] < targetArcLength) {
                largest = arcLengths[i]
                index = i
            }
        }
        return index
    }

    private fun cubic(p0: Double, p1: Double, p2: Double, t: Double): Double {
        val pt01 = linear(p0, p1, t)
        val pt12 = linear(p1, p2, t)
        return linear(pt01, pt12, t)
    }
}