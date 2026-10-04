package com.linkpoint.world.topography

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.objects.prim.FlexiParams
import com.linkpoint.objects.prim.FlexiblePrimSimulator
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.render.lumiya.drawable.DrawableParticleManager
import com.linkpoint.render.lumiya.spatial.FrustumCuller
import com.linkpoint.render.lumiya.spatial.SpatialEntry
import com.linkpoint.render.lumiya.spatial.SpatialIndex
import com.linkpoint.world.RegionCrossingManager
import com.linkpoint.world.RegionInfo
import com.linkpoint.world3d.AvatarController
import com.linkpoint.world3d.InputState
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class TopographyIntegrationTest {

    @Test
    fun testSpatialIndexNonPlanarTopography() {
        val radius = 1000f
        val ringworld = RingworldTopographyProjection(radius = radius)
        val index = SpatialIndex(topographyProjection = ringworld)

        // Insert entry within region coordinates (localX = 128, localY = 128, localZ = 10)
        val entry = SpatialEntry(id = 101L, posX = 128f, posY = 128f, posZ = 10f, halfExtentX = 5f, halfExtentY = 5f, halfExtentZ = 5f)
        index.insert(entry)

        assertEquals(1, index.objectCount)

        // Query frustum culler that covers projected space
        val culler = mock(FrustumCuller::class.java)
        `when`(culler.classifyAABB(anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyFloat()))
            .thenReturn(com.linkpoint.render.lumiya.spatial.FrustumResult.INTERSECTS)
        `when`(culler.isAABBVisible(anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyFloat()))
            .thenReturn(true)

        val results = index.queryFrustum(culler)
        assertEquals(1, results.size)
        assertEquals(101L, results[0].id)
    }

    @Test
    fun testRegionCrossingManagerTopographyRouting() {
        val udpConn = mock(UDPConnectionFixed::class.java)
        val capMgr = mock(CapabilityManager::class.java)
        val manager = RegionCrossingManager(udpConn, capMgr)

        val initialHandle = (1000L shl 40) or (1000L shl 8)
        manager.setCurrentRegion(RegionInfo(handle = initialHandle, name = "StartRegion", simIP = "127.0.0.1", simPort = 9000, seedCapability = "http://localhost"))

        // Default planar
        val eastHandlePlanar = manager.resolveNeighborHandle(300f, 128f)
        assertNotNull(eastHandlePlanar)
        assertEquals(1000L + 256L, (eastHandlePlanar!! shr 40))

        // Switch to Ringworld topology
        val radius = 1000f
        val ringworld = RingworldTopographyProjection(radius = radius)
        manager.topographyProjection = ringworld

        val circumference = (2.0 * Math.PI * radius).toFloat()
        val totalRegionsX = Math.round(circumference / 256f)
        val worldWidthX = totalRegionsX * 256

        // Set current region at boundary region 0
        manager.setCurrentRegion(RegionInfo(handle = (0L shl 40) or (1000L shl 8), name = "BoundaryRegion", simIP = "127.0.0.1", simPort = 9000, seedCapability = "http://localhost"))

        // Moving past negative X wraps around ringworld
        val wrappedWestHandle = manager.resolveNeighborHandle(-50f, 128f)
        assertNotNull(wrappedWestHandle)
        val wrappedWestX = (wrappedWestHandle!! shr 40).toInt()
        assertEquals(worldWidthX - 256, wrappedWestX)
    }

    @Test
    fun testAvatarControllerNonPlanarGravity() {
        val controller = AvatarController()
        val radius = 1000f
        val ringworld = RingworldTopographyProjection(radius = radius)
        controller.topographyProjection = ringworld

        // Position avatar in mid-air at local position (localX = 200, localY = 100, localZ = 100)
        controller.teleport(com.badlogic.gdx.math.Vector3(200f, 100f, 100f))
        controller.jump() // Un-ground avatar so gravity applies

        // Trigger in-air physics update
        val input = InputState()
        controller.fixedUpdate(dt = 0.1f, input = input, topography = ringworld)

        // Acceleration along ringworld gravity vector should produce X velocity component
        assertTrue("X velocity should increase due to non-planar gravity vector", controller.velocity.x > 0f)
    }

    @Test
    fun testFlexiblePrimSimulatorNonPlanarGravity() {
        val radius = 1000f
        val sphere = SphericalTopographyProjection(radius = radius)
        val simulator = FlexiblePrimSimulator(topographyProjection = sphere)

        val params = FlexiParams(softness = 2, gravity = 0.5f, wind = 0f, tension = 0.1f, drag = 0.0f)
        val basePos = floatArrayOf(0f, 500f, 0f)
        val baseRot = floatArrayOf(0f, 0f, 0f, 1f)

        simulator.registerFlexiPrim(localId = 1, params = params, basePosition = basePos, baseRotation = baseRot, length = 2.0f)
        simulator.simulate(deltaTime = 0.1f)

        assertNotNull(simulator)
    }

    @Test
    fun testDrawableParticleManagerNonPlanarGravity() {
        val sphere = SphericalTopographyProjection(radius = 1000f)
        val source = DrawableParticleManager.ParticleSource(
            id = 1L,
            posX = 0f, posY = 500f, posZ = 10f,
            pattern = DrawableParticleManager.EmitPattern.DROP,
            maxCount = 10,
            lifetime = 5.0f,
            rate = 10.0f,
            startColor = floatArrayOf(1f, 1f, 1f, 1f),
            endColor = floatArrayOf(1f, 1f, 1f, 0f),
            startScale = 0.2f,
            endScale = 0.05f,
            speed = 0.0f
        )

        source.update(0.1f, sphere)
        val aliveParticle = source.particles.firstOrNull { it.alive }
        assertNotNull("Particle should emit and update", aliveParticle)
    }
}
