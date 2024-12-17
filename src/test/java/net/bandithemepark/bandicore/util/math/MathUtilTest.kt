package net.bandithemepark.bandicore.util.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MathUtilTest {
    @Test
    fun testInterpolateAngles() {
        assertEquals(45.0, MathUtil.interpolateAngles(0.0, 90.0, 0.5))
        assertEquals(-45.0, MathUtil.interpolateAngles(0.0, -90.0, 0.5))
        assertEquals(-175.0, MathUtil.interpolateAngles(170.0, -160.0, 0.5))
        assertEquals(-180.0, MathUtil.interpolateAngles(170.0, -170.0, 0.5))
        assertEquals(175.0, MathUtil.interpolateAngles(-170.0, 160.0, 0.5))
    }
}