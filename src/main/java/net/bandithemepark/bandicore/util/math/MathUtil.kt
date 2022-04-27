package net.bandithemepark.bandicore.util.math

import net.bandithemepark.bandicore.park.attractions.tracks.splines.BezierSpline
import org.bukkit.util.Vector
import kotlin.math.*


object MathUtil {
//    fun rotateAroundPoint(x: Double, y: Double, z: Double, pitch: Double, yaw: Double, roll: Double): Vector {
//        val offsetMatrix = Matrix(3, 1)
//        val rotationMatrix = Matrix.fromAngles(pitch, yaw, roll)
//
//        offsetMatrix.m[0][0] = x
//        offsetMatrix.m[1][0] = y
//        offsetMatrix.m[2][0] = z
//
//        val newMatrix = Matrix.multiply(rotationMatrix, offsetMatrix)
//        return Vector(newMatrix.m[0][0], newMatrix.m[1][0], newMatrix.m[2][0])
//    }

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
     * Interpolates between two angles
     * @param a1 The first angle
     * @param a2 The second angle
     * @param t The interpolation value (ranging from 0 to 1)
     * @return Interpolated angle
     */
    fun interpolateAngles(a1: Double, a2: Double, t: Double): Double {
        val difBetween = a2 - a1
        if(difBetween > 180.0 || difBetween < -180.0) {
            val delta = (a2 - a1 + 360 + 180) % 360 - 180
            return (a1 + delta * t + 360) % 360
        } else {
            return BezierSpline().linear(a1, a2, t)
        }
    }

//    private fun lerp(start: Double, end: Double, t: Double): Double {
//        return (1-t)*start+t*end
//    }
//
//    private fun clamp(number: Double, min: Double, max: Double): Double {
//        return number.coerceAtLeast(min).coerceAtMost(max)
//    }
//
//    private fun repeat(t: Double, m: Double): Double {
//        return clamp(t - floor(t / m) * m, 0.0, m)
//    }
//
//    fun interpolateAngles(a1: Double, a2: Double, t: Double): Double {
//        val dt = repeat(a2 - a1, 360.0)
//        val stuff = if(dt > 180) {
//            dt - 360
//        } else {
//            dt
//        }
//        return lerp(a1, a1 + stuff, t)
//    }
}