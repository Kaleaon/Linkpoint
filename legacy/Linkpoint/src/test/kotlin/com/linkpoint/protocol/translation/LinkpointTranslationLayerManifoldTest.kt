package com.linkpoint.protocol.translation

import com.linkpoint.world.manifold.ManifoldFrame
import com.linkpoint.world.manifold.TopologyType
import org.junit.Assert.*
import org.junit.Test

class LinkpointTranslationLayerManifoldTest {

    @Test
    fun `detectManifoldCapabilities falls back to FLAT_2D identity when capabilities missing`() {
        val frame = LinkpointTranslationLayer.detectManifoldCapabilities(null, null)
        assertEquals(ManifoldFrame.IDENTITY, frame)
        assertEquals(TopologyType.FLAT_2D, frame.topologyType)
    }

    @Test
    fun `detectManifoldCapabilities parses manifold_frame LLSD block`() {
        val manifoldObj = ManifoldFrame(
            frameId = "cylinder-grid-01",
            topologyType = TopologyType.RINGWORLD_CYLINDER,
            radius = 50000.0
        ).toLLSD()

        val caps = mapOf("manifold_frame" to manifoldObj)
        val frame = LinkpointTranslationLayer.detectManifoldCapabilities(capabilities = caps)

        assertEquals("cylinder-grid-01", frame.frameId)
        assertEquals(TopologyType.RINGWORLD_CYLINDER, frame.topologyType)
        assertEquals(50000.0, frame.radius, 0.001)
    }

    @Test
    fun `transformCoordinate executes under 1ms with identity transform for FLAT_2D`() {
        // Warm up classloader / JIT
        LinkpointTranslationLayer.transformCoordinate(0.0, 0.0, 0.0, ManifoldFrame.IDENTITY)

        val startTime = System.nanoTime()
        val (x, y, z) = LinkpointTranslationLayer.transformCoordinate(128.0, 256.0, 50.0, ManifoldFrame.IDENTITY)
        val elapsedUs = (System.nanoTime() - startTime) / 1000

        assertTrue("Identity transform must execute under 1ms (took ${elapsedUs}us)", elapsedUs < 1000)
        assertEquals(128.0, x, 0.001)
        assertEquals(256.0, y, 0.001)
        assertEquals(50.0, z, 0.001)
    }
}
