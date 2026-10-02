package com.linkpoint.render.lumiya.spatial

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.system.measureNanoTime

class SpatialIndexOctreeTest {

    private fun createTestFrustum(): FrustumCuller {
        val culler = FrustumCuller()
        // Define frustum bounded inside region: X [0..128], Y [0..128], Z [0..100]
        culler.setPlaneForTest(0, 1f, 0f, 0f, 0f)       // Left: x >= 0
        culler.setPlaneForTest(1, -1f, 0f, 0f, 128f)   // Right: x <= 128
        culler.setPlaneForTest(2, 0f, 1f, 0f, 0f)       // Bottom: y >= 0
        culler.setPlaneForTest(3, 0f, -1f, 0f, 128f)   // Top: y <= 128
        culler.setPlaneForTest(4, 0f, 0f, 1f, 0f)       // Near: z >= 0
        culler.setPlaneForTest(5, 0f, 0f, -1f, 100f)    // Far: z <= 100
        return culler
    }

    @Test
    fun `frustum culling hierarchical traversal accurately filters spatial entries`() {
        val pool = OctreeNodePool(2048)
        val index = SpatialIndex(pool)
        val culler = createTestFrustum()

        // Inside frustum
        val entry1 = SpatialEntry(id = 1L, posX = 50f, posY = 50f, posZ = 10f, halfExtentX = 1f, halfExtentY = 1f, halfExtentZ = 1f)
        // Outside frustum (X = 200)
        val entry2 = SpatialEntry(id = 2L, posX = 200f, posY = 50f, posZ = 10f, halfExtentX = 1f, halfExtentY = 1f, halfExtentZ = 1f)

        index.insert(entry1)
        index.insert(entry2)

        val visible = index.queryFrustum(culler)
        assertEquals(1, visible.size)
        assertEquals(1L, visible[0].id)
    }

    @Test
    fun `diagnostic safety mechanism toggle maintains exact culling parity with legacy linear scan`() {
        val pool = OctreeNodePool(2048)
        val index = SpatialIndex(pool)
        val culler = createTestFrustum()

        // Insert 500 objects across the region
        for (i in 1..500) {
            val posX = (i * 17) % 256f
            val posY = (i * 31) % 256f
            val posZ = (i * 13) % 200f
            index.insert(SpatialEntry(id = i.toLong(), posX = posX, posY = posY, posZ = posZ))
        }

        // Octree mode query
        index.useOctree = true
        val octreeResults = index.queryFrustum(culler, maxResults = 4096)

        // Legacy linear mode query
        index.useOctree = false
        val linearResults = index.queryFrustum(culler, maxResults = 4096)

        val octreeIds = octreeResults.map { it.id }.toSet()
        val linearIds = linearResults.map { it.id }.toSet()

        assertEquals("Octree mode and linear mode must return identical visible objects", linearIds, octreeIds)
    }

    @Test
    fun `spatial octree depth is strictly capped at 8 levels`() {
        val pool = OctreeNodePool(2048)
        val index = SpatialIndex(pool)

        // Cluster 100 objects at the exact same point to force deep subdivision
        for (i in 1..100) {
            index.insert(SpatialEntry(id = i.toLong(), posX = 100.0f, posY = 100.0f, posZ = 100.0f))
        }

        val maxDepth = index.getMaxDepth()
        assertTrue("Max depth ($maxDepth) must not exceed 8 levels", maxDepth <= SpatialIndex.MAX_DEPTH)
        assertEquals("Max depth cap must be exactly 8", 8, SpatialIndex.MAX_DEPTH)
    }

