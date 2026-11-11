package com.linkpoint.world

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.world.minimap.MinimapManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class RegionCrossingAndMinimapTest {

    @Test
    fun `isNearRegionBorder respects region dimensions`() {
        val udpConnection = mock(UDPConnectionFixed::class.java)
        val capabilityManager = mock(com.linkpoint.protocol.capabilities.CapabilityManager::class.java)
        val manager = RegionCrossingManager(udpConnection, capabilityManager)

        // Default 256m region
        manager.setCurrentRegion(RegionInfo(100L, "Standard256", "127.0.0.1", 9000, "http://cap", regionSizeX = 256, regionSizeY = 256))
        assertTrue(manager.isNearRegionBorder(250f, 100f))
        assertFalse(manager.isNearRegionBorder(128f, 128f))

        // Varregion 1024m region
        manager.setCurrentRegion(RegionInfo(200L, "Var1024", "127.0.0.1", 9000, "http://cap", regionSizeX = 1024, regionSizeY = 1024))
        // Position 250m is inside a 1024m region, so not near border
        assertFalse(manager.isNearRegionBorder(250f, 100f))
        // Position 1020m is near border of 1024m region
        assertTrue(manager.isNearRegionBorder(1020f, 500f))
    }

    @Test
    fun `MinimapManager updates region dimensions`() {
        val udpConnection = mock(UDPConnectionFixed::class.java)
        val minimap = MinimapManager(udpConnection)

        assertEquals(256f, minimap.regionSizeX, 0.01f)
        assertEquals(256f, minimap.regionSizeY, 0.01f)

        minimap.setRegionSize(1024f, 1024f)
        assertEquals(1024f, minimap.regionSizeX, 0.01f)
        assertEquals(1024f, minimap.regionSizeY, 0.01f)
    }
}
