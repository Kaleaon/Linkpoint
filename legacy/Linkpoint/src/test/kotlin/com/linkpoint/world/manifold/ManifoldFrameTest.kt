package com.linkpoint.world.manifold

import com.linkpoint.protocol.llsd.*
import org.junit.Assert.*
import org.junit.Test

class ManifoldFrameTest {

    @Test
    fun `default ManifoldFrame is FLAT_2D identity`() {
        val frame = ManifoldFrame.IDENTITY
        assertEquals(ManifoldFrame.DEFAULT_FRAME_ID, frame.frameId)
        assertEquals(TopologyType.FLAT_2D, frame.topologyType)
        assertEquals(0.0, frame.radius, 0.001)
        assertEquals(0.0, frame.curvature, 0.001)
    }

    @Test
    fun `toLLSD and fromLLSD roundtrip`() {
        val original = ManifoldFrame(
            frameId = "ringworld-01",
            topologyType = TopologyType.RINGWORLD_CYLINDER,
            radius = 100000.0,
            curvature = 0.00001,
            originX = 100.0,
            originY = 200.0,
            originZ = 50.0,
            sizeX = 262144.0,
            sizeY = 256.0
        )

        val llsdMap = original.toLLSD()
        val parsed = ManifoldFrame.fromLLSD(llsdMap)

        assertEquals("ringworld-01", parsed.frameId)
        assertEquals(TopologyType.RINGWORLD_CYLINDER, parsed.topologyType)
        assertEquals(100000.0, parsed.radius, 0.001)
        assertEquals(0.00001, parsed.curvature, 0.000001)
        assertEquals(100.0, parsed.originX, 0.001)
        assertEquals(200.0, parsed.originY, 0.001)
        assertEquals(50.0, parsed.originZ, 0.001)
        assertEquals(262144.0, parsed.sizeX, 0.001)
        assertEquals(256.0, parsed.sizeY, 0.001)
    }

    @Test
    fun `fromLLSD handles null or malformed data safely`() {
        val fallback = ManifoldFrame.fromLLSD(null)
        assertEquals(ManifoldFrame.IDENTITY, fallback)

        val emptyMap = ManifoldFrame.fromLLSD(LLSDMap())
        assertEquals(TopologyType.FLAT_2D, emptyMap.topologyType)
    }
}
