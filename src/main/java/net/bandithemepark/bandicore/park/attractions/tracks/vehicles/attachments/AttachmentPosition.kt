package net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments

import net.bandithemepark.bandicore.util.math.Matrix
import net.bandithemepark.bandicore.util.math.Quaternion
import org.bukkit.util.Vector

class AttachmentPosition(var x: Double, var y: Double, var z: Double, var pitch: Double, var yaw: Double, var roll: Double) {
    /**
     * Calculates the position of the attachment when a certain rotation is applied
     * @param rotation The rotation to apply
     * @return The position of the attachment (does not include rotation!)
     */
    fun getPosition(rotation: Quaternion): Vector {
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
     * Creates a list containing all axis and rotation
     * @return A list containing all axis and rotation
     */
    fun getList(): List<Double> {
        return listOf(x, y, z, pitch, yaw, roll)
    }

    fun toVector(): Vector {
        return Vector(x, y, z)
    }
}
