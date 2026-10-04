package com.linkpoint.protocol.capabilities

import com.linkpoint.protocol.translation.LinkpointTranslationLayer
import org.junit.Assert.assertTrue
import org.junit.Test

class CapabilityManagerTest {

    @Test
    fun `seed initialization requests CAP_MOVE_INVENTORY_ITEM`() {
        // Verify CAP_MOVE_INVENTORY_ITEM is present in reference capability list
        val referenceCaps = LinkpointTranslationLayer.getReferenceCapabilityNames()
        assertTrue(
            "LinkpointTranslationLayer reference capability names must contain CAP_MOVE_INVENTORY_ITEM",
            referenceCaps.contains(CapabilityManager.CAP_MOVE_INVENTORY_ITEM)
        )

        // Verify CAP_MOVE_INVENTORY_ITEM is in INVENTORY_CAPS set for throttling
        val inventoryCapsField = CapabilityManager::class.java.getDeclaredField("INVENTORY_CAPS")
        inventoryCapsField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val inventoryCaps = inventoryCapsField.get(null) as Set<String>
        assertTrue(
            "INVENTORY_CAPS throttling set must contain CAP_MOVE_INVENTORY_ITEM",
            inventoryCaps.contains(CapabilityManager.CAP_MOVE_INVENTORY_ITEM)
        )
    }
}
