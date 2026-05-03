package com.linkpoint.render.lumiya.drawable

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Distance-fade math for [DrawableHoverText]. The geometric scaling
 * keeps text legible up to ~4x the texture pixel size, but a separate
 * distance-fade tapers alpha to zero past the chat radius so cluttered
 * regions stay readable.
 *
 * The fade lives on [DrawableHoverText.Companion.fadeFactorAt] so it
 * can be exercised without instantiating the class — the constructor
 * builds an Android `Paint` which is unavailable under the JVM unit
 * test runner.
 */
class HoverTextDistanceFadeTest {

    @Test
    fun `inside fade-start returns full alpha`() {
        assertEquals(1f, DrawableHoverText.fadeFactorAt(0f), 1e-5f)
        assertEquals(1f, DrawableHoverText.fadeFactorAt(DrawableHoverText.FADE_START_DISTANCE - 0.1f), 1e-5f)
        assertEquals(1f, DrawableHoverText.fadeFactorAt(DrawableHoverText.FADE_START_DISTANCE), 1e-5f)
    }

    @Test
    fun `past fade-end returns zero alpha`() {
        assertEquals(0f, DrawableHoverText.fadeFactorAt(DrawableHoverText.FADE_END_DISTANCE), 1e-5f)
        assertEquals(0f, DrawableHoverText.fadeFactorAt(DrawableHoverText.FADE_END_DISTANCE + 5f), 1e-5f)
        assertEquals(0f, DrawableHoverText.fadeFactorAt(1000f), 1e-5f)
    }

    @Test
    fun `midpoint reports half alpha`() {
        val mid = (DrawableHoverText.FADE_START_DISTANCE + DrawableHoverText.FADE_END_DISTANCE) / 2f
        assertEquals(0.5f, DrawableHoverText.fadeFactorAt(mid), 1e-5f)
    }

    @Test
    fun `fade is monotonically non-increasing across the band`() {
        val start = DrawableHoverText.FADE_START_DISTANCE
        val end = DrawableHoverText.FADE_END_DISTANCE
        var prev = 1f
        var d = start
        while (d <= end) {
            val v = DrawableHoverText.fadeFactorAt(d)
            assertTrue("fade should not increase: prev=$prev v=$v at d=$d", v <= prev + 1e-5f)
            prev = v
            d += (end - start) / 16f
        }
    }
}
