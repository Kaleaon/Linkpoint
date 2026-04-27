package com.linkpoint.assets

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TextureFormatPolicyTest {

    @Test
    fun `prefers ETC2 when supported`() {
        val decision = TextureFormatPolicy.decide(
            backend = TextureFormatPolicy.Backend.FILAMENT,
            semantic = TextureFormatPolicy.TextureSemantic.ALBEDO,
            width = 512,
            height = 256,
            capabilities = TextureFormatPolicy.DeviceCapabilities(
                supportsEtc2Rgba = true,
                supportsRgba8 = true,
                supportsBasisTranscoding = false,
                supportsEtcpak = true
            )
        )

        assertEquals(TextureFormatPolicy.TargetFormat.ETC2_RGBA, decision.targetFormat)
        assertTrue(decision.srgb)
        assertEquals(TextureFormatPolicy.mipLevelsFor(512, 256), decision.mipLevels)
        assertTrue(decision.useEtcpakPath)
    }

    @Test
    fun `uses RGBA8 when ETC2 unavailable`() {
        val decision = TextureFormatPolicy.decide(
            backend = TextureFormatPolicy.Backend.LUMIYA_GL,
            semantic = TextureFormatPolicy.TextureSemantic.DATA,
            width = 128,
            height = 128,
            capabilities = TextureFormatPolicy.DeviceCapabilities(
                supportsEtc2Rgba = false,
                supportsRgba8 = true,
                supportsBasisTranscoding = true,
                supportsEtcpak = false
            )
        )

        assertEquals(TextureFormatPolicy.TargetFormat.RGBA8, decision.targetFormat)
        assertFalse(decision.srgb)
        assertTrue(decision.useBasisPath)
        assertFalse(decision.useEtcpakPath)
    }
}

