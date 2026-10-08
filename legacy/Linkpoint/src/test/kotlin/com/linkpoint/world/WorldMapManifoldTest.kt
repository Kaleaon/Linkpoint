package com.linkpoint.world

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.protocol.capabilities.CapabilityManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class WorldMapManifoldTest {

    private lateinit var capabilityManager: CapabilityManager
    private lateinit var worldMap: WorldMap

    @Before
    fun setUp() {
        capabilityManager = CapabilityManager()
        worldMap = WorldMap(capabilityManager)
    }

    @Test
    fun `cacheRegionInfo uses frame-aware cache keys and prevents coordinate collisions`() {
        val img1 = UUID.randomUUID()
        val img2 = UUID.randomUUID()

        // Cache region at (1000, 1000) under frame "frame-planar-01"
        worldMap.cacheRegionInfo(1000, 1000, "Planar Region", img1, "frame-planar-01")

        // Cache another region at identical 2D coordinates (1000, 1000) under frame "frame-ringworld-02"
        worldMap.cacheRegionInfo(1000, 1000, "Ringworld Region", img2, "frame-ringworld-02")

        // Verify lookup with frameId "frame-planar-01"
        worldMap.activeManifoldFrameId = "frame-planar-01"
        val handle = worldMap.calculateRegionHandle(1000, 1000)
        val name1 = worldMap.getCachedRegionName(handle, "frame-planar-01")
        assertEquals("Planar Region", name1)

        // Verify lookup with frameId "frame-ringworld-02"
        val name2 = worldMap.getCachedRegionName(handle, "frame-ringworld-02")
        assertEquals("Ringworld Region", name2)
    }
}
