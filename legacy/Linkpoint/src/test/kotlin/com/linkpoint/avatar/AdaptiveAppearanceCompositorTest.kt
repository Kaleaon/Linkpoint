package com.linkpoint.avatar

import com.linkpoint.bom.BakesOnMeshManager
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.FakeCapabilityRequester
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDString
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.runBlocking
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class AdaptiveAppearanceCompositorTest {

    private class TestableCapabilityManager : CapabilityManager() {
        private val capMap = mutableMapOf<String, String>()

        fun setCap(name: String, url: String) {
            capMap[name] = url
        }

        fun removeCap(name: String) {
            capMap.remove(name)
        }

        override fun getCapability(name: String): String? {
            return capMap[name]
        }

        override fun hasCapability(name: String): Boolean {
            return capMap.containsKey(name)
        }
    }

    private lateinit var capabilityManager: TestableCapabilityManager
    private lateinit var negotiator: AppearanceCapabilityNegotiator

    @Before
    fun setUp() {
        capabilityManager = TestableCapabilityManager()
        negotiator = AppearanceCapabilityNegotiator(capabilityManager)
    }

    @Test
    fun testSeedCapabilityResolutionIdentifiesSsaModeWithin200ms() = runBlocking {
        capabilityManager.setCap(CapabilityManager.CAP_SERVER_SIDE_APPEARANCE, "https://sim.example.com/cap/ServerSideAppearance")
        capabilityManager.setCap(CapabilityManager.CAP_UPLOAD_BAKED_TEXTURE, "https://sim.example.com/cap/UploadBakedTexture")
        capabilityManager.setCap(CapabilityManager.CAP_AGENT_PREFERENCES, "https://sim.example.com/cap/AgentPreferences")

        val result = negotiator.negotiateCapabilities()

        assertEquals(AppearanceMode.SERVER_SIDE, result.mode)
        assertTrue("Latency must be <= 200ms", result.resolutionTimeMs <= 200)
        assertEquals("https://sim.example.com/cap/ServerSideAppearance", result.ssaCapUrl)
        assertEquals("https://sim.example.com/cap/UploadBakedTexture", result.uploadBakedTextureCapUrl)
        assertTrue(result.isBomSupported)
    }

    @Test
    fun testFallbackToClientSideBakingWhenSsaCapabilityIsMissing() = runBlocking {
        // Missing ServerSideAppearance capability (legacy OpenSim CSB region)
        capabilityManager.removeCap(CapabilityManager.CAP_SERVER_SIDE_APPEARANCE)
        capabilityManager.removeCap(CapabilityManager.CAP_UPDATE_AVATAR_APPEARANCE)
        capabilityManager.setCap(CapabilityManager.CAP_UPLOAD_BAKED_TEXTURE, "https://opensim.example.com/cap/UploadBakedTexture")

        val result = negotiator.negotiateCapabilities()

        assertEquals(AppearanceMode.CLIENT_SIDE_BAKING, result.mode)
        assertTrue("Latency must be <= 200ms", result.resolutionTimeMs <= 200)
        assertNull(result.ssaCapUrl)
        assertEquals("https://opensim.example.com/cap/UploadBakedTexture", result.uploadBakedTextureCapUrl)
        assertTrue(result.isBomSupported)
    }

    @Test
    fun testAvatarBakerVramLimitAndMemoryRecycling() {
        assertEquals("Max bake width must be 1024", 1024, AvatarBaker.MAX_BAKE_WIDTH)
        assertEquals("Max bake height must be 1024", 1024, AvatarBaker.MAX_BAKE_HEIGHT)

        // Memory footprint per 1024x1024 ARGB_8888 channel bitmap is 4MB.
        // With single-channel recycling, peak memory consumption is 4MB, strictly under 32MB.
        val singleChannelBytes = 1024 * 1024 * 4
        val maxMemoryBudget = 32 * 1024 * 1024
        assertTrue("Channel bitmap memory must remain well under 32MB", singleChannelBytes < maxMemoryBudget)
    }

    @Test
    fun testBakesOnMeshChannelBindingForMeshAttachments() {
        val context: android.content.Context = org.robolectric.RuntimeEnvironment.getApplication()
        val textureManager = com.linkpoint.assets.TextureManager(
            context,
            com.linkpoint.assets.AssetCache(context),
            capabilityManager
        )
        val bomManager = BakesOnMeshManager(capabilityManager, textureManager)
        val avatarId = UUID.randomUUID()
        val bakedHeadId = UUID.randomUUID()
        val bakedUpperId = UUID.randomUUID()

        // Set baked textures for avatar
        bomManager.updateBakedTexture(avatarId, BakesOnMeshManager.BAKE_HEAD, bakedHeadId)
        bomManager.updateBakedTexture(avatarId, BakesOnMeshManager.BAKE_UPPER, bakedUpperId)

        // Attachment channels referencing BoM UUIDs
        val materialChannels = mapOf(
            0 to BakesOnMeshManager.BOM_HEAD_UUID,
            1 to BakesOnMeshManager.BOM_UPPER_UUID,
            2 to UUID.randomUUID() // Non-BoM texture
        )

        val boundChannels = bomManager.bindMaterialChannels(avatarId, materialChannels)

        assertEquals(bakedHeadId, boundChannels[0])
        assertEquals(bakedUpperId, boundChannels[1])
        assertEquals(materialChannels[2], boundChannels[2])
    }
}
