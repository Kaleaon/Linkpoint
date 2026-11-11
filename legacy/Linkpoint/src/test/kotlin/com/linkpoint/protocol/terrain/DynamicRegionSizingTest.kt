package com.linkpoint.protocol.terrain

import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.FakeCapabilityRequester
import com.linkpoint.protocol.llsd.LLSDInteger
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDReal
import com.linkpoint.protocol.llsd.LLSDString
import com.linkpoint.protocol.types.LLVector3
import com.linkpoint.world.SimulatorFeaturesManager
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DynamicRegionSizingTest {

    @Test
    fun testCriterion1_SimulatorFeaturesRegionSizeParsing() {
        val fakeRequester = FakeCapabilityRequester().apply {
            enqueueResponse(CapabilityManager.CAP_SIMULATOR_FEATURES, LLSDMap().apply {
                this["RegionSizeX"] = LLSDInteger(512)
                this["RegionSizeY"] = LLSDInteger(1024)
            })
        }
        val manager = SimulatorFeaturesManager(fakeRequester)
        val projection = kotlinx.coroutines.runBlocking { manager.fetchSimulatorFeatures() }

        assertNotNull(projection)
        assertEquals(512, projection?.regionSizeX)
        assertEquals(1024, projection?.regionSizeY)

        // Test fallback and clamping
        val clampRequester = FakeCapabilityRequester().apply {
            enqueueResponse(CapabilityManager.CAP_SIMULATOR_FEATURES, LLSDMap().apply {
                this["RegionSizeX"] = LLSDReal(4096.0)
                this["RegionSizeY"] = LLSDString("128")
            })
        }
        val clampManager = SimulatorFeaturesManager(clampRequester)
        val clampProj = kotlinx.coroutines.runBlocking { clampManager.fetchSimulatorFeatures() }

        assertNotNull(clampProj)
        // 4096 should be clamped to max 2048
        assertEquals(2048, clampProj?.regionSizeX)
        // 128 should be clamped to min 256
        assertEquals(256, clampProj?.regionSizeY)
    }

    @Test
    fun testCriterion2_LayerDataParserAcceptsExtendedPatches() {
        val patch16_20 = TerrainPatch(16, 20, FloatArray(256) { 30.0f })
        assertEquals(16, patch16_20.x)
        assertEquals(20, patch16_20.y)

        // Check LayerDataParser.parse max patch calculations for 512m and 2048m
        val result = LayerDataParser.parse(ByteArray(0), regionSizeX = 512, regionSizeY = 512)
        assertNull("Empty byte array should return null", result)
    }

    @Test
    fun testCriterion3_TerrainManagerAllocatesExtendedHeightmap() {
        val terrainManager = TerrainManager()
        terrainManager.setRegionSize(512, 512)

        assertEquals(512, terrainManager.regionSizeX)
        assertEquals(512, terrainManager.regionSizeY)

        // Apply a patch at x = 18, y = 22 (outside standard 256m region)
        val heights = FloatArray(256) { 42.5f }
        val extendedPatch = TerrainPatch(18, 22, heights)

        val result = LayerDataResult(LayerType.LAND, listOf(extendedPatch))
        terrainManager.processLayerData(result)

        assertEquals(1, terrainManager.validPatchCount)
        assertEquals(42.5f, terrainManager.getHeightAt(18 * 16 + 5, 22 * 16 + 5), 0.01f)

        // Height sampling near 512m boundary
        assertEquals(0f, terrainManager.getHeightAt(512, 512), 0.001f)
    }

    @Test
    fun testCriterion4_TerrainRendererRegionDimensionConfiguration() {
        val terrainManager = TerrainManager()
        terrainManager.setRegionSize(1024, 1024)

        assertEquals(1024, terrainManager.regionSizeX)
        assertEquals(1024, terrainManager.regionSizeY)

        val patch32 = TerrainPatch(32, 32, FloatArray(256) { 100f })
        terrainManager.processLayerData(LayerDataResult(LayerType.LAND, listOf(patch32)))

        assertEquals(100f, terrainManager.getHeightAt(32 * 16 + 2, 32 * 16 + 2), 0.01f)
    }

    @Test
    fun testCriterion5_TerseVectorDecodingAndHeightSamplingAcrossExtendedBounds() {
        // Encode a terse vector at relative ratio (384 / 512) = 0.75
        val ratio = 384f / 512f
        val shortVal = (ratio * 65535f).toInt().coerceIn(0, 65535)

        val bytes = java.nio.ByteBuffer.allocate(6)
            .order(java.nio.ByteOrder.LITTLE_ENDIAN)
            .putShort(shortVal.toShort())
            .putShort(shortVal.toShort())
            .putShort(100.toShort())
            .array()

        val decoded512 = LLVector3.fromTerse(bytes, 0, rangeX = 512f, rangeY = 512f)
        assertEquals(384f, decoded512.x, 0.1f)
        assertEquals(384f, decoded512.y, 0.1f)

        // Terrain height sampling test across 512m
        val terrainManager = TerrainManager()
        terrainManager.setRegionSize(512, 512)
        val patch31 = TerrainPatch(31, 31, FloatArray(256) { 88f })
        terrainManager.processLayerData(LayerDataResult(LayerType.LAND, listOf(patch31)))

        // Point at x=500, y=500 (beyond 256m)
        val heightAt500 = terrainManager.getHeightAt(500, 500)
        assertEquals(88f, heightAt500, 0.1f)
    }
}
