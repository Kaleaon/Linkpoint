package com.linkpoint.protocol.gatekeeper

import com.linkpoint.world.manifold.TopologyType
import org.junit.Assert.*
import org.junit.Test

class GatekeeperUriParserTest {

    @Test
    fun `parses hypergrid URI with manifold_frame_id query string`() {
        val uri = "http://hg.osgrid.org:8002/gatekeeper?manifold_frame_id=ringworld-alpha&topology_type=RINGWORLD_CYLINDER&radius=100000.0&curvature=0.00001&size_x=262144&size_y=256"

        val target = GatekeeperUriParser.parse(uri)
        assertNotNull(target)
        assertEquals("http://hg.osgrid.org:8002", target!!.gridUri)
        assertEquals("http://hg.osgrid.org:8002/gatekeeper", target.gatekeeperUrl)

        val frame = target.manifoldFrame
        assertEquals("ringworld-alpha", frame.frameId)
        assertEquals(TopologyType.RINGWORLD_CYLINDER, frame.topologyType)
        assertEquals(100000.0, frame.radius, 0.001)
        assertEquals(0.00001, frame.curvature, 0.000001)
        assertEquals(262144.0, frame.sizeX, 0.001)
        assertEquals(256.0, frame.sizeY, 0.001)
    }

    @Test
    fun `parses colon notation hypergrid URI with manifold frame query parameters`() {
        val uri = "hg.osgrid.org:8002:RegionAlpha/128/128/30?manifold_frame_id=sphere-world&topology_type=SPHERE_3D&radius=6371000.0"

        val target = GatekeeperUriParser.parse(uri)
        assertNotNull(target)
        assertEquals("http://hg.osgrid.org:8002", target!!.gridUri)
        assertEquals("RegionAlpha", target.regionName)
        assertEquals(128, target.x)
        assertEquals(128, target.y)
        assertEquals(30, target.z)

        val frame = target.manifoldFrame
        assertEquals("sphere-world", frame.frameId)
        assertEquals(TopologyType.SPHERE_3D, frame.topologyType)
        assertEquals(6371000.0, frame.radius, 0.001)
    }

    @Test
    fun `returns default FLAT_2D frame when query parameters are absent`() {
        val uri = "http://hg.osgrid.org:8002/RegionBeta/64/64/25"

        val target = GatekeeperUriParser.parse(uri)
        assertNotNull(target)
        assertEquals(TopologyType.FLAT_2D, target!!.manifoldFrame.topologyType)
        assertEquals("flat-2d", target.manifoldFrame.frameId)
    }
}
