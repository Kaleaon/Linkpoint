package com.linkpoint.protocol.terrain

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LayerDataParserTest {

    @Test
    fun `LayerDataParser creates fallback patches for 256m region`() {
        val result = LayerDataParser.parse(byteArrayOf(76), 256, 256)
        assertNotNull(result)
        assertEquals(LayerType.LAND, result?.type)
        assertEquals(256, result?.patches?.size)
    }

    @Test
    fun `LayerDataParser creates fallback patches for 512m region`() {
        val result = LayerDataParser.parse(byteArrayOf(76), 512, 512)
        assertNotNull(result)
        assertEquals(LayerType.LAND, result?.type)
        assertEquals(1024, result?.patches?.size) // 32 * 32 = 1024 patches
    }

    @Test
    fun `LayerDataParser creates fallback patches for 1024m region`() {
        val result = LayerDataParser.parse(byteArrayOf(76), 1024, 1024)
        assertNotNull(result)
        assertEquals(LayerType.LAND, result?.type)
        assertEquals(4096, result?.patches?.size) // 64 * 64 = 4096 patches
    }

    @Test
    fun `TerrainManager tracks patches dynamically for variable region sizes`() {
        val manager = TerrainManager()
        manager.setRegionSize(512, 512)
        assertEquals(512, manager.regionSizeX)
        assertEquals(512, manager.regionSizeY)
        
        val patches = LayerDataParser.createDefaultPatches(512, 512)
        val result = LayerDataResult(LayerType.LAND, patches)
        manager.processLayerData(result)
        
        assertEquals(1024, manager.validPatchCount)
        assertEquals(100f, manager.getLoadPercentage(), 0.01f)
        assertEquals(true, manager.isFullyLoaded())
    }
}
