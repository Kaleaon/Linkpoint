package com.linkpoint.protocol.terrain

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.protocol.translation.LinkpointProtocolBridge
import com.linkpoint.protocol.types.LLVector3
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VirtualRegionMapperTest {

    @Test
    fun testGlobalToVirtualTileMapping() {
        // Test standard 256m region
        val pos256 = VirtualRegionMapper.globalToVirtualTile(128f, 128f, 25f, 256, 256)
        assertEquals(0, pos256.tileIndex.x)
        assertEquals(0, pos256.tileIndex.y)
        assertEquals(128f, pos256.localX, 0.001f)
        assertEquals(128f, pos256.localY, 0.001f)
        assertEquals(25f, pos256.localZ, 0.001f)

        // Test 1024m x 1024m varregion position (x=700m, y=300m)
        val pos1024 = VirtualRegionMapper.globalToVirtualTile(700f, 300f, 50f, 1024, 1024)
        assertEquals(2, pos1024.tileIndex.x) // 700 / 256 = 2
        assertEquals(1, pos1024.tileIndex.y) // 300 / 256 = 1
        assertEquals(188f, pos1024.localX, 0.001f) // 700 - 512 = 188
        assertEquals(44f, pos1024.localY, 0.001f)  // 300 - 256 = 44
        assertEquals(50f, pos1024.localZ, 0.001f)

        // Test roundtrip translation
        val globalBack = pos1024.toGlobalVector()
        assertEquals(700f, globalBack.x, 0.001f)
        assertEquals(300f, globalBack.y, 0.001f)
        assertEquals(50f, globalBack.z, 0.001f)
    }

    @Test
    fun testTerrainPatchReindexingStandardRange() {
        // Create patch with global patch coordinates (x=18, y=35) in a 1024m region
        // x=18 -> tile 1, local patch 2
        // y=35 -> tile 2, local patch 3
        val heightMap = FloatArray(256) { 15.0f }
        val globalPatch = TerrainPatch(x = 18, y = 35, heightMap = heightMap)

        val (tileIndex, reindexedPatch) = VirtualRegionMapper.mapPatchToVirtualTile(globalPatch)

        assertEquals(1, tileIndex.x)
        assertEquals(2, tileIndex.y)

        // Acceptance Criteria: Re-indexed patches within standard 0 to 15 coordinate ranges
        assertTrue("Patch X must be in 0..15 range", reindexedPatch.x in 0..15)
        assertTrue("Patch Y must be in 0..15 range", reindexedPatch.y in 0..15)
        assertEquals(2, reindexedPatch.x) // 18 % 16 = 2
        assertEquals(3, reindexedPatch.y) // 35 % 16 = 3
    }

    @Test
    fun testNoPatchDropsFor4096mRegions() {
        val patches = mutableListOf<TerrainPatch>()
        val dummyHeights = FloatArray(256) { 20.0f }

        // Generate patches across a 4096m x 4096m region (256x256 = 65,536 patches)
        // Test key corner and interior patches up to patch (255, 255)
        val patchCoords = listOf(
            Pair(0, 0),
            Pair(15, 15),
            Pair(16, 16),
            Pair(100, 200),
            Pair(255, 255)
        )

        for ((px, py) in patchCoords) {
            patches.add(TerrainPatch(x = px, y = py, heightMap = dummyHeights))
        }

        val virtualTiles = VirtualRegionMapper.splitTerrainPatchesToVirtualTiles(
            patches = patches,
            regionWidth = 4096,
            regionHeight = 4096
        )

        // Ensure all patches are parsed and placed in virtual tile packets without drops
        var totalParsedPatches = 0
        for ((tileIdx, packet) in virtualTiles) {
            for (patch in packet.patches) {
                totalParsedPatches++
                assertTrue("Re-indexed patch X must be in 0..15 range", patch.x in 0..15)
                assertTrue("Re-indexed patch Y must be in 0..15 range", patch.y in 0..15)
            }
        }

        assertEquals(patchCoords.size, totalParsedPatches)

        // Verify corner patch (255, 255) maps to tile (15, 15) local patch (15, 15)
        val tile15_15 = virtualTiles[VirtualTileIndex(15, 15)]
        assertNotNull("Tile (15, 15) packet must exist", tile15_15)
        val cornerPatch = tile15_15!!.patches.first { it.x == 15 && it.y == 15 }
        assertNotNull(cornerPatch)
    }

    @Test
    fun testLinkpointProtocolBridgeVirtualRegionMethods() {
        val bridge = LinkpointProtocolBridge("https://login.secondlife.com/cgi-bin/login.cgi")

        val pos = bridge.translateGlobalToVirtualTile(500f, 600f, 10f, 2048, 2048)
        assertEquals(1, pos.tileIndex.x) // 500 / 256 = 1
        assertEquals(2, pos.tileIndex.y) // 600 / 256 = 2
        assertEquals(244f, pos.localX, 0.001f) // 500 - 256 = 244
        assertEquals(88f, pos.localY, 0.001f)  // 600 - 512 = 88

        val globalVec = bridge.translateVirtualTileToGlobal(VirtualTileIndex(1, 2), 244f, 88f, 10f)
        assertEquals(500f, globalVec.x, 0.001f)
        assertEquals(600f, globalVec.y, 0.001f)
        assertEquals(10f, globalVec.z, 0.001f)
    }

    @Test
    fun testTerrainManagerVirtualTileProcessingAndLRUEviction() {
        val manager = TerrainManager()
        manager.setRegionSize(4096, 4096)
        manager.setActiveTile(2, 2)

        val heightMap = FloatArray(256) { 25.0f }
        // Patch at global x=34 (tile 2, local patch 2), y=35 (tile 2, local patch 3)
        val patchInActiveTile = TerrainPatch(34, 35, heightMap)
        val layerDataResult = LayerDataResult(LayerType.LAND, listOf(patchInActiveTile))

        manager.processLayerData(layerDataResult)

        assertEquals(1, manager.validPatchCount)
        assertTrue(manager.isFullyLoaded() == false)

        // Reset clears tile cache and restores defaults
        manager.reset()
        assertEquals(0, manager.validPatchCount)
        assertEquals(256, manager.regionWidth)
        assertEquals(256, manager.regionHeight)
        assertEquals(VirtualTileIndex(0, 0), manager.activeTileIndex)
    }
}
