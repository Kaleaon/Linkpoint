package com.linkpoint.protocol.capabilities

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CapabilityAssetResolutionTest {

    @Test
    fun `texture fetch URL prefers ViewerAsset over GetTexture`() {
        val manager = CapabilityManager()
        // Inject capabilities via reflection or test method
        val capabilitiesField = CapabilityManager::class.java.getDeclaredField("capabilities").apply {
            isAccessible = true
        }
        @Suppress("UNCHECKED_CAST")
        val capsMap = capabilitiesField.get(manager) as MutableMap<String, String>

        capsMap[CapabilityManager.CAP_VIEWER_ASSET] = "https://sim.agni.lindenlab.com/cap/viewer_asset"
        capsMap[CapabilityManager.CAP_GET_TEXTURE] = "https://sim.agni.lindenlab.com/cap/get_texture"

        assertEquals("https://sim.agni.lindenlab.com/cap/viewer_asset", manager.getTextureFetchURL())
        assertTrue(manager.hasTextureCapability())
    }

    @Test
    fun `texture fetch URL falls back to GetTexture when ViewerAsset is missing`() {
        val manager = CapabilityManager()
        val capabilitiesField = CapabilityManager::class.java.getDeclaredField("capabilities").apply {
            isAccessible = true
        }
        @Suppress("UNCHECKED_CAST")
        val capsMap = capabilitiesField.get(manager) as MutableMap<String, String>

        capsMap[CapabilityManager.CAP_GET_TEXTURE] = "https://opensim.grid/cap/get_texture"

        assertEquals("https://opensim.grid/cap/get_texture", manager.getTextureFetchURL())
        assertTrue(manager.hasTextureCapability())
    }

    @Test
    fun `mesh fetch URL prefers ViewerAsset over GetMesh2 and GetMesh`() {
        val manager = CapabilityManager()
        val capabilitiesField = CapabilityManager::class.java.getDeclaredField("capabilities").apply {
            isAccessible = true
        }
        @Suppress("UNCHECKED_CAST")
        val capsMap = capabilitiesField.get(manager) as MutableMap<String, String>

        capsMap[CapabilityManager.CAP_VIEWER_ASSET] = "https://sim.agni.lindenlab.com/cap/viewer_asset"
        capsMap[CapabilityManager.CAP_GET_MESH2] = "https://sim.agni.lindenlab.com/cap/get_mesh2"
        capsMap[CapabilityManager.CAP_GET_MESH] = "https://sim.agni.lindenlab.com/cap/get_mesh"

        assertEquals("https://sim.agni.lindenlab.com/cap/viewer_asset", manager.getMeshFetchURL())
        assertTrue(manager.hasMeshCapability())
    }

    @Test
    fun `mesh fetch URL falls back to GetMesh2 then GetMesh when ViewerAsset is missing`() {
        val manager = CapabilityManager()
        val capabilitiesField = CapabilityManager::class.java.getDeclaredField("capabilities").apply {
            isAccessible = true
        }
        @Suppress("UNCHECKED_CAST")
        val capsMap = capabilitiesField.get(manager) as MutableMap<String, String>

        capsMap[CapabilityManager.CAP_GET_MESH2] = "https://opensim.grid/cap/get_mesh2"
        capsMap[CapabilityManager.CAP_GET_MESH] = "https://opensim.grid/cap/get_mesh"

        assertEquals("https://opensim.grid/cap/get_mesh2", manager.getMeshFetchURL())
        assertTrue(manager.hasMeshCapability())

        capsMap.remove(CapabilityManager.CAP_GET_MESH2)
        assertEquals("https://opensim.grid/cap/get_mesh", manager.getMeshFetchURL())
        assertTrue(manager.hasMeshCapability())

        capsMap.remove(CapabilityManager.CAP_GET_MESH)
        assertNull(manager.getMeshFetchURL())
        assertFalse(manager.hasMeshCapability())
    }
}
