package net.bandithemepark.bandicore.util.math

import org.bukkit.util.Vector
import org.joml.Quaternionf
import org.joml.Quaternionfc
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.withSign

class Quaternion(var x: Double, var y: Double, var z: Double, var w: Double): Cloneable {
    constructor() : this(0.0, 0.0, 0.0, 1.0)

    init {
        normalize()
    }

    fun divide(quat: Quaternion) {
        val x = w * -quat.x + x * quat.w + y * -quat.z - z * -quat.y
        val y = w * -quat.y + y * quat.w + z * -quat.x - this.x * -quat.z
        val z = w * -quat.z + z * quat.w + this.x * -quat.y - this.y * -quat.x
        val w = w * quat.w - this.x * -quat.x - this.y * -quat.y - this.z * -quat.z
        this.x = x
        this.y = y
        this.z = z
        this.w = w
        this.normalize()
    }

    fun multiply(quat: Quaternion) {
        val x = w * quat.x + x * quat.w + y * quat.z - z * quat.y
        val y = w * quat.y + y * quat.w + z * quat.x - this.x * quat.z
        val z = w * quat.z + z * quat.w + this.x * quat.y - this.y * quat.x
        val w = w * quat.w - this.x * quat.x - this.y * quat.y - this.z * quat.z
        this.x = x
        this.y = y
        this.z = z
        this.w = w
        this.normalize()
    }

    fun rotateYawPitchRoll(pitch: Double, yaw: Double, roll: Double) {
        this.rotateY(-yaw)
        this.rotateX(pitch)
        this.rotateZ(roll)
    }

    fun getYawPitchRoll(): Vector {
        return getYawPitchRoll(x, y, z, w)
    }

    fun getPitch(): Double {
        return getPitch(x, y, z, w)
    }

    fun getYaw(): Double {
        return getYaw(x, y, z, w)
    }

    fun getRoll(): Double {
        return getRoll(x, y, z, w)
    }

    fun getYaw(x: Double, y: Double, z: Double, w: Double): Double {
        val test = 2.0 * (w * x - y * z)
        return if (Math.abs(test) < 1.0 - 1E-15) {
            var yaw: Double = atan2(-2.0 * (w * y + z * x), 1.0 - 2.0 * (x * x + y * y))
            val roll_x = 0.5 - (x * x + z * z)
            if (roll_x <= 0.0 && Math.abs(w * z + x * y) > roll_x) {
                yaw += if (yaw < 0.0) Math.PI else -Math.PI
            }
            Math.toDegrees(yaw)
        } else if (test < 0.0) {
            Math.toDegrees(-2.0 * atan2(z, w))
        } else {
            Math.toDegrees(2.0 * atan2(z, w))
        }
    }

    fun getPitch(x: Double, y: Double, z: Double, w: Double): Double {
        val test = 2.0 * (w * x - y * z)
        return if (Math.abs(test) < 1.0 - 1E-15) {
            var pitch = Math.asin(test)
            val roll_x = 0.5 - (x * x + z * z)
            if (roll_x <= 0.0 && Math.abs(w * z + x * y) > roll_x) {
                pitch = -pitch
                pitch += if (pitch < 0.0) Math.PI else -Math.PI
            }
            Math.toDegrees(pitch)
        } else if (test < 0.0) {
            -90.0
        } else {
            90.0
        }
    }

    fun getRoll(x: Double, y: Double, z: Double, w: Double): Double {
        val test = 2.0 * (w * x - y * z)
        return if (abs(test) < 1.0 - 1E-15) {
            var roll: Double = atan2(2.0 * (w * z + x * y), 1.0 - 2.0 * (x * x + z * z))
            if (abs(roll) > 0.5 * Math.PI) {
                roll += if (roll < 0.0) Math.PI else -Math.PI
            }
            Math.toDegrees(roll)
        } else {
            0.0
        }
    }

    fun rotateX(angleDegrees: Double) {
        if (angleDegrees != 0.0) {
            val r = 0.5 * Math.toRadians(angleDegrees)
            rotateX_unsafe(Math.cos(r), Math.sin(r))
        }
    }

    private fun rotateX_unsafe(fy: Double, fz: Double) {
        val x = x * fy + w * fz
        val y = y * fy + z * fz
        val z = z * fy - this.y * fz
        val w = w * fy - this.x * fz
        this.x = x
        this.y = y
        this.z = z
        this.w = w
        this.normalize()
    }

    fun rotateY(angleDegrees: Double) {
        if (angleDegrees != 0.0) {
            val r = 0.5 * Math.toRadians(angleDegrees)
            rotateY_unsafe(Math.cos(r), Math.sin(r))
        }
    }

    private fun rotateY_unsafe(fx: Double, fz: Double) {
        val x = x * fx - z * fz
        val y = y * fx + w * fz
        val z = z * fx + this.x * fz
        val w = w * fx - this.y * fz
        this.x = x
        this.y = y
        this.z = z
        this.w = w
        this.normalize()
    }

    fun rotateZ(angleDegrees: Double) {
        if (angleDegrees != 0.0) {
            val r = 0.5 * Math.toRadians(angleDegrees)
            rotateZ_unsafe(Math.cos(r), Math.sin(r))
        }
    }

    private fun rotateZ_unsafe(fx: Double, fy: Double) {
        val x = x * fx + y * fy
        val y = y * fx - this.x * fy
        val z = z * fx + w * fy
        val w = w * fx - this.z * fy
        this.x = x
        this.y = y
        this.z = z
        this.w = w
        this.normalize()
    }

