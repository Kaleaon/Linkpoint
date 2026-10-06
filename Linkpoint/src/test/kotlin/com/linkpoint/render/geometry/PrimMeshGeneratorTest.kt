package com.linkpoint.render.geometry

import com.linkpoint.protocol.messages.PrimShapeParams
import org.junit.Assert.*
import org.junit.Test

class PrimMeshGeneratorTest {

    @Test
    fun testStandardShapesGeneration() {
        val shapes = PrimShape.values()
        for (shape in shapes) {
            val key = PrimShapeKey.defaultFor(shape)
            val mesh = PrimMeshGenerator.generateMesh(key)

            assertTrue("Shape $shape should produce non-empty positions", mesh.positions.isNotEmpty())
            assertTrue("Shape $shape should produce non-empty normals", mesh.normals.isNotEmpty())
            assertTrue("Shape $shape should produce non-empty uvs", mesh.uvs.isNotEmpty())
            assertTrue("Shape $shape should produce non-empty indices", mesh.indices.isNotEmpty())

            // POS3 (3) + NORMAL3 (3) + UV2 (2) = 8 floats per vertex
            val vertexCount = mesh.positions.size / 3
            assertEquals("Normals count mismatch for $shape", vertexCount * 3, mesh.normals.size)
            assertEquals("UVs count mismatch for $shape", vertexCount * 2, mesh.uvs.size)

            val interleaved = mesh.toInterleavedBuffer()
            assertEquals("Interleaved size mismatch for $shape", vertexCount * 8, interleaved.size)

            // Verify no NaN or Infinite values
            for (i in interleaved.indices) {
                assertFalse("Interleaved float at index $i is NaN or Infinite", interleaved[i].isNaN() || interleaved[i].isInfinite())
            }

            // Verify all indices are in range
            for (index in mesh.indices) {
                val idx = index.toInt() and 0xFFFF
                assertTrue("Index $idx out of bounds for vertex count $vertexCount in $shape", idx in 0 until vertexCount)
            }
        }
    }

    @Test
    fun testExtrudedProfileCutsAndHollow() {
        // Standard box
        val defaultBoxKey = PrimShapeKey.defaultFor(PrimShape.BOX)
        val defaultBoxMesh = PrimMeshGenerator.generateMesh(defaultBoxKey)

        // Box with profile cuts
        val cutParams = PrimShapeParams(
            profileBegin = 0.25f,
            profileEnd = 0.75f
        )
        val cutBoxKey = PrimShapeKey(PrimShape.BOX, cutParams)
        val cutBoxMesh = PrimMeshGenerator.generateMesh(cutBoxKey)

        assertTrue("Cut profile should generate vertices", cutBoxMesh.positions.isNotEmpty())

        // Box with hollow
        val hollowParams = PrimShapeParams(
            profileHollow = 0.3f
        )
        val hollowBoxKey = PrimShapeKey(PrimShape.BOX, hollowParams)
        val hollowBoxMesh = PrimMeshGenerator.generateMesh(hollowBoxKey)

        assertTrue("Hollow profile should generate vertices", hollowBoxMesh.positions.isNotEmpty())
        assertTrue("Hollow profile should have more indices than solid cut", hollowBoxMesh.indices.size > defaultBoxMesh.indices.size)
    }

    @Test
    fun testExtrudedProfileTaperAndTwist() {
        val twistTaperParams = PrimShapeParams(
            pathTaperX = 0.5f,
            pathTaperY = 0.5f,
            pathTwist = 0.5f,
            pathTwistBegin = 0.0f
        )
        val key = PrimShapeKey(PrimShape.CYLINDER, twistTaperParams)
        val mesh = PrimMeshGenerator.generateMesh(key)

        assertTrue("Taper + twist cylinder should generate positions", mesh.positions.isNotEmpty())
        assertTrue("Taper + twist cylinder should generate indices", mesh.indices.isNotEmpty())

        // Verify top ring vertices are tapered (scaled smaller on XY)
        val vertexCount = mesh.positions.size / 3
        var maxZ = -Float.MAX_VALUE
        var topX = 0f
        for (i in 0 until vertexCount) {
            val z = mesh.positions[i * 3 + 2]
            if (z > maxZ) {
                maxZ = z
                topX = Math.abs(mesh.positions[i * 3])
            }
        }
        assertTrue("Top ring X position should be tapered (< 0.5)", topX < 0.49f)
    }

