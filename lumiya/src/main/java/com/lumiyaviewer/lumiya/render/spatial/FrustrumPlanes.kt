package com.lumiyaviewer.lumiya.render.spatial

class FrustrumPlanes {
    class Plane(
        val a: Float,
        val b: Float,
        val c: Float,
        val d: Float
    )

    val nearPlane = Plane(0f, 0f, 1f, 0f)
    val farPlane = Plane(0f, 0f, -1f, 1000f)
    val leftPlane = Plane(1f, 0f, 0f, 0f)
    val rightPlane = Plane(-1f, 0f, 0f, 0f)
    val topPlane = Plane(0f, -1f, 0f, 0f)
    val bottomPlane = Plane(0f, 1f, 0f, 0f)

    fun testBoundingBox(position: FloatArray, depthBuf: FloatArray): Int {
        // Stub
        return 1
    }
}
