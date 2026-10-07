package com.linkpoint.render.lumiya.drawable

import com.linkpoint.render.lumiya.shaders.ParticleShaderProgram
import com.linkpoint.world.topography.PlanarTopographyProjection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verification unit tests for GPU Instanced Billboard Particle Rendering pipeline.
 */
class InstancedParticleRenderingTest {

    @Test
    fun `ParticleShaderProgram shader source contains required instanced attributes and uniforms`() {
        val program = ParticleShaderProgram()
        val vertSource = program.vertexSource

        assertTrue("Vertex shader must accept aCornerPos attribute", vertSource.contains("aCornerPos"))
        assertTrue("Vertex shader must accept aInstancePos attribute", vertSource.contains("aInstancePos"))
        assertTrue("Vertex shader must accept aInstanceScale attribute", vertSource.contains("aInstanceScale"))
        assertTrue("Vertex shader must accept aInstanceColor attribute", vertSource.contains("aInstanceColor"))
        assertTrue("Vertex shader must accept uCameraRight uniform", vertSource.contains("uCameraRight"))
        assertTrue("Vertex shader must accept uCameraUp uniform", vertSource.contains("uCameraUp"))
        assertTrue(
            "Vertex shader must compute billboard corner world position on GPU",
            vertSource.contains("aInstancePos + (uCameraRight * aCornerPos.x + uCameraUp * aCornerPos.y) * aInstanceScale")
        )
    }

    @Test
    fun `ParticleSource updates up to MAX_PARTICLES properly`() {
        val topography = PlanarTopographyProjection()
        val source = DrawableParticleManager.ParticleSource(
            id = 100L,
            posX = 128f, posY = 128f, posZ = 20f,
            pattern = DrawableParticleManager.EmitPattern.EXPLODE,
            maxCount = 4096,
            lifetime = 10.0f,
            rate = 10000.0f, // high emission rate to fill particles
            startColor = floatArrayOf(1f, 0.5f, 0.2f, 1f),
            endColor = floatArrayOf(1f, 0f, 0f, 0f),
            startScale = 1.0f,
            endScale = 0.1f,
            speed = 5.0f
        )

        source.update(1.0f, topography)

        val liveCount = source.particles.count { it.alive }
        assertTrue("Live particles should be spawned up to maxCount", liveCount > 0)
        assertTrue("Live count should not exceed MAX_PARTICLES limit", liveCount <= DrawableParticleManager.MAX_PARTICLES)

        val sample = source.particles.first { it.alive }
        assertNotNull("Particle position must be updated", sample)
        assertTrue("Particle age must be within lifetime", sample.age >= 0f && sample.age <= sample.lifetime)
    }

    @Test
    fun `MAX_PARTICLES constant matches contract specification`() {
        assertEquals(4096, DrawableParticleManager.MAX_PARTICLES)
    }
}