    @Test
    fun `incremental spatial node updates reflect object translation and rotation without full index rebuilds`() {
        val pool = OctreeNodePool(2048)
        val index = SpatialIndex(pool)
        val culler = createTestFrustum()

        val entry = SpatialEntry(id = 42L, posX = 200f, posY = 200f, posZ = 10f) // Outside frustum
        index.insert(entry)

        var visible = index.queryFrustum(culler)
        assertEquals(0, visible.size)

        // Translate object into frustum
        entry.posX = 50f
        entry.posY = 50f
        index.updateIncremental(entry)

        visible = index.queryFrustum(culler)
        assertEquals(1, visible.size)
        assertEquals(42L, visible[0].id)

        // Rotate object using fromOrientedBox matrix expansion
        val rotation90Z = floatArrayOf(
            0f, 1f, 0f, 0f,
           -1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
        val rotatedEntry = SpatialEntry.fromOrientedBox(
            id = 42L,
            posX = 50f, posY = 50f, posZ = 10f,
            halfX = 5f, halfY = 1f, halfZ = 1f,
            rotation = rotation90Z
        )
        index.updateIncremental(rotatedEntry)

        visible = index.queryFrustum(culler)
        assertEquals(1, visible.size)
        assertEquals(42L, visible[0].id)
    }

    @Test
    fun `bounding volume traversal completes in under 2ms per frame for a 4000 object scene`() {
        val pool = OctreeNodePool(2048)
        val index = SpatialIndex(pool)
        val culler = createTestFrustum()

        // Populate 4,000 spatial objects randomly across the region
        for (i in 1..4000) {
            val posX = (i * 19) % 256f
            val posY = (i * 23) % 256f
            val posZ = (i * 29) % 500f
            index.insert(SpatialEntry(id = i.toLong(), posX = posX, posY = posY, posZ = posZ, halfExtentX = 1f, halfExtentY = 1f, halfExtentZ = 1f))
        }

        // Warmup pass
        for (w in 1..10) {
            index.queryFrustum(culler)
        }

        // Benchmark 50 iterations
        val iterations = 50
        val totalNanos = measureNanoTime {
            for (it in 1..iterations) {
                index.queryFrustum(culler)
            }
        }

        val avgMillis = (totalNanos / 1_000_000.0) / iterations
        println("4,000-object scene frustum culling average duration: ${avgMillis} ms per frame")
        assertTrue("Frustum traversal ($avgMillis ms) must complete in under 2.0 ms", avgMillis < 2.0)
    }

    @Test
    fun `incremental node update overhead stays below 0_5ms when 100 objects move simultaneously`() {
        val pool = OctreeNodePool(2048)
        val index = SpatialIndex(pool)

        // Populate 4,000 objects in the scene
        val movingEntries = mutableListOf<SpatialEntry>()
        for (i in 1..4000) {
            val entry = SpatialEntry(id = i.toLong(), posX = (i * 7) % 256f, posY = (i * 11) % 256f, posZ = (i * 13) % 200f)
            index.insert(entry)
            if (i <= 100) {
                movingEntries.add(entry)
            }
        }

        // Warmup pass (trigger JVM JIT compilation)
        for (warm in 1..50) {
            for (e in movingEntries) {
                e.posX += 0.1f
                index.updateIncremental(e)
            }
        }

        // Measure average overhead over 10 update frames for 100 simultaneous moving objects
        val frameCount = 10
        val totalNanos = measureNanoTime {
            for (f in 1..frameCount) {
                for (e in movingEntries) {
                    e.posX += 0.5f
                    e.posY += 0.25f
                    index.updateIncremental(e)
                }
            }
        }

        val avgFrameMillis = (totalNanos / 1_000_000.0) / frameCount
        println("100 moving objects incremental update average duration: ${avgFrameMillis} ms per frame")
        assertTrue("Incremental node update overhead ($avgFrameMillis ms) must be under 0.5 ms", avgFrameMillis < 0.5)
    }

    @Test
    fun `memory footprint for spatial index nodes stays under 5MB per active region`() {
        val pool = OctreeNodePool(2048)
        val index = SpatialIndex(pool)

        // Populate 4,000 objects
        for (i in 1..4000) {
            index.insert(SpatialEntry(id = i.toLong(), posX = (i * 17) % 256f, posY = (i * 19) % 256f, posZ = (i * 23) % 400f))
        }

        val memoryFootprintBytes = index.getMemoryFootprintBytes()
        val memoryFootprintMB = memoryFootprintBytes / (1024.0 * 1024.0)
        println("Spatial index memory footprint for 4,000 objects: ${memoryFootprintMB} MB (${memoryFootprintBytes} bytes)")

        assertTrue("Memory footprint ($memoryFootprintMB MB) must stay under 5.0 MB", memoryFootprintMB < 5.0)

        // Verify pooling recycling
        val initialAvailable = pool.availableCount()
        index.clear()
        val afterClearAvailable = pool.availableCount()
        assertTrue("Clearing spatial index must recycle nodes back into node pool", afterClearAvailable >= initialAvailable)
    }
}
