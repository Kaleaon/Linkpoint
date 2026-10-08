package com.linkpoint.render.materials

import com.linkpoint.protocol.llsd.LLSDArray
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDReal
import com.linkpoint.protocol.llsd.LLSDString
import com.linkpoint.protocol.llsd.LLSDUUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class PbrToPhongTranscoderTest {

    @Before
    fun setUp() {
        PbrToPhongTranscoder.clearCache()
    }

    @Test
    fun testDielectricTranscoding() {
        // Dielectric (metallic = 0.0), roughness = 0.5
        val params = PbrToPhongTranscoder.transcode(
            baseColorR = 0.8f,
            baseColorG = 0.5f,
            baseColorB = 0.2f,
            baseColorA = 1.0f,
            metallic = 0.0f,
            roughness = 0.5f
        )

        // Diffuse retains base color for dielectrics (1 - 0 = 1)
        assertEquals(0.8f, params.diffuseR, 0.001f)
        assertEquals(0.5f, params.diffuseG, 0.001f)
        assertEquals(0.2f, params.diffuseB, 0.001f)

        // Specular color for dielectrics is non-conductive ~0.04 F0
        assertEquals(0.04f, params.specularR, 0.001f)
        assertEquals(0.04f, params.specularG, 0.001f)
        assertEquals(0.04f, params.specularB, 0.001f)

        // Specular exponent for roughness 0.5 should be moderate
        assertTrue(params.specularExponent in 1f..256f)
    }

    @Test
    fun testMetallicTranscoding() {
        // Metallic (metallic = 1.0)
        val params = PbrToPhongTranscoder.transcode(
            baseColorR = 0.9f,
            baseColorG = 0.7f,
            baseColorB = 0.3f,
            baseColorA = 1.0f,
            metallic = 1.0f,
            roughness = 0.2f
        )

        // Diffuse for pure metal is 0 (diffuse = baseColor * (1 - 1))
        assertEquals(0.0f, params.diffuseR, 0.001f)
        assertEquals(0.0f, params.diffuseG, 0.001f)
        assertEquals(0.0f, params.diffuseB, 0.001f)

        // Specular color for metals takes the tint of the base color
        assertEquals(0.9f, params.specularR, 0.001f)
        assertEquals(0.7f, params.specularG, 0.001f)
        assertEquals(0.3f, params.specularB, 0.001f)
    }

    @Test
    fun testRoughnessToExponentMapping() {
        // Smooth surface (roughness = 0.0) -> high exponent (sharp highlight)
        val smooth = PbrToPhongTranscoder.transcode(
            baseColorR = 1f, baseColorG = 1f, baseColorB = 1f, baseColorA = 1f,
            metallic = 0f, roughness = 0.0f
        )
        assertEquals(256.0f, smooth.specularExponent, 0.01f)

        // Rough surface (roughness = 1.0) -> low exponent (broad highlight)
        val rough = PbrToPhongTranscoder.transcode(
            baseColorR = 1f, baseColorG = 1f, baseColorB = 1f, baseColorA = 1f,
            metallic = 0f, roughness = 1.0f
        )
        assertEquals(1.0f, rough.specularExponent, 0.01f)

        assertTrue("Smooth exponent should be greater than rough exponent", smooth.specularExponent > rough.specularExponent)
    }

    @Test
    fun testCachingAndPerformance() {
        val startTime = System.nanoTime()

        // Transcode 100 materials
        for (i in 0 until 100) {
            PbrToPhongTranscoder.transcode(
                baseColorR = (i % 10) / 10f,
                baseColorG = (i % 5) / 5f,
                baseColorB = 0.5f,
                baseColorA = 1.0f,
                metallic = 0.1f,
                roughness = 0.4f
            )
        }

        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000.0
        val perItemMs = elapsedMs / 100.0

        // Requirement: CPU transcoding delay under 2ms per material load event
        assertTrue("Transcoding delay per item ($perItemMs ms) should be well below 2.0ms", perItemMs < 2.0)
        assertTrue(PbrToPhongTranscoder.cacheSize() > 0)
    }

    @Test
    fun testNaNAndEdgeCasesHandledSafely() {
        val params = PbrToPhongTranscoder.transcode(
            baseColorR = Float.NaN,
            baseColorG = Float.NaN,
            baseColorB = Float.NaN,
            baseColorA = Float.NaN,
            metallic = Float.NaN,
            roughness = Float.NaN
        )

        assertFalse(params.diffuseR.isNaN())
        assertFalse(params.diffuseG.isNaN())
        assertFalse(params.diffuseB.isNaN())
        assertFalse(params.diffuseA.isNaN())
        assertFalse(params.specularR.isNaN())
        assertFalse(params.specularExponent.isNaN())
    }

    @Test
    fun testPbrMaterialDecoderLLSD() {
        val texUUID = UUID.randomUUID()
        val normUUID = UUID.randomUUID()
        val mrUUID = UUID.randomUUID()

        val llsdData = LLSDMap().apply {
            this["BaseColor"] = LLSDArray().apply {
                add(LLSDReal(0.7))
                add(LLSDReal(0.8))
                add(LLSDReal(0.9))
                add(LLSDReal(1.0))
            }
            this["Metallic"] = LLSDReal(0.8)
            this["Roughness"] = LLSDReal(0.3)
            this["Textures"] = LLSDMap().apply {
                this["BaseColor"] = LLSDUUID(texUUID)
                this["Normal"] = LLSDUUID(normUUID)
                this["MetallicRoughness"] = LLSDUUID(mrUUID)
            }
        }

        val descriptor = GltfMaterialParser.parseLlsd(llsdData)

        assertEquals(0.7f, descriptor.baseColor.x, 0.01f)
        assertEquals(0.8f, descriptor.baseColor.y, 0.01f)
        assertEquals(0.9f, descriptor.baseColor.z, 0.01f)
        assertEquals(0.8f, descriptor.metallicFactor, 0.01f)
        assertEquals(0.3f, descriptor.roughnessFactor, 0.01f)

        assertNotNull(descriptor.baseColorTexture)
        assertEquals(texUUID, descriptor.baseColorTexture?.declaredId)

        assertNotNull(descriptor.normalTexture)
        assertEquals(normUUID, descriptor.normalTexture?.declaredId)

        assertNotNull(descriptor.metallicRoughnessTexture)
        assertEquals(mrUUID, descriptor.metallicRoughnessTexture?.declaredId)
    }
}
