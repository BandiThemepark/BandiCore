package net.bandithemepark.bandicore.util.math

import org.bukkit.util.Vector
import org.joml.Math.clamp
import kotlin.math.*

object MathUtil {

    /**
     * Rotates an offset around a certain point
     * @param rotation The rotation to apply as a Quaternion
     * @param x The x offset
     * @param y The y offset
     * @param z The z offset
     * @return The rotated offset
     */
    fun rotateAroundPoint(rotation: Quaternion, x: Double, y: Double, z: Double): Vector {
        val rotationMatrix = rotation.toMatrix()

        val coordinateMatrix = Matrix(4, 1)
        coordinateMatrix.m[0][0] = x
        coordinateMatrix.m[1][0] = y
        coordinateMatrix.m[2][0] = z
        coordinateMatrix.m[3][0] = 1.0

        val newMatrix = Matrix.multiply(rotationMatrix, coordinateMatrix)
        return Vector(newMatrix.m[0][0], newMatrix.m[1][0], newMatrix.m[2][0])
    }

    /**
     * Gives the distance between two vectors
     * @param p0 The first vector
     * @param p1 The second vector
     * @return The distance between the two vectors
     */
    fun getDistanceBetween(p0: Vector, p1: Vector): Double {
        val x = p1.x - p0.x
        val y = p1.y - p0.y
        val z = p1.z - p0.z
        val one = sqrt(x * x + z * z)
        return sqrt(one * one + y * y)
    }

    /**
     * IDK what this does, it's also witchery to me. Used in the Quaternion class
     */
    fun getNormalizationFactor(x: Double, y: Double, z: Double, w: Double): Double {
        return getNormalizationFactorLS(x * x + y * y + z * z + w * w)
    }

    private fun getNormalizationFactorLS(lengthSquared: Double): Double {
        // https://stackoverflow.com/a/12934750
        return if (abs(1.0 - lengthSquared) < 2.107342e-08) {
            2.0 / (1.0 + lengthSquared)
        } else {
            1.0 / sqrt(lengthSquared)
        }
    }

    /**
     * Converts a Quaternion to an armor stand pose
     * @param rotation The Quaternion to convert
     * @return Vector with x, y and z rotations in degrees
     */
    fun getArmorStandPose(rotation: Quaternion): Vector {
        val qx = rotation.x
        val qy = rotation.y
        val qz = rotation.z
        val qw = rotation.w
        val rx = 1.0 + 2.0 * (-qy * qy - qz * qz)
        val ry = 2.0 * (qx * qy + qz * qw)
        val rz = 2.0 * (qx * qz - qy * qw)
        val uz = 2.0 * (qy * qz + qx * qw)
        val fz = 1.0 + 2.0 * (-qx * qx - qy * qy)
        return if (abs(rz) < 1.0 - 1E-15) {
            // Standard calculation
            Vector(atan2(uz, fz), asin(rz), atan2(-ry, rx))
        } else {
            // At the -90 or 90 degree angle singularity
            val sign = if (rz < 0) -1.0 else 1.0
            Vector(0.0, sign * 90.0, -sign * 2.0 * atan2(qx, qw))
        }
    }

    /**
     * Interpolates between two angles, with a boundary of -180 to 180
     * @param a1 The first angle
     * @param a2 The second angle
     * @param t The interpolation value (ranging from 0 to 1)
     * @return Interpolated angle
     */
    fun interpolateAngles(a1: Double, a2: Double, t: Double): Double {
        val aNorm = normalize(a1)
        val bNorm = normalize(a2)

        // Calculate the shortest difference
        var delta = bNorm - aNorm
        if (delta > 180) delta -= 360
        if (delta < -180) delta += 360

        // Interpolate
        val result = aNorm + t * delta
        return normalize(result)
    }

    /**
     * Normalizes an angle to the range [-180, 180)
     * @param angle The angle to normalize
     * @return The normalized angle
     */
    private fun normalize(angle: Double): Double {
        var normalized = angle % 360
        if (normalized >= 180) normalized -= 360
        if (normalized < -180) normalized += 360
        return normalized
    }

    /**
     * Gets a point on a circle
     * @param radius The radius of the circle
     * @param rotation The rotation of the point you want to use. In degrees
     * @return The point on the circle as a vector
     */
    fun getPointOnCircleXZ(radius: Double, rotation: Double): Vector {
        return Vector(sin(Math.toRadians(rotation)) * radius, 0.0, cos(Math.toRadians(rotation)) * radius)
    }

    /**
     * Elias wants this really badly
     */
    fun cosineInterpolation(t: Double, from: Double, to: Double): Double {
        val t2 = (1 - cos(t * Math.PI)) / 2
        return from * (1 - t2) + to * t2
    }

    /**
     * Interpolates between two numbers linearly
     * @param from The first number
     * @param to The second number
     * @param t The interpolation value (ranging from 0 to 1)
     */
    fun lerp(from: Double, to: Double, t: Double): Double {
        return from + (to - from) * t
    }

    /**
     * Interpolates between two numbers using easeOutBounce interpolation.
     * Interpolation formula is from https://easings.net/
     * @param from The first number
     * @param to The second number
     * @param t The interpolation value (ranging from 0 to 1)
     * @return The interpolated number
     */
    fun easeOutBounceInterpolation(from: Double, to: Double, t: Double): Double {
        return lerp(from, to, easeOutBounce(t))
    }

    private fun easeOutBounce(t: Double): Double {
        val n1 = 7.5625
        val d1 = 2.75

        return if (t < 1 / d1) {
            n1 * t * t
        } else if (t < 2 / d1) {
            n1 * (t - 1.5 / d1) * (t - 1.5 / d1 + 0.75) + 0.75
        } else if (t < 2.5 / d1) {
            n1 * (t - 2.25 / d1) * (t - 2.25 / d1 + 0.9375) + 0.9375
        } else {
            n1 * (t - 2.625 / d1) * (t - 2.625 / d1 + 0.984375) + 0.984375
        }
    }
}