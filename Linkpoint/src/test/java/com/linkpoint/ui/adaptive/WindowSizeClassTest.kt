package com.linkpoint.ui.adaptive

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WindowSizeClassTest {

    @Test
    fun testCompactThresholds() {
        assertEquals(WindowSizeClass.Compact, calculateWindowSizeClass(0))
        assertEquals(WindowSizeClass.Compact, calculateWindowSizeClass(360))
        assertEquals(WindowSizeClass.Compact, calculateWindowSizeClass(599))
        assertEquals(WindowSizeClass.Compact, calculateWindowSizeClass(599.dp))

        val compact = calculateWindowSizeClass(400)
        assertTrue(compact.isCompact)
        assertFalse(compact.isMedium)
        assertFalse(compact.isExpanded)
        assertFalse(compact.isAtLeastMedium)
    }

    @Test
    fun testMediumThresholds() {
        assertEquals(WindowSizeClass.Medium, calculateWindowSizeClass(600))
        assertEquals(WindowSizeClass.Medium, calculateWindowSizeClass(720))
        assertEquals(WindowSizeClass.Medium, calculateWindowSizeClass(840))
        assertEquals(WindowSizeClass.Medium, calculateWindowSizeClass(600.dp))

        val medium = calculateWindowSizeClass(700)
        assertFalse(medium.isCompact)
        assertTrue(medium.isMedium)
        assertFalse(medium.isExpanded)
        assertTrue(medium.isAtLeastMedium)
    }

    @Test
    fun testExpandedThresholds() {
        assertEquals(WindowSizeClass.Expanded, calculateWindowSizeClass(841))
        assertEquals(WindowSizeClass.Expanded, calculateWindowSizeClass(1080))
        assertEquals(WindowSizeClass.Expanded, calculateWindowSizeClass(1280.dp))

        val expanded = calculateWindowSizeClass(900)
        assertFalse(expanded.isCompact)
        assertFalse(expanded.isMedium)
        assertTrue(expanded.isExpanded)
        assertTrue(expanded.isAtLeastMedium)
    }
}
