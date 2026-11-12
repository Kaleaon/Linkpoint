package com.linkpoint.scene.math

import com.linkpoint.linden.llmath.BBox
import com.linkpoint.linden.llmath.BBoxLocal
import com.linkpoint.linden.llmath.CoordFrame
import com.linkpoint.linden.llmath.Quaternion
import com.linkpoint.linden.llmath.Vector3
import org.junit.Assert.*
import org.junit.Test

class BBoxCoordFrameSceneMathTest {

    private val eps = 1e-5f

    @Test
    fun testCoordFrameMatrixToLocal() {
        val frame = CoordFrame(Vector3(10f, 20f, 30f))
        val mat = Matrix4()
        frame.getMatrixToLocal(mat)

        val worldPos = Vector3(15f, 25f, 35f)
        val localPos = frame.transformToLocal(worldPos)
        val matrixTransformed = mat * worldPos

        assertEquals(localPos.x, matrixTransformed.x, eps)
        assertEquals(localPos.y, matrixTransformed.y, eps)
        assertEquals(localPos.z, matrixTransformed.z, eps)
    }

    @Test
    fun testBBoxTransformAndAgentToLocal() {
        val bbox = BBox(
            posAgent = Vector3(100f, 100f, 100f),
            rot = Quaternion(),
            minLocal = Vector3(-1f, -1f, -1f),
            maxLocal = Vector3(1f, 1f, 1f)
        )

        val ptAgent = Vector3(100.5f, 100.5f, 100.5f)
        assertTrue(bbox.containsPointAgent(ptAgent))

        val outsideAgent = Vector3(105f, 100f, 100f)
        assertFalse(bbox.containsPointAgent(outsideAgent))
    }

    @Test
    fun testBBoxLocalTransformWithSceneMatrix() {
        val bboxLocal = BBoxLocal(Vector3(-2f, -2f, -2f), Vector3(2f, 2f, 2f))
        val m = Matrix4()
        m.setTranslation(10f, 20f, 30f)

        val transformed = bboxLocal.transform(m)
        assertEquals(8f, transformed.min.x, eps)
        assertEquals(18f, transformed.min.y, eps)
        assertEquals(28f, transformed.min.z, eps)
        assertEquals(12f, transformed.max.x, eps)
        assertEquals(22f, transformed.max.y, eps)
        assertEquals(32f, transformed.max.z, eps)
    }
}
