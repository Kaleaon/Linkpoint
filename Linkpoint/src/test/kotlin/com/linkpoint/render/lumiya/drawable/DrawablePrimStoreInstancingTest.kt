package com.linkpoint.render.lumiya.drawable

import com.linkpoint.protocol.messages.PrimShapeParams
import com.linkpoint.render.lumiya.drawable.DrawablePrimStore.FaceMaterial
import com.linkpoint.render.lumiya.drawable.DrawablePrimStore.InstancedBatchKey
import com.linkpoint.render.lumiya.drawable.DrawablePrimStore.InstanceVboPool
import com.linkpoint.render.lumiya.drawable.DrawablePrimStore.MaterialBatchKey
import com.linkpoint.render.lumiya.drawable.DrawablePrimStore.PrimInstance
import com.linkpoint.render.lumiya.drawable.DrawablePrimStore.ShapeKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.nio.FloatBuffer
import java.util.UUID

class DrawablePrimStoreInstancingTest {

    private lateinit var primStore: DrawablePrimStore

    @Before
    fun setUp() {
        primStore = DrawablePrimStore()
    }

    @Test
    fun testMaterialBatchKeyEqualityForIdenticalMaterials() {
        val mat1 = FaceMaterial(
            textureHandle = 10,
            colorR = 0.8f, colorG = 0.2f, colorB = 0.5f, colorA = 1.0f,
            scaleS = 1.0f, scaleT = 1.0f,
            metallicFactor = 0.2f, roughnessFactor = 0.8f
        )
        val mat2 = FaceMaterial(
            textureHandle = 10,
            colorR = 0.8f, colorG = 0.2f, colorB = 0.5f, colorA = 1.0f,
            scaleS = 1.0f, scaleT = 1.0f,
            metallicFactor = 0.2f, roughnessFactor = 0.8f
        )
        val mat3 = FaceMaterial(
            textureHandle = 11, // Different texture handle
            colorR = 0.8f, colorG = 0.2f, colorB = 0.5f, colorA = 1.0f
        )

        val key1 = InstancedBatchKey(ShapeKind.BOX, MaterialBatchKey(
            mat1.textureHandle, mat1.normalHandle, mat1.metallicRoughnessHandle, mat1.emissiveHandle, mat1.occlusionHandle,
            mat1.colorR, mat1.colorG, mat1.colorB, mat1.colorA, mat1.scaleS, mat1.scaleT, mat1.offsetS, mat1.offsetT, mat1.rotation,
            mat1.metallicFactor, mat1.roughnessFactor, mat1.glow
        ))

        val key2 = InstancedBatchKey(ShapeKind.BOX, MaterialBatchKey(
            mat2.textureHandle, mat2.normalHandle, mat2.metallicRoughnessHandle, mat2.emissiveHandle, mat2.occlusionHandle,
            mat2.colorR, mat2.colorG, mat2.colorB, mat2.colorA, mat2.scaleS, mat2.scaleT, mat2.offsetS, mat2.offsetT, mat2.rotation,
            mat2.metallicFactor, mat2.roughnessFactor, mat2.glow
        ))

        val key3 = InstancedBatchKey(ShapeKind.BOX, MaterialBatchKey(
            mat3.textureHandle, mat3.normalHandle, mat3.metallicRoughnessHandle, mat3.emissiveHandle, mat3.occlusionHandle,
            mat3.colorR, mat3.colorG, mat3.colorB, mat3.colorA, mat3.scaleS, mat3.scaleT, mat3.offsetS, mat3.offsetT, mat3.rotation,
            mat3.metallicFactor, mat3.roughnessFactor, mat3.glow
        ))

        assertEquals("Identical material properties must produce identical batch keys", key1, key2)
        assertFalse("Differing texture handles must produce different batch keys", key1 == key3)
    }

    @Test
    fun testInstanceVboPoolResizesWithoutDataLoss() {
        val pool = InstanceVboPool()
        val initialCapacity = pool.floatBuffer.capacity() / 16

        // Initial capacity should accommodate at least 1024 instances
        assertTrue("Initial VBO pool capacity should be at least 1024 instances", initialCapacity >= 1024)

        val requiredInstances = 2000
        pool.ensureCapacity(requiredInstances)

        val newCapacity = pool.floatBuffer.capacity() / 16
        assertTrue("VBO pool capacity must expand to accommodate required instances", newCapacity >= requiredInstances)

        // Verify buffer clear and put operations
        val floatBuffer = pool.floatBuffer
        floatBuffer.clear()
        val matrixData = FloatArray(16) { it.toFloat() }
        floatBuffer.put(matrixData)
        floatBuffer.flip()

        assertEquals("Buffer position after flip should be 0", 0, floatBuffer.position())
        assertEquals("Buffer limit after flip should be 16 floats", 16, floatBuffer.limit())
        for (i in 0 until 16) {
            assertEquals("Matrix element $i must match written data", i.toFloat(), floatBuffer.get(), 0.0001f)
        }
    }

    @Test
    fun testGroupOpaquePrimitivesByShapeAndMaterial() {
        // Add multiple boxes with identical materials
        for (i in 1..5) {
            primStore.addPrim(i.toLong(), i * 2.0f, 0.0f, 0.0f)
        }

        // Add 3 spheres with a different material tint
        for (i in 6..8) {
            primStore.upsertPrim(
                id = i.toLong(),
                posX = i * 2.0f, posY = 0.0f, posZ = 0.0f,
                scaleX = 1.0f, scaleY = 1.0f, scaleZ = 1.0f,
                shapeParams = PrimShapeParams(
                    pathCurve = PrimShapeParams.PATH_CIRCLE,
                    profileCurve = PrimShapeParams.PROFILE_CIRCLE
                )
            )
        }

        assertEquals("Prim store should contain 8 total instances", 8, primStore.primCount())

        val snapshot = primStore.snapshot()
        val boxes = snapshot.filter { it.shape == ShapeKind.BOX }
        val spheres = snapshot.filter { it.shape == ShapeKind.SPHERE }

        assertEquals("There should be 5 box instances", 5, boxes.size)
        assertEquals("There should be 3 sphere instances", 3, spheres.size)
    }

    @Test
    fun testTransparencyFallbackFlag() {
        primStore.addPrim(100L, 0.0f, 0.0f, 0.0f)
        var instance = primStore.snapshot().first { it.id == 100L }
        assertFalse("Newly added prim should be opaque by default", instance.isTransparent)

        primStore.setPrimTransparent(100L, true)
        instance = primStore.snapshot().first { it.id == 100L }
        assertTrue("Prim marked transparent should reflect transparent flag", instance.isTransparent)
    }
}
