package com.linkpoint.render.materials

import com.linkpoint.render.lumiya.shaders.PrimShaderProgram
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class GlesMaterialTranslatorTest {

    @Test
    fun testTextureBindingsDefaults() {
        val bindings = GlesMaterialTranslator.TextureBindings()
        assertEquals(0, bindings.baseColorHandle)
        assertEquals(0, bindings.normalHandle)
        assertEquals(0, bindings.metallicRoughnessHandle)
        assertEquals(0, bindings.emissiveHandle)
        assertEquals(0, bindings.occlusionHandle)
    }

    @Test
    fun testTextureBindingsWithHandles() {
        val bindings = GlesMaterialTranslator.TextureBindings(
            baseColorHandle = 101,
            normalHandle = 102,
            metallicRoughnessHandle = 103,
            emissiveHandle = 104,
            occlusionHandle = 105
        )
        assertEquals(101, bindings.baseColorHandle)
        assertEquals(102, bindings.normalHandle)
        assertEquals(103, bindings.metallicRoughnessHandle)
        assertEquals(104, bindings.emissiveHandle)
        assertEquals(105, bindings.occlusionHandle)
    }

    @Test
    fun testMaterialDescriptor5ChannelDefaults() {
        val dummyUuid = UUID.randomUUID()
        val desc = MaterialDescriptor(
            baseColor = MaterialDescriptor.Float4(1f, 0f, 0f, 1f),
            baseColorTexture = MaterialDescriptor.TextureRef(dummyUuid, dummyUuid),
            normalTexture = MaterialDescriptor.TextureRef(dummyUuid, dummyUuid),
            metallicRoughnessTexture = MaterialDescriptor.TextureRef(dummyUuid, dummyUuid),
            emissiveTexture = MaterialDescriptor.TextureRef(dummyUuid, dummyUuid),
            occlusionTexture = MaterialDescriptor.TextureRef(dummyUuid, dummyUuid),
            metallicFactor = 0.8f,
            roughnessFactor = 0.3f,
            occlusionFactor = 0.9f,
            normalScale = 1.2f
        )

        assertNotNull(desc.baseColorTexture)
        assertNotNull(desc.normalTexture)
        assertNotNull(desc.metallicRoughnessTexture)
        assertNotNull(desc.emissiveTexture)
        assertNotNull(desc.occlusionTexture)

        assertEquals(0.8f, desc.metallicFactor, 0.001f)
        assertEquals(0.3f, desc.roughnessFactor, 0.001f)
        assertEquals(0.9f, desc.occlusionFactor, 0.001f)
        assertEquals(1.2f, desc.normalScale, 0.001f)
    }

    @Test
    fun testApplyGles30ModeBindsMetallicRoughnessMap() {
        val dummyUuid = UUID.randomUUID()
        val desc = MaterialDescriptor(
            metallicRoughnessTexture = MaterialDescriptor.TextureRef(dummyUuid, dummyUuid),
            metallicFactor = 0.8f,
            roughnessFactor = 0.4f
        )
        val bindings = GlesMaterialTranslator.TextureBindings(metallicRoughnessHandle = 103)
        val mockProgram = mock<PrimShaderProgram>()

        GlesMaterialTranslator.apply(mockProgram, desc, bindings, isGles20Fallback = false)

        verify(mockProgram).setHasMetallicRoughnessMap(true)
        verify(mockProgram).setMetallicRoughnessMapSampler(2)
        verify(mockProgram).setIsGles20Fallback(false)
    }

    @Test
    fun testApplyGles20FallbackSuppressesMetallicRoughnessMap() {
        val dummyUuid = UUID.randomUUID()
        val desc = MaterialDescriptor(
            metallicRoughnessTexture = MaterialDescriptor.TextureRef(dummyUuid, dummyUuid),
            metallicFactor = 0.8f,
            roughnessFactor = 0.4f
        )
        val bindings = GlesMaterialTranslator.TextureBindings(metallicRoughnessHandle = 103)
        val mockProgram = mock<PrimShaderProgram>()

        GlesMaterialTranslator.apply(mockProgram, desc, bindings, isGles20Fallback = true)

        verify(mockProgram).setHasMetallicRoughnessMap(false)
        verify(mockProgram).setIsGles20Fallback(true)
    }
}