    @Test
    fun testSculptMeshGenerationTopologies() {
        // Build 16x16 dummy RGB pixel grid
        val width = 16
        val height = 16
        val rgbPixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val r = (x * 255 / width) and 0xFF
                val g = (y * 255 / height) and 0xFF
                val b = 128
                rgbPixels[y * width + x] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
            }
        }

        // Test topologies 1..4 (1: Sphere, 2: Torus, 3: Plane, 4: Cylinder)
        for (topo in 1..4) {
            val mesh = PrimMeshGenerator.generateSculptMesh(width, height, rgbPixels, topo)

            assertTrue("Sculpt topology $topo should generate positions", mesh.positions.isNotEmpty())
            assertTrue("Sculpt topology $topo should generate normals", mesh.normals.isNotEmpty())
            assertTrue("Sculpt topology $topo should generate uvs", mesh.uvs.isNotEmpty())
            assertTrue("Sculpt topology $topo should generate indices", mesh.indices.isNotEmpty())

            val vertexCount = mesh.positions.size / 3
            assertEquals("Sculpt topology $topo vertex count mismatch with normals", vertexCount * 3, mesh.normals.size)
            assertEquals("Sculpt topology $topo vertex count mismatch with uvs", vertexCount * 2, mesh.uvs.size)

            val interleaved = mesh.toInterleavedBuffer()
            assertEquals("Sculpt topology $topo interleaved size mismatch", vertexCount * 8, interleaved.size)

            // Ensure valid float range
            for (i in interleaved.indices) {
                assertFalse("Sculpt topology $topo float at $i is NaN or Infinite", interleaved[i].isNaN() || interleaved[i].isInfinite())
            }

            // Ensure valid index bounds
            for (index in mesh.indices) {
                val idx = index.toInt() and 0xFFFF
                assertTrue("Sculpt topology $topo index $idx out of bounds", idx in 0 until vertexCount)
            }
        }
    }

    @Test
    fun testInterleavedBufferFormatParity() {
        val key = PrimShapeKey.defaultFor(PrimShape.TORUS)
        val mesh = PrimMeshGenerator.generateMesh(key)
        val interleaved = mesh.toInterleavedBuffer()

        // 8 floats per vertex = POS3 (0..2), NORMAL3 (3..5), UV2 (6..7)
        val vertexCount = mesh.positions.size / 3
        assertEquals("Interleaved buffer length must be 8 floats per vertex", vertexCount * 8, interleaved.size)

        for (v in 0 until vertexCount) {
            val px = interleaved[v * 8 + 0]
            val py = interleaved[v * 8 + 1]
            val pz = interleaved[v * 8 + 2]

            val nx = interleaved[v * 8 + 3]
            val ny = interleaved[v * 8 + 4]
            val nz = interleaved[v * 8 + 5]

            val u = interleaved[v * 8 + 6]
            val vUv = interleaved[v * 8 + 7]

            assertEquals("Position X mismatch at vertex $v", mesh.positions[v * 3 + 0], px, 1e-5f)
            assertEquals("Position Y mismatch at vertex $v", mesh.positions[v * 3 + 1], py, 1e-5f)
            assertEquals("Position Z mismatch at vertex $v", mesh.positions[v * 3 + 2], pz, 1e-5f)

            assertEquals("Normal X mismatch at vertex $v", mesh.normals[v * 3 + 0], nx, 1e-5f)
            assertEquals("Normal Y mismatch at vertex $v", mesh.normals[v * 3 + 1], ny, 1e-5f)
            assertEquals("Normal Z mismatch at vertex $v", mesh.normals[v * 3 + 2], nz, 1e-5f)

            assertEquals("UV U mismatch at vertex $v", mesh.uvs[v * 2 + 0], u, 1e-5f)
            assertEquals("UV V mismatch at vertex $v", mesh.uvs[v * 2 + 1], vUv, 1e-5f)
        }
    }
}
