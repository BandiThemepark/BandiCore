package net.bandithemepark.bandicore.server.animation

import kotlin.math.cos

enum class Interpolation(val code: String) {
    LINEAR("linear") {
        override fun interpolate(t: Double, from: Double, to: Double): Double {
            return from + (to - from) * t
        }
    }, SMOOTH("catmullrom") {
        override fun interpolate(t: Double, from: Double, to: Double): Double {
            val t2 = (1 - cos(t * Math.PI)) / 2
            return from * (1 - t2) + to * t2
        }
    }, STEP("step") {
        override fun interpolate(t: Double, from: Double, to: Double): Double {
            return from
        }
    };

    abstract fun interpolate(t: Double, from: Double, to: Double): Double
}