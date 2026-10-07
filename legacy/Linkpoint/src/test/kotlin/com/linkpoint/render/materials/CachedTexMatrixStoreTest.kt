package com.linkpoint.render.materials

import com.linkpoint.render.lumiya.drawable.DrawableMeshStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CachedTexMatrixStoreTest {

    @Test
    fun testFaceMaterialIdentityFastPath() {
        val meshFace1 = DrawableMeshStore.FaceMaterial()
        assertTrue(meshFace1.isDirty)
        val matrix1 = meshFace1.getMatrix()
        assertFalse(meshFace1.isDirty)

        // Identity fast path returns static identity matrix constant
        val identityRef = MaterialDescriptor.UvTransform.IDENTITY_MATRIX
        assertSame("Identity FaceMaterial should return shared IDENTITY_MATRIX constant", identityRef, matrix1)

        val meshFace2 = DrawableMeshStore.FaceMaterial()
        assertTrue(meshFace2.isDirty)
        val meshMatrix = meshFace2.getMatrix()
        assertFalse(meshFace2.isDirty)
        assertSame("Identity Mesh FaceMaterial should return shared IDENTITY_MATRIX constant", identityRef, meshMatrix)
    }

    @Test
    fun testFaceMaterialDirtyStateTrackingAndCaching() {
        val face = DrawableMeshStore.FaceMaterial()
        face.getMatrix() // clear initial dirty flag

        // Mutate scale
        face.scaleS = 2.0f
        assertTrue("Mutating scaleS must set dirty flag", face.isDirty)

        val matrix1 = face.getMatrix()
        assertFalse("getMatrix() must clear dirty flag", face.isDirty)

        // Matrix output when non-identity must write to pre-allocated buffer
        val matrix2 = face.getMatrix()
        assertSame("Subsequent reads without parameter changes must return cached matrix reference", matrix1, matrix2)

        // Mutate offset
        face.offsetT = 0.5f
        assertTrue("Mutating offsetT must set dirty flag", face.isDirty)

        val matrix3 = face.getMatrix()
        assertSame("Recalculated matrix must reuse pre-allocated buffer reference", matrix1, matrix3)

        // Reset back to identity
        face.scaleS = 1.0f
        face.offsetT = 0.0f
        assertTrue("Resetting to identity parameters must set dirty flag", face.isDirty)

        val matrix4 = face.getMatrix()
        assertSame("Returning to identity must fast-path to static IDENTITY_MATRIX", MaterialDescriptor.UvTransform.IDENTITY_MATRIX, matrix4)
    }

    @Test
    fun testUvTransformPrecomputedMatrixIntegration() {
        val face = DrawableMeshStore.FaceMaterial(
            scaleS = 2f,
            scaleT = 3f,
            offsetS = 0.1f,
            offsetT = 0.2f,
            rotation = 0.5f
        )
        val faceMatrix = face.getMatrix()

        val uvTransform = MaterialDescriptor.UvTransform(
            scaleS = face.scaleS,
            scaleT = face.scaleT,
            offsetS = face.offsetS,
            offsetT = face.offsetT,
            rotation = face.rotation,
            precomputedMatrix = faceMatrix
        )

        assertSame("UvTransform with precomputedMatrix must return exact cached reference", faceMatrix, uvTransform.matrix)
    }

    @Test
    fun testMatrixMathCorrectnessForScaledAndOffsetUV() {
        val scaleS = 2.0f
        val scaleT = 4.0f
        val offsetS = 0.25f
        val offsetT = -0.5f
        val rotation = 0.0f

        val face = DrawableMeshStore.FaceMaterial(
            scaleS = scaleS,
            scaleT = scaleT,
            offsetS = offsetS,
            offsetT = offsetT,
            rotation = rotation
        )

        val matrix = face.getMatrix()

        // Matrix columns for 2D affine transform around UV center (0.5, 0.5):
        // Translate(0.5 + offsetS, 0.5 + offsetT) * Scale(scaleS, scaleT) * Translate(-0.5, -0.5)
        // m[0] = scaleS
        // m[5] = scaleT
        // m[12] = 0.5 + offsetS - 0.5 * scaleS
        // m[13] = 0.5 + offsetT - 0.5 * scaleT
        val expectedM12 = 0.5f + offsetS - 0.5f * scaleS
        val expectedM13 = 0.5f + offsetT - 0.5f * scaleT

        assertEquals(scaleS, matrix[0], 1e-5f)
        assertEquals(scaleT, matrix[5], 1e-5f)
        assertEquals(1.0f, matrix[10], 1e-5f)
        assertEquals(1.0f, matrix[15], 1e-5f)
        assertEquals(expectedM12, matrix[12], 1e-5f)
        assertEquals(expectedM13, matrix[13], 1e-5f)
    }

    @Test
    fun testZeroAllocationsInSimulatedRenderLoop() {
        val face = DrawableMeshStore.FaceMaterial(scaleS = 1.5f, scaleT = 1.5f)
        val initialMatrix = face.getMatrix()

        // Simulate 10,000 frame loop calls
        val references = Array(10000) { face.getMatrix() }

        for (i in references.indices) {
            assertSame("Render loop access #$i must return cached matrix without new allocation", initialMatrix, references[i])
        }
    }
}
