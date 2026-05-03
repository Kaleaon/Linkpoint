package com.linkpoint.render.driver

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure-math tests for [resolveFeatureFlags]. These exercise the
 * actual decisions a real device would produce — capability flags
 * are the floor under every optional render pass, so a regression
 * here means the renderer would either crash on a low-end device or
 * skip a feature it could otherwise have used.
 */
class FeatureFlagsResolverTest {

    private val noVulkan = GraphicsDriverProbe.DriverProfile.from(glesMajor = 3, glesMinor = 1)
    private val withVulkan = GraphicsDriverProbe.DriverProfile.from(
        glesMajor = 3, glesMinor = 2,
        vulkanLevel = 1, vulkanVersion = 0x40300, vulkanDeqp = 132317184
    )

    private fun caps(
        glVersion: Int = 31,
        vendor: GraphicsDriverProbe.VendorFamily = GraphicsDriverProbe.VendorFamily.ADRENO,
        extensions: Set<String> = emptySet(),
        maxTex: Int = 4096,
        maxRb: Int = 4096,
        maxAniso: Float = 1f
    ): GpuCapabilities = GpuCapabilities(
        glVersion = glVersion,
        vendor = vendor,
        rendererString = "test",
        vendorString = "test",
        versionString = "OpenGL ES $glVersion.0",
        extensions = extensions,
        maxTextureSize = maxTex,
        maxRenderbufferSize = maxRb,
        maxSamples = 4,
        maxUniformBlockSize = 16384,
        maxAnisotropy = maxAniso
    )

    @Test
    fun `low end GLES 3_0 enables FXAA + occlusion but not reflections`() {
        val flags = resolveFeatureFlags(noVulkan, caps(glVersion = 30, maxTex = 2048, maxRb = 2048))
        assertTrue("FXAA needs only GLES 3.0 + 1024 max tex", flags.fxaa)
        assertTrue("Occlusion queries are GLES 3.0 baseline", flags.occlusionQueries)
        assertFalse("Reflections require GLES 3.1+", flags.waterPlanarReflections)
        assertFalse("No compute on GLES 3.0", flags.computeShaders)
    }

    @Test
    fun `GLES 3_1 with large max texture enables reflections and compute`() {
        val flags = resolveFeatureFlags(noVulkan, caps(glVersion = 31, maxTex = 4096, maxRb = 4096))
        assertTrue(flags.waterPlanarReflections)
        assertTrue(flags.computeShaders)
    }

    @Test
    fun `tiny max texture disables FXAA`() {
        val flags = resolveFeatureFlags(noVulkan, caps(glVersion = 30, maxTex = 512))
        assertFalse(flags.fxaa)
    }

    @Test
    fun `pre-GLES 3_0 disables every optional pass`() {
        val flags = resolveFeatureFlags(noVulkan, caps(glVersion = 20))
        assertFalse(flags.fxaa)
        assertFalse(flags.occlusionQueries)
        assertFalse(flags.waterPlanarReflections)
        assertFalse(flags.computeShaders)
    }

    @Test
    fun `persistent mapped UBO enabled when extension present and vendor not powervr`() {
        val flags = resolveFeatureFlags(
            noVulkan,
            caps(extensions = setOf("GL_EXT_buffer_storage"))
        )
        assertTrue(flags.persistentMappedUBO)
    }

    @Test
    fun `persistent mapped UBO disabled on PowerVR even with extension`() {
        val flags = resolveFeatureFlags(
            noVulkan,
            caps(
                vendor = GraphicsDriverProbe.VendorFamily.POWERVR,
                extensions = setOf("GL_EXT_buffer_storage")
            )
        )
        assertFalse("PowerVR persistent maps were buggy through 2022", flags.persistentMappedUBO)
    }

    @Test
    fun `persistent mapped UBO disabled when extension absent`() {
        val flags = resolveFeatureFlags(noVulkan, caps(extensions = emptySet()))
        assertFalse(flags.persistentMappedUBO)
    }

    @Test
    fun `anisotropic filtering requires both extension and max gt 1`() {
        val noExt = resolveFeatureFlags(noVulkan, caps(maxAniso = 16f))
        assertFalse(noExt.anisotropicFiltering)

        val noMax = resolveFeatureFlags(
            noVulkan,
            caps(extensions = setOf("GL_EXT_texture_filter_anisotropic"), maxAniso = 1f)
        )
        assertFalse(noMax.anisotropicFiltering)

        val both = resolveFeatureFlags(
            noVulkan,
            caps(extensions = setOf("GL_EXT_texture_filter_anisotropic"), maxAniso = 16f)
        )
        assertTrue(both.anisotropicFiltering)
    }

    @Test
    fun `Vulkan readiness reflects probe state only`() {
        // No-Vulkan profile: false regardless of GLES caps.
        val noVk = resolveFeatureFlags(noVulkan, caps(glVersion = 32))
        assertFalse(noVk.vulkanReady)
        // Vulkan-level-0 profile counts as not ready (limited driver).
        val limited = GraphicsDriverProbe.DriverProfile.from(3, 2, vulkanLevel = 0)
        assertFalse(resolveFeatureFlags(limited, caps()).vulkanReady)
        // Full Vulkan: ready.
        assertTrue(resolveFeatureFlags(withVulkan, caps()).vulkanReady)
    }

    @Test
    fun `tile-based renderer flag matches mobile vendor families`() {
        for (mobile in listOf(
            GraphicsDriverProbe.VendorFamily.ADRENO,
            GraphicsDriverProbe.VendorFamily.MALI,
            GraphicsDriverProbe.VendorFamily.POWERVR,
            GraphicsDriverProbe.VendorFamily.XCLIPSE
        )) {
            val flags = resolveFeatureFlags(noVulkan, caps(vendor = mobile))
            assertTrue("$mobile should be tile-based", flags.tileBasedRenderer)
        }
        for (desktop in listOf(
            GraphicsDriverProbe.VendorFamily.TEGRA,
            GraphicsDriverProbe.VendorFamily.INTEL,
            GraphicsDriverProbe.VendorFamily.UNKNOWN
        )) {
            val flags = resolveFeatureFlags(noVulkan, caps(vendor = desktop))
            assertFalse("$desktop should not be tile-based", flags.tileBasedRenderer)
        }
    }

    @Test
    fun `ALL_OFF defaults safe`() {
        // Sanity: every optional feature defaults to off when the
        // renderer hasn't yet resolved a capability snapshot.
        assertEquals(false, FeatureFlags.ALL_OFF.fxaa)
        assertEquals(false, FeatureFlags.ALL_OFF.waterPlanarReflections)
        assertEquals(false, FeatureFlags.ALL_OFF.occlusionQueries)
        assertEquals(false, FeatureFlags.ALL_OFF.persistentMappedUBO)
        assertEquals(false, FeatureFlags.ALL_OFF.anisotropicFiltering)
        assertEquals(false, FeatureFlags.ALL_OFF.computeShaders)
        assertEquals(false, FeatureFlags.ALL_OFF.vulkanReady)
        assertEquals(false, FeatureFlags.ALL_OFF.tileBasedRenderer)
    }
}
