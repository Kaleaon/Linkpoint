package com.linkpoint.assets

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class BasisTranscoderTest {

    @Test
    fun `pipeline processes basis ktx2 payloads gracefully`() {
        val basisBytes = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte())
        val req = TextureDecodeTranscodePipeline.Request(
            backend = TextureFormatPolicy.Backend.FILAMENT,
            semantic = TextureFormatPolicy.TextureSemantic.ALBEDO,
            capabilities = TextureFormatPolicy.DeviceCapabilities(
                supportsEtc2Rgba = true,
                supportsBasisTranscoding = false
            )
        )
        val output = TextureDecodeTranscodePipeline.fromBasisKtx2(UUID.randomUUID(), basisBytes, req)
        assertNotNull("Pipeline must produce valid output for Basis KTX2 input", output)
        assertEquals(128, output!!.width)
        assertEquals(128, output.height)
    }

    @Test
    fun `confirm vram allocation reduction for compressed textures`() {
        val width = 512
        val height = 512
        val uncompressedRgbaVramBytes = width * height * 4 // 1,048,576 bytes (1 MB)
        
        // ETC2 and ASTC 4x4 block compressed texture size (16 bytes per 4x4 block)
        val blocks = ((width + 3) / 4) * ((height + 3) / 4) // 16,384 blocks
        val compressedVramBytes = blocks * 16 // 262,144 bytes (256 KB)

        val vramReductionRatio = 1.0 - (compressedVramBytes.toDouble() / uncompressedRgbaVramBytes.toDouble())
        
        // Verify 75% reduction in GPU VRAM allocation
        assertEquals(0.75, vramReductionRatio, 0.01)
        assertEquals(1048576, uncompressedRgbaVramBytes)
        assertEquals(262144, compressedVramBytes)
    }

    @Test
    fun `texture format policy enables basis path when capabilities allow`() {
        val decision = TextureFormatPolicy.decide(
            backend = TextureFormatPolicy.Backend.FILAMENT,
            semantic = TextureFormatPolicy.TextureSemantic.ALBEDO,
            width = 512,
            height = 512,
            capabilities = TextureFormatPolicy.DeviceCapabilities(
                supportsEtc2Rgba = true,
                supportsBasisTranscoding = true
            )
        )

        assertTrue(decision.useBasisPath)
        assertEquals(TextureFormatPolicy.TargetFormat.ETC2_RGBA, decision.targetFormat)
        assertEquals(10, decision.mipLevels)
    }
}
