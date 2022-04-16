package net.bandithemepark.bandicore.util.math

import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

class Matrix(var rows: Int, var columns: Int) {
    var m = Array(rows) { DoubleArray(columns) }

    init {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                m[i][j] = 0.0
            }
        }
    }

    fun getElement(rows: Int, columns: Int): Double {
        return m[rows][columns]
    }

    fun setElement(newValue: Double, rows: Int, columns: Int) {
        m[rows][columns] = newValue
    }

    fun getRotation(): Quaternion {
        val tr: Double = m[0][0] + m[1][1] + m[2][2]
        return if (tr > 0) {
            Quaternion(m[2][1] - m[1][2], m[0][2] - m[2][0], m[1][0] - m[0][1], 1.0 + tr)
        } else if ((m[0][0] > m[1][1]) and (m[0][0] > m[2][2])) {
            Quaternion(1.0 + m[0][0] - m[1][1] - m[2][2], m[0][1] + m[1][0], m[0][2] + m[2][0], m[2][1] - m[1][2])
        } else if (m[1][1] > m[2][2]) {
            Quaternion(m[0][1] + m[1][0], 1.0 + m[1][1] - m[0][0] - m[2][2], m[1][2] + m[2][1], m[0][2] - m[2][0])
        } else {
            Quaternion(m[0][2] + m[2][0], m[1][2] + m[2][1], 1.0 + m[2][2] - m[0][0] - m[1][1], m[1][0] - m[0][1])
        }
    }

    companion object {
        fun fromColumns3x3(v0: Vector, v1: Vector, v2: Vector): Matrix {
            val matrix = Matrix(4, 4)

            matrix.m[0][0] = v0.x
            matrix.m[0][1] = v1.x
            matrix.m[0][2] = v2.x
            matrix.m[0][3] = 0.0

            matrix.m[1][0] = v0.y
            matrix.m[1][1] = v1.y
            matrix.m[1][2] = v2.y
            matrix.m[1][3] = 0.0

            matrix.m[2][0] = v0.z
            matrix.m[2][1] = v1.z
            matrix.m[2][2] = v2.z
            matrix.m[2][3] = 0.0

            matrix.m[3][0] = 0.0
            matrix.m[3][1] = 0.0
            matrix.m[3][2] = 0.0
            matrix.m[3][3] = 1.0

            return matrix
        }

        fun add(x: Matrix, y: Matrix): Matrix {
            val z = Matrix(x.rows, x.columns)
            var value: Double

            return if (x.rows == y.rows && x.columns == y.columns) {
                for (i in 0 until x.rows) {
                    for (j in 0 until y.columns) {
                        value = x.getElement(i, j) + y.getElement(i, j)
                        z.setElement(value, i, j)
                    }
                }
                z
            } else {
                println("ERROR: The number of rows and columns of the matricies are not equal.")
                z
            }
        }

        fun subtract(x: Matrix, y: Matrix): Matrix {
            val z = Matrix(x.rows, x.columns)
            var value: Double

            return if (x.rows == y.rows && x.columns == y.columns) {
                for (i in 0 until x.rows) {
                    for (j in 0 until y.columns) {
                        value = x.getElement(i, j) - y.getElement(i, j)
                        z.setElement(value, i, j)
                    }
                }
                z
            } else {
                println("ERROR: The number of rows and columns of the matricies are not equal.")
                z
            }
        }

        fun multiply(x: Matrix, y: Matrix): Matrix {
            val z = Matrix(x.rows, y.columns)
            var value: Double

            return if (x.columns == y.rows) {
                for (i in 0 until x.rows) {
                    for (j in 0 until y.columns) {
                        var sum = 0.0
                        for (k in 0 until x.rows) {
                            sum += x.getElement(i, k) * y.getElement(k, j)
                        }
                        value = sum
                        z.setElement(value, i, j)
                    }
                }
                z
            } else {
                println("ERROR: The number of columns of the first matrix (${x.columns}) and the number of rows of the second matrix (${y.rows}) are not equivalent.")
                z
            }
        }

//        fun fromAngles(pitch: Double, yaw: Double, roll: Double): Matrix {
//            val su = Math.sin(Math.toRadians(pitch))
//            val cu = Math.cos(Math.toRadians(pitch))
//            val sv = Math.sin(Math.toRadians(yaw))
//            val cv = Math.cos(Math.toRadians(yaw))
//            val sw = Math.sin(Math.toRadians(roll))
//            val cw = Math.cos(Math.toRadians(roll))
//
//            val matrix = Matrix(3, 3)
//            matrix.m[0][0] = cv*cw
//            matrix.m[0][1] = su*sv*cw - cu*sw
//            matrix.m[0][2] = su*sw + cu*sv*cw
//            matrix.m[1][0] = cv*sw
//            matrix.m[1][1] = cu*cw + su*sv*sw
//            matrix.m[1][2] = cu*sv*sw - su*cw
//            matrix.m[2][0] = -sv
//            matrix.m[2][1] = su*cv
//            matrix.m[2][2] = cu*cv
//
//            return matrix
//        }

        fun fromAngles(pitch: Double, yaw: Double, roll: Double): Matrix {
            val rotX = Matrix(4, 4)
            rotX.m[0][0] = 1.0
            rotX.m[1][1] = cos(Math.toRadians(pitch))
            rotX.m[1][2] = -sin(Math.toRadians(pitch))
            rotX.m[2][1] = sin(Math.toRadians(pitch))
            rotX.m[2][2] = cos(Math.toRadians(pitch))
            rotX.m[3][3] = 1.0

            val rotY = Matrix(4, 4)
            rotY.m[0][0] = cos(Math.toRadians(yaw))
            rotY.m[0][2] = sin(Math.toRadians(yaw))
            rotY.m[1][1] = 1.0
            rotY.m[2][0] = -sin(Math.toRadians(yaw))
            rotY.m[2][2] = cos(Math.toRadians(yaw))
            rotY.m[3][3] = 1.0

            val rotZ = Matrix(4, 4)
            rotZ.m[0][0] = cos(Math.toRadians(roll))
            rotZ.m[0][1] = -sin(Math.toRadians(roll))
            rotZ.m[1][0] = sin(Math.toRadians(roll))
            rotZ.m[1][1] = cos(Math.toRadians(roll))
            rotZ.m[2][2] = 1.0
            rotZ.m[3][3] = 1.0

            return multiply(rotX, multiply(rotY, rotZ))
        }
    }
}