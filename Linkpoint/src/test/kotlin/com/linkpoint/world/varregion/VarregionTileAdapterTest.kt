package com.linkpoint.world.varregion

import com.linkpoint.protocol.terrain.LayerDataResult
import com.linkpoint.protocol.terrain.TerrainPatch
import com.linkpoint.render.lumiya.spatial.FrustumCuller
import com.linkpoint.render.lumiya.spatial.SpatialEntry
import com.linkpoint.testing.RobolectricTestApp
import com.linkpoint.world.RegionCrossingManager
import com.linkpoint.world.RegionInfo
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = RobolectricTestApp::class)
class VarregionTileAdapterTest {

    private fun createTestFrustum(minX: Float, minY: Float, maxX: Float, maxY: Float): FrustumCuller {
        val culler = FrustumCuller()
        culler.setPlaneForTest(0, 1f, 0f, 0f, -minX)      // Left plane: x >= minX
        culler.setPlaneForTest(1, -1f, 0f, 0f, maxX)      // Right plane: x <= maxX
        culler.setPlaneForTest(2, 0f, 1f, 0f, -minY)      // Bottom plane: y >= minY
        culler.setPlaneForTest(3, 0f, -1f, 0f, maxY)      // Top plane: y <= maxY
        culler.setPlaneForTest(4, 0f, 0f, 1f, 0f)         // Near plane: z >= 0
        culler.setPlaneForTest(5, 0f, 0f, -1f, 4096f)     // Far plane: z <= 4096
        return culler
    }

    @Test
    fun `VarregionTileAdapter instantiates NxM matrix matching Varregion dimensions`() {
        // Standard 256m region -> 1x1 matrix
        val adapter256 = VarregionTileAdapter(256, 256)
        assertEquals(1, adapter256.gridWidth)
        assertEquals(1, adapter256.gridHeight)
        assertEquals(1, adapter256.subTiles.size)

        // 512m Varregion -> 2x2 matrix
        val adapter512 = VarregionTileAdapter(512, 512)
        assertEquals(2, adapter512.gridWidth)
        assertEquals(2, adapter512.gridHeight)
        assertEquals(2, adapter512.subTiles.size)
        assertEquals(2, adapter512.subTiles[0].size)

        // 1024m Varregion -> 4x4 matrix
        val adapter1024 = VarregionTileAdapter(1024, 1024)
        assertEquals(4, adapter1024.gridWidth)
        assertEquals(4, adapter1024.gridHeight)

        // Asymmetric Varregion (512m x 1024m) -> 2x4 matrix
        val adapterAsym = VarregionTileAdapter(512, 1024)
        assertEquals(2, adapterAsym.gridWidth)
        assertEquals(4, adapterAsym.gridHeight)
    }

    @Test
    fun `coordinate translation accurately maps world and patch coordinates to sub-tiles`() {
        val adapter = VarregionTileAdapter(1024, 1024) // 4x4 matrix

        // World coordinates in tile (0,0)
        assertEquals(Pair(0, 0), adapter.getTileIndices(100f, 100f))
        assertEquals(Pair(100f, 100f), adapter.toLocalCoordinates(100f, 100f))

        // World coordinates in tile (1,2) -> (256 + 100 = 356, 512 + 50 = 562)
        assertEquals(Pair(1, 2), adapter.getTileIndices(356f, 562f))
        assertEquals(Pair(100f, 50f), adapter.toLocalCoordinates(356f, 562f))

        // Patch coordinate routing: patch (18, 20) -> tile (1, 1), local patch (2, 4)
        val tile = adapter.getSubTileForPatch(18, 20)
        assertEquals(1, tile.tileX)
        assertEquals(1, tile.tileY)
        assertEquals(256f, tile.worldOffsetX, 0.01f)
        assertEquals(256f, tile.worldOffsetY, 0.01f)
    }

    @Test
    fun `terrain patches and layer data with coordinates over 256m are routed without dropping data`() {
        val adapter = VarregionTileAdapter(512, 512) // 2x2 matrix

        // Create heightmap data
        val heights = FloatArray(256) { 35.0f }

        // Patch at (18, 20) in global patch grid (corresponds to world position (288m, 320m) in sub-tile (1, 1))
        val patch11 = TerrainPatch(18, 20, heights)
        val result = LayerDataResult(type = 76, patches = listOf(patch11))

        adapter.routeLayerData(result)

        val targetTile = adapter.subTiles[1][1]
        // Local patch index inside sub-tile (1,1) is (2, 4) -> height at local (2*16, 4*16) = (32, 64)
        assertEquals(35.0f, targetTile.terrainManager.getHeightAt(32, 64), 0.01f)
    }

    @Test
    fun `sub-tile terrain and water meshes are positioned at correct world coordinate offsets`() {
        val adapter = VarregionTileAdapter(1024, 1024) // 4x4 matrix

        for (tx in 0 until 4) {
            for (ty in 0 until 4) {
                val tile = adapter.subTiles[tx][ty]
                assertEquals(tx * 256f, tile.worldOffsetX, 0.001f)
                assertEquals(ty * 256f, tile.worldOffsetY, 0.001f)
                assertEquals(tx, tile.tileX)
                assertEquals(ty, tile.tileY)
            }
        }

        // Global water height propagation
        adapter.setWaterHeight(25.5f)
        assertEquals(25.5f, adapter.waterHeight, 0.001f)
        assertEquals(25.5f, adapter.subTiles[1][1].waterHeight, 0.001f)
        assertEquals(25.5f, adapter.subTiles[1][1].terrainManager.waterHeight, 0.001f)
    }

    @Test
    fun `multi-octree frustum queries aggregate visible entries across all active sub-tile octrees`() {
        val adapter = VarregionTileAdapter(512, 512) // 2x2 matrix

        // Insert objects in tile (0, 0) and tile (1, 1)
        val entry00 = SpatialEntry(id = 101L, posX = 100f, posY = 100f, posZ = 10f)
        val entry11 = SpatialEntry(id = 202L, posX = 350f, posY = 350f, posZ = 10f)
        val entryOutside = SpatialEntry(id = 303L, posX = 450f, posY = 450f, posZ = 10f)

        adapter.routeSpatialEntry(entry00)
        adapter.routeSpatialEntry(entry11)
        adapter.routeSpatialEntry(entryOutside)

        assertEquals(3, adapter.totalObjectCount)

        // Frustum covering area (0, 0) to (400, 400)
        val frustum = createTestFrustum(minX = 0f, minY = 0f, maxX = 400f, maxY = 400f)
        val visible = adapter.queryFrustum(frustum)

        val visibleIds = visible.map { it.id }.toSet()
        assertTrue("Entry 101 in sub-tile (0,0) must be visible", visibleIds.contains(101L))
        assertTrue("Entry 202 in sub-tile (1,1) must be visible", visibleIds.contains(202L))
        assertFalse("Entry 303 outside frustum must not be visible", visibleIds.contains(303L))
    }

    @Test
    fun `composite minimap generates stitched bitmap for full Varregion footprint`() {
        val adapter = VarregionTileAdapter(512, 512) // 2x2 matrix

        val compositeBmp = adapter.generateCompositeMinimap(outputSize = 512)
        assertNotNull(compositeBmp)
        assertEquals(512, compositeBmp.width)
        assertEquals(512, compositeBmp.height)
    }

    @Test
    fun `internal tile transitions inside Varregion do not trigger false external region crossings`() {
        val adapter = VarregionTileAdapter(1024, 1024)

        // Internal transition from tile (0,0) at (100, 100) to tile (1,0) at (300, 100)
        assertTrue(adapter.isInternalTransition(100f, 100f, 300f, 100f))
        assertTrue(adapter.isInsideVarregion(300f, 100f))

        // External movement outside Varregion boundary (x = 1100m in 1024m region)
        assertFalse(adapter.isInternalTransition(100f, 100f, 1100f, 100f))
        assertFalse(adapter.isInsideVarregion(1100f, 100f))

        // Verify with RegionCrossingManager logic
        val dummyUDP = com.linkpoint.protocol.messages.UDPConnectionFixed()
        val dummyCap = com.linkpoint.protocol.capabilities.CapabilityManager()
        val crossingManager = RegionCrossingManager(dummyUDP, dummyCap)

        val varregionInfo = RegionInfo(
            handle = 1000L,
            name = "TestVarregion",
            simIP = "127.0.0.1",
            simPort = 9000,
            seedCapability = "http://localhost/seed",
            regionSizeX = 1024,
            regionSizeY = 1024
        )
        crossingManager.setCurrentRegion(varregionInfo)

        // Moving to local position (500m, 500m) inside 1024m Varregion must NOT produce a neighbor crossing handle
        val neighbor = crossingManager.getNeighborRegion(500f, 500f)
        assertNull("Internal sub-tile transition must not trigger region crossing", neighbor)
    }
}
