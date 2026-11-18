package com.linkpoint.scene.math

import com.linkpoint.linden.llmath.Quaternion
import com.linkpoint.linden.llmath.Vector3
import com.linkpoint.linden.llmath.Vector4
import org.junit.Assert.*
import org.junit.Test

class Matrix4Test {

    private val eps = 1e-6f

    @Test
    fun testDefaultConstructorIsIdentity() {
        val m = Matrix4()
        assertTrue(m.isIdentity())
        val id = Matrix4.identity()
        assertTrue(id.isIdentity())
        assertEquals(1f, m[0, 0], eps)
        assertEquals(1f, m[1, 1], eps)
        assertEquals(1f, m[2, 2], eps)
        assertEquals(1f, m[3, 3], eps)
        assertEquals(0f, m[0, 1], eps)
        assertEquals(0f, m[1, 0], eps)
    }

    @Test
    fun testRowMajorArrayLayout() {
        val arr = FloatArray(16) { it.toFloat() }
        val m = Matrix4(arr)
        // Row 0
        assertEquals(0f, m[0, 0], eps)
        assertEquals(1f, m[0, 1], eps)
        assertEquals(2f, m[0, 2], eps)
        assertEquals(3f, m[0, 3], eps)
        // Row 1
        assertEquals(4f, m[1, 0], eps)
        assertEquals(5f, m[1, 1], eps)
        assertEquals(6f, m[1, 2], eps)
        assertEquals(7f, m[1, 3], eps)
        // Row 3
        assertEquals(12f, m[3, 0], eps)
        assertEquals(13f, m[3, 1], eps)
        assertEquals(14f, m[3, 2], eps)
        assertEquals(15f, m[3, 3], eps)
    }

    @Test
    fun testTranslationSetAndGet() {
        val m = Matrix4()
        m.setTranslation(10f, 20f, 30f)
        val trans = m.getTranslation()
        assertEquals(10f, trans.x, eps)
        assertEquals(20f, trans.y, eps)
        assertEquals(30f, trans.z, eps)

        val v = Vector3(1f, 2f, 3f)
        val transformed = m * v
        assertEquals(11f, transformed.x, eps)
        assertEquals(22f, transformed.y, eps)
        assertEquals(33f, transformed.z, eps)
    }

    @Test
    fun testMatrixMultiplicationAndPrecision() {
        val m1 = Matrix4()
        m1.setTranslation(5f, 10f, 15f)

        val m2 = Matrix4()
        m2.setTranslation(1f, 2f, 3f)

        val combined = m1 * m2
        val trans = combined.getTranslation()
        assertEquals(6f, trans.x, eps)
        assertEquals(12f, trans.y, eps)
        assertEquals(18f, trans.z, eps)
    }

    @Test
    fun testMatrixInverseAndPrecision() {
        val q = Quaternion().also { it.setAngleAxis(0.5f, 0f, 1f, 0f) }
        val pos = Vector3(10f, -5f, 20f)
        val m = Matrix4.fromQuaternionAndTranslation(q, pos)

        val inv = m.inversed()
        val identity = m * inv

        assertTrue("Multiplication with inverse must produce identity within 1e-6 tolerance", identity.isIdentity() || isApproxIdentity(identity))
    }

    @Test
    fun testRotateVectorDoesNotApplyTranslation() {
        val m = Matrix4()
        m.setTranslation(100f, 200f, 300f)

        val v = Vector3(1f, 2f, 3f)
        val rotated = m.rotateVector(v)

        assertEquals(1f, rotated.x, eps)
        assertEquals(2f, rotated.y, eps)
        assertEquals(3f, rotated.z, eps)
    }

    @Test
    fun testQuaternionRoundtrip() {
        val q = Quaternion().also { it.setAngleAxis(0.785398f, 0f, 0f, 1f) } // 45 deg Z rotation
        val m = Matrix4.fromQuaternion(q)
        val qResult = m.toQuaternion()

        val diff1 = Math.abs(q.x - qResult.x) + Math.abs(q.y - qResult.y) + Math.abs(q.z - qResult.z) + Math.abs(q.w - qResult.w)
        val diff2 = Math.abs(q.x + qResult.x) + Math.abs(q.y + qResult.y) + Math.abs(q.z + qResult.z) + Math.abs(q.w + qResult.w)

        assertTrue(diff1 < 1e-5f || diff2 < 1e-5f)
    }

    private fun isApproxIdentity(m: Matrix4): Boolean {
        for (r in 0..3) {
            for (c in 0..3) {
                val expected = if (r == c) 1f else 0f
                if (Math.abs(m[r, c] - expected) > 1e-5f) return false
            }
        }
        return true
    }
}
