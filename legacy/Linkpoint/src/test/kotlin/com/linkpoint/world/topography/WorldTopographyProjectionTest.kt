package com.linkpoint.world.topography

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.render.lumiya.spatial.SpatialIndex
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class WorldTopographyProjectionTest {

    private val delta = 0.01f

    @Test
    fun testPlanarTopographyProjection() {
        val planar = PlanarTopographyProjection()
        assertEquals(TopographyType.PLANAR, planar.topographyType)

        // Coordinate projection
        val cartesian = planar.projectToCartesian(100f, 150f, 25f)
        assertEquals(100f, cartesian[0], delta)
        assertEquals(150f, cartesian[1], delta)
        assertEquals(25f, cartesian[2], delta)

        val local = planar.projectFromCartesian(cartesian[0], cartesian[1], cartesian[2])
        assertEquals(100f, local[0], delta)
        assertEquals(150f, local[1], delta)
        assertEquals(25f, local[2], delta)

        // Normal and gravity
        val normal = planar.getSurfaceNormal(100f, 150f, 25f)
        assertEquals(0f, normal[0], delta)
        assertEquals(0f, normal[1], delta)
        assertEquals(1f, normal[2], delta)

        val gravity = planar.getGravityVector(100f, 150f, 25f, 9.8f)
        assertEquals(0f, gravity[0], delta)
        assertEquals(0f, gravity[1], delta)
        assertEquals(-9.8f, gravity[2], delta)

        // Bounds
        val bounds = planar.getProjectedBounds(10f, 10f, 10f, 20f, 20f, 20f)
        assertEquals(10f, bounds.minX, delta)
        assertEquals(20f, bounds.maxX, delta)

        // Neighbor handles
        val currentHandle = (1000L shl 40) or (2000L shl 8)
        assertNull(planar.getNeighborRegionHandle(currentHandle, 128f, 128f, 256))

        val eastNeighbor = planar.getNeighborRegionHandle(currentHandle, 300f, 128f, 256)
        assertNotNull(eastNeighbor)
        val eastRegionX = (eastNeighbor!! shr 40).toInt()
        assertEquals(1000 + 256, eastRegionX)

        val westNeighbor = planar.getNeighborRegionHandle(currentHandle, -50f, 128f, 256)
        assertNotNull(westNeighbor)
        val westRegionX = (westNeighbor!! shr 40).toInt()
        assertEquals(1000 - 256, westRegionX)
    }

    @Test
    fun testRingworldTopographyProjection() {
        val radius = 1000f
        val ringworld = RingworldTopographyProjection(radius = radius)
        assertEquals(TopographyType.RINGWORLD, ringworld.topographyType)

        // At localX = 0 (bottom of ring), theta = 0, normal should be (0, 0, 1), gravity (0, 0, -9.8)
        val normalBottom = ringworld.getSurfaceNormal(0f, 100f, 0f)
        assertEquals(0f, normalBottom[0], delta)
        assertEquals(0f, normalBottom[1], delta)
        assertEquals(1f, normalBottom[2], delta)

        val gravityBottom = ringworld.getGravityVector(0f, 100f, 0f, 9.8f)
        assertEquals(0f, gravityBottom[0], delta)
        assertEquals(0f, gravityBottom[1], delta)
        assertEquals(-9.8f, gravityBottom[2], delta)

        // At quarter ring (localX = PI/2 * radius), normal and gravity rotate along ring curve
        val quarterX = (Math.PI / 2.0 * radius).toFloat()
        val normalQuarter = ringworld.getSurfaceNormal(quarterX, 100f, 0f)
        assertEquals(-1f, normalQuarter[0], delta)
        assertEquals(0f, normalQuarter[1], delta)
        assertEquals(0f, normalQuarter[2], delta)

        val gravityQuarter = ringworld.getGravityVector(quarterX, 100f, 0f, 9.8f)
        assertEquals(9.8f, gravityQuarter[0], delta)
        assertEquals(0f, gravityQuarter[1], delta)
        assertEquals(0f, gravityQuarter[2], delta)

        // Coordinate roundtrip
        val cart = ringworld.projectToCartesian(quarterX, 120f, 10f)
        val backLocal = ringworld.projectFromCartesian(cart[0], cart[1], cart[2])
        assertEquals(quarterX, backLocal[0], 0.1f)
        assertEquals(120f, backLocal[1], 0.1f)
        assertEquals(10f, backLocal[2], 0.1f)

        // Circumferential wrapping neighbor handles
        val circumference = (2.0 * Math.PI * radius).toFloat()
        val totalRegions = Math.round(circumference / 256f)
        val worldWidthX = totalRegions * 256
        val currentHandle = (0L shl 40) or (1000L shl 8)

        // Moving past negative X wraps to the end of ringworld manifold
        val wrappedWestHandle = ringworld.getNeighborRegionHandle(currentHandle, -10f, 128f, 256)
        assertNotNull(wrappedWestHandle)
        val wrappedWestX = (wrappedWestHandle!! shr 40).toInt()
        assertEquals(worldWidthX - 256, wrappedWestX)
    }

    @Test
    fun testSphericalTopographyProjection() {
        val radius = 1000f
        val sphere = SphericalTopographyProjection(radius = radius)
        assertEquals(TopographyType.SPHERICAL, sphere.topographyType)

        // Equator at origin (localX=0, localY=0): normal is (0, 0, 1), gravity (0, 0, -9.8)
        val normalEq = sphere.getSurfaceNormal(0f, 0f, 0f)
        assertEquals(0f, normalEq[0], delta)
        assertEquals(0f, normalEq[1], delta)
        assertEquals(1f, normalEq[2], delta)

        val gravityEq = sphere.getGravityVector(0f, 0f, 0f, 9.8f)
        assertEquals(0f, gravityEq[0], delta)
        assertEquals(0f, gravityEq[1], delta)
        assertEquals(-9.8f, gravityEq[2], delta)

        // Coordinate roundtrip
        val cart = sphere.projectToCartesian(100f, -50f, 15f)
        val backLocal = sphere.projectFromCartesian(cart[0], cart[1], cart[2])
        assertEquals(100f, backLocal[0], 0.1f)
        assertEquals(-50f, backLocal[1], 0.1f)
        assertEquals(15f, backLocal[2], 0.1f)

        // Bounding box projection
        val projectedBounds = sphere.getProjectedBounds(-10f, -10f, 0f, 10f, 10f, 10f)
        assertTrue(projectedBounds.maxX > projectedBounds.minX)
        assertTrue(projectedBounds.maxY > projectedBounds.minY)
        assertTrue(projectedBounds.maxZ > projectedBounds.minZ)

        // Neighbor handle lookup across non-linear spherical manifold
        val currentHandle = (0L shl 40) or (0L shl 8)
        val neighbor = sphere.getNeighborRegionHandle(currentHandle, 300f, 128f, 256)
        assertNotNull(neighbor)
    }

    @Test
    fun testTopographyNetworkSerializer() {
        val ringworld = RingworldTopographyProjection(radius = 500f)
        val localX = 150f; val localY = 200f; val localZ = 30f

        // Serialize local manifold coords to Cartesian protocol packet array
        val packetData = TopographyNetworkSerializer.toCartesianProtocolPacket(localX, localY, localZ, ringworld)
        assertNotNull(packetData)
        assertEquals(3, packetData.size)

        // Deserialize Cartesian protocol packet array to local manifold coords
        val restored = TopographyNetworkSerializer.fromCartesianProtocolPacket(packetData[0], packetData[1], packetData[2], ringworld)
        assertEquals(localX, restored[0], 0.1f)
        assertEquals(localY, restored[1], 0.1f)
        assertEquals(localZ, restored[2], 0.1f)
    }
}