    fun toMatrix(): Matrix {
        val matrix = Matrix(4, 4)

        matrix.m[0][0] = 1.0 - 2.0 * y * y - 2.0 * z * z
        matrix.m[0][1] = 2.0 * x * y - 2.0 * z * w
        matrix.m[0][2] = 2.0 * x * z + 2.0 * y * w
        matrix.m[0][3] = 0.0

        matrix.m[1][0] = 2.0 * x * y + 2.0 * z * w
        matrix.m[1][1] = 1.0 - 2.0 * x * x - 2.0 * z * z
        matrix.m[1][2] = 2.0 * y * z - 2.0 * x * w
        matrix.m[1][3] = 0.0

        matrix.m[2][0] = 2.0 * x * z - 2.0 * y * w
        matrix.m[2][1] = 2.0 * y * z + 2.0 * x * w
        matrix.m[2][2] = 1.0 - 2.0 * x * x - 2.0 * y * y
        matrix.m[2][3] = 0.0

        matrix.m[3][0] = 0.0
        matrix.m[3][1] = 0.0
        matrix.m[3][2] = 0.0
        matrix.m[3][3] = 1.0

        return matrix
    }

    private fun normalize() {
        val f: Double = MathUtil.getNormalizationFactor(x, y, z, w)
        x *= f
        y *= f
        z *= f
        w *= f
    }

    fun forwardVector(): Vector {
        return Vector(2.0 * (x * z + y * w), 2.0 * (y * z - x * w), 1.0 + 2.0 * (-x * x - y * y))
    }

    fun upVector(): Vector {
        return Vector(2.0 * (x * y - z * w), 1.0 + 2.0 * (-x * x - z * z), 2.0 * (y * z + x * w))
    }

    public override fun clone(): Quaternion {
        return super.clone() as Quaternion
    }

    fun toBukkitQuaternion(): Quaternionf {
        return Quaternionf(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
    }

    companion object {
        fun getYawPitchRoll(x: Double, y: Double, z: Double, w: Double): Vector {
            val test = 2.0 * (w * x - y * z)
            return if (Math.abs(test) < 1.0 - 1E-15) {
                // Standard angle
                var roll: Double = atan2(2.0 * (w * z + x * y), 1.0 - 2.0 * (x * x + z * z))
                var pitch = Math.asin(test)
                var yaw: Double = atan2(-2.0 * (w * y + z * x), 1.0 - 2.0 * (x * x + y * y))

                // This means the following:
                // roll = Math.atan2(rightVector.getY(), upVector.getY());
                // pitch = Math.asin(-forwardVector.getY());
                // yaw = Math.atan2(forwardVector.getX(), forwardVector.getZ());

                // Reduce roll if it is > 90.0 degrees
                // This can be done thanks to the otherwise annoying 'gymbal lock' effect
                // We can rotate yaw and roll with 180 degrees, and invert pitch to adjust
                // This results in the equivalent rotation
                if (Math.abs(roll) > 0.5 * Math.PI) {
                    roll += if (roll < 0.0) Math.PI else -Math.PI
                    yaw += if (yaw < 0.0) Math.PI else -Math.PI
                    pitch = -pitch
                    pitch += if (pitch < 0.0) Math.PI else -Math.PI
                }
                Vector(Math.toDegrees(pitch), Math.toDegrees(yaw), Math.toDegrees(roll))
            } else if (test < 0.0) {
                // This is at the pitch=-90.0 singularity
                // All we can do is yaw (or roll) around the vertical axis
                Vector(-90.0, Math.toDegrees(-2.0 * atan2(z, w)), 0.0)
            } else {
                // This is at the pitch=90.0 singularity
                // All we can do is yaw (or roll) around the vertical axis
                Vector(90.0, Math.toDegrees(2.0 * atan2(z, w)), 0.0)
            }
        }

        fun fromYawPitchRoll(pitch: Double, yaw: Double, roll: Double): Quaternion {
            val quat = Quaternion()
            quat.rotateYawPitchRoll(pitch, yaw, roll)
            return quat
        }

        fun fromLookDirection(dir: Vector): Quaternion {
            val q = Quaternion(-dir.y, dir.x, 0.0, dir.z + dir.length())

            // there is a special case when dir is (0, 0, -1)
            if (java.lang.Double.isNaN(q.w)) {
                q.x = 0.0
                q.y = 1.0
                q.z = 0.0
                q.w = 0.0
            }
            return q
        }

        fun fromLookDirection(dir: Vector, up: Vector): Quaternion {
            // Use the 3x3 rotation matrix solution found on SO, combined with a getRotation()
            // https://stackoverflow.com/a/18574797
            val D = dir.clone().normalize()
            val S = up.clone().crossProduct(dir).normalize()
            val U = D.clone().crossProduct(S)
            val result: Quaternion = Matrix.fromColumns3x3(S, U, D).getRotation()

            // Fix NaN as a result of dir == up
            return if (java.lang.Double.isNaN(result.x)) {
                fromLookDirection(dir)
            } else {
                result
            }
        }

        fun multiply(q1: Quaternion, q2: Quaternion?): Quaternion {
            val result = q1.clone()
            result.multiply(q2!!)
            return result
        }
    }

    override fun toString(): String {
        return "Quaternion{x=$x, y=$y, z=$z, w=$w}"
    }
}