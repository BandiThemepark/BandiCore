package net.bandithemepark.bandicore.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UtilTest {
    @Test
    fun zeroCharactersShouldReturnNoNegativeText() {
        val result = Util.getNegativeText(0)
        assertEquals("", result)
    }

    @Test
    fun twoCharactersShouldReturnNegativeText() {
        val result = Util.getNegativeText(2)
        assertEquals("\uE019\uE019", result)
    }
}