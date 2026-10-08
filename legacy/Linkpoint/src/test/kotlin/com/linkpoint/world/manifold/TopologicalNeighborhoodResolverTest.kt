package com.linkpoint.world.manifold

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TopologicalNeighborhoodResolverTest {

    private fun packHandle(gridXMeters: Int, gridYMeters: Int): Long {
        return (gridXMeters.toLong() shl 40) or (gridYMeters.toLong() shl 8)
    }

    private fun unpackHandle(handle: Long): Pair<Int, Int> {
        val x = (handle shr 40).toInt()
        val y = ((handle shr 8) and 0xFFFFFFFF).toInt()
        return x to y
    }

    @Test
    fun `FLAT_2D calculates standard linear neighbor handles`() {
        val currentHandle = packHandle(256000, 256000)

        // Crossing East (localX >= 256)
        val eastHandle = TopologicalNeighborhoodResolver.getNeighborRegionHandle(
            currentHandle, 260f, 128f, ManifoldFrame.IDENTITY
        )
        assertNotNull(eastHandle)
        val (eastX, eastY) = unpackHandle(eastHandle!!)
        assertEquals(256256, eastX)
        assertEquals(256000, eastY)

        // Crossing West (localX < 0)
        val westHandle = TopologicalNeighborhoodResolver.getNeighborRegionHandle(
            currentHandle, -10f, 128f, ManifoldFrame.IDENTITY
        )
        assertNotNull(westHandle)
        val (westX, westY) = unpackHandle(westHandle!!)
        assertEquals(255744, westX)
        assertEquals(256000, westY)
    }

    @Test
    fun `RINGWORLD_CYLINDER wraps X-axis boundaries correctly`() {
        // Grid width is 1024 regions = 262144 meters
        val ringFrame = ManifoldFrame(
            frameId = "ring-alpha",
            topologyType = TopologyType.RINGWORLD_CYLINDER,
            sizeX = 262144.0, // 1024 regions
            sizeY = 256.0
        )

        // Region at rightmost boundary (grid index 1023 -> 261888 meters)
        val rightmostHandle = packHandle(261888, 256000)

        // Cross East beyond circumference -> wraps to 0
        val wrappedHandle = TopologicalNeighborhoodResolver.getNeighborRegionHandle(
            rightmostHandle, 260f, 128f, ringFrame
        )
        assertNotNull(wrappedHandle)
        val (wrappedX, wrappedY) = unpackHandle(wrappedHandle!!)
        assertEquals(0, wrappedX)
        assertEquals(256000, wrappedY)

        // Region at leftmost boundary (grid index 0 -> 0 meters)
        val leftmostHandle = packHandle(0, 256000)

        // Cross West below 0 -> wraps to rightmost index 1023 (261888 meters)
        val leftWrappedHandle = TopologicalNeighborhoodResolver.getNeighborRegionHandle(
            leftmostHandle, -10f, 128f, ringFrame
        )
        assertNotNull(leftWrappedHandle)
        val (leftWrappedX, _) = unpackHandle(leftWrappedHandle!!)
        assertEquals(261888, leftWrappedX)
    }

    @Test
    fun `SPHERE_3D handles pole reflection correctly`() {
        // Sphere with 4x4 regions
        val sphereFrame = ManifoldFrame(
            frameId = "sphere-01",
            topologyType = TopologyType.SPHERE_3D,
            sizeX = 1024.0, // 4 regions width
            sizeY = 1024.0  // 4 regions height
        )

        // Region at top boundary (index (0, 3) -> (0, 768))
        val northPoleHandle = packHandle(0, 768)

        // Crossing North beyond top -> reflects longitude by +gridWidth/2 (2 regions = +512m)
        val polarHandle = TopologicalNeighborhoodResolver.getNeighborRegionHandle(
            northPoleHandle, 128f, 260f, sphereFrame
        )
        assertNotNull(polarHandle)
        val (polarX, polarY) = unpackHandle(polarHandle!!)
        assertEquals(512, polarX)
        assertEquals(768, polarY)
    }

    @Test
    fun `returns null when avatar stays inside active region`() {
        val currentHandle = packHandle(256000, 256000)
        val result = TopologicalNeighborhoodResolver.getNeighborRegionHandle(
            currentHandle, 128f, 128f, ManifoldFrame.IDENTITY
        )
        assertNull(result)
    }
}
