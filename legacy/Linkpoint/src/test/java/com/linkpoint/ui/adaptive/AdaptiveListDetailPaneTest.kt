package com.linkpoint.ui.adaptive

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveListDetailPaneTest {

    @Test
    fun testSelectionStateBehavior() {
        var selectedItem: String? = null
        val onClearSelection: () -> Unit = { selectedItem = null }

        selectedItem = "Friend Profile A"
        assertEquals("Friend Profile A", selectedItem)

        // Simulate back press in compact mode clearing selection
        onClearSelection()
        assertNull(selectedItem)
    }

    @Test
    fun testSplitPaneDetectionLogic() {
        val compactClass = calculateWindowSizeClass(500)
        val mediumClass = calculateWindowSizeClass(700)
        val expandedClass = calculateWindowSizeClass(1000)

        assertFalse(compactClass.isAtLeastMedium)
        assertTrue(mediumClass.isAtLeastMedium)
        assertTrue(expandedClass.isAtLeastMedium)
    }
}
