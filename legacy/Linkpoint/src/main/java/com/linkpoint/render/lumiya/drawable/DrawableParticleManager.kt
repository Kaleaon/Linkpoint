package com.linkpoint.render.lumiya.drawable

import android.opengl.GLES32
import android.opengl.Matrix
import com.linkpoint.render.lumiya.core.LumiyaRenderContext
import com.linkpoint.render.lumiya.glres.GLBufferManager
import com.linkpoint.world.topography.PlanarTopographyProjection
import com.linkpoint.world.topography.WorldTopographyProjection
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * SL-compatible particle system with camera-aligned billboards.
 *
 * Design lineage: Lumiya particle rendering, modernised with GL ES 3.2
 * VAO + dynamic VBO updates.
 *
 * Supports the SL particle source parameters:
 *  - Drop, explode, angle-cone emission patterns
 *  - Wind, bounce, follow-source flags
 *  - Colour / scale interpolation over lifetime
 *  - Per-source max particle count
 */
class DrawableParticleManager(
    private val ctx: LumiyaRenderContext,
    var topographyProjection: WorldTopographyProjection = PlanarTopographyProjection()
) {

    companion object {
        /** Maximum live particles across all sources. */
        const val MAX_PARTICLES = 4096

        /** Floats per particle instance: pos(3) + scale(1) + color(4) = 8 */
        private const val FLOATS_PER_INSTANCE = 8

        /** Bytes per particle instance: 8 floats * 4 bytes = 32 bytes */
        private const val BYTES_PER_INSTANCE = FLOATS_PER_INSTANCE * 4

        /** 4 vertices per unit quad, 6 indices per unit quad */
        private const val VERTS_PER_QUAD = 4
        private const val INDICES_PER_QUAD = 6
    }

    private val sources = ConcurrentHashMap<Long, ParticleSource>()
    private var vao = 0
    private var staticQuadVbo = 0
    private var staticEbo = 0
    private val instanceVbos = IntArray(2)
    private var currentBufferIndex = 0
    private var instanceByteBuffer: ByteBuffer? = null
    private var liveCount = 0

    /** Identity model matrix (particles are already in world space). */
    private val identityMatrix = FloatArray(16).also { Matrix.setIdentityM(it, 0) }

    init {
        allocateBuffers()
    }

    // ── Source management ────────────────────────────────────────────────

    fun addSource(
        sourceId: Long,
        posX: Float, posY: Float, posZ: Float,
        pattern: EmitPattern = EmitPattern.EXPLODE,
        maxCount: Int = 50,
        lifetime: Float = 5.0f,
        rate: Float = 10.0f,
        startColorR: Float = 1f, startColorG: Float = 1f, startColorB: Float = 1f, startColorA: Float = 1f,
        endColorR: Float = 1f, endColorG: Float = 1f, endColorB: Float = 1f, endColorA: Float = 0f,
        startScale: Float = 0.2f,
        endScale: Float = 0.05f,
        speed: Float = 1.0f
    ) {
        sources[sourceId] = ParticleSource(
            id = sourceId,
            posX = posX, posY = posY, posZ = posZ,
            pattern = pattern, maxCount = maxCount,
            lifetime = lifetime, rate = rate,
            startColor = floatArrayOf(startColorR, startColorG, startColorB, startColorA),
            endColor = floatArrayOf(endColorR, endColorG, endColorB, endColorA),
            startScale = startScale, endScale = endScale,
            speed = speed
        )
    }

    fun removeSource(sourceId: Long) {
        sources.remove(sourceId)
    }

    // ── Update + Draw ────────────────────────────────────────────────────

    fun draw(ctx: LumiyaRenderContext) {
        if (sources.isEmpty()) return

        val dt = ctx.deltaTime
        liveCount = 0

        // Camera right/up vectors for billboarding
        val right = floatArrayOf(ctx.viewMatrix[0], ctx.viewMatrix[4], ctx.viewMatrix[8])
        val up = floatArrayOf(ctx.viewMatrix[1], ctx.viewMatrix[5], ctx.viewMatrix[9])

        val fb = instanceByteBuffer?.asFloatBuffer() ?: return
        fb.clear()

        for (source in sources.values) {
            source.update(dt, topographyProjection)
            for (particle in source.particles) {
                if (!particle.alive || liveCount >= MAX_PARTICLES) continue
                val t = particle.age / particle.lifetime
                val scale = source.startScale + (source.endScale - source.startScale) * t
                val cr = source.startColor[0] + (source.endColor[0] - source.startColor[0]) * t
                val cg = source.startColor[1] + (source.endColor[1] - source.startColor[1]) * t
                val cb = source.startColor[2] + (source.endColor[2] - source.startColor[2]) * t
                val ca = source.startColor[3] + (source.endColor[3] - source.startColor[3]) * t

                // Instance payload: pos(3), scale(1), color(4)
                fb.put(particle.posX)
                fb.put(particle.posY)
                fb.put(particle.posZ)
                fb.put(scale)
                fb.put(cr)
                fb.put(cg)
                fb.put(cb)
                fb.put(ca)

                liveCount++
            }
        }

        if (liveCount == 0) return

        fb.flip()

        // Double-buffered stream upload with buffer orphaning
        currentBufferIndex = (currentBufferIndex + 1) % 2
        val activeInstanceVbo = instanceVbos[currentBufferIndex]

        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, activeInstanceVbo)
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, MAX_PARTICLES * BYTES_PER_INSTANCE, null, GLES32.GL_DYNAMIC_DRAW)
        GLES32.glBufferSubData(GLES32.GL_ARRAY_BUFFER, 0, liveCount * BYTES_PER_INSTANCE, fb)

        val program = ctx.particleProgram ?: return
        program.use()
        program.setModelMatrix(identityMatrix)
        program.setCameraRight(right)
        program.setCameraUp(up)
        program.setUseTexture(false)

        GLES32.glDepthMask(false)
        GLES32.glBindVertexArray(vao)

        // Bind active instance VBO and set per-instance attributes
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, activeInstanceVbo)

        // aInstancePos (location = 2, vec3)
        GLES32.glEnableVertexAttribArray(2)
        GLES32.glVertexAttribPointer(2, 3, GLES32.GL_FLOAT, false, BYTES_PER_INSTANCE, 0)
        GLES32.glVertexAttribDivisor(2, 1)

        // aInstanceScale (location = 3, float)
        GLES32.glEnableVertexAttribArray(3)
        GLES32.glVertexAttribPointer(3, 1, GLES32.GL_FLOAT, false, BYTES_PER_INSTANCE, 12)
        GLES32.glVertexAttribDivisor(3, 1)

        // aInstanceColor (location = 4, vec4)
        GLES32.glEnableVertexAttribArray(4)
        GLES32.glVertexAttribPointer(4, 4, GLES32.GL_FLOAT, false, BYTES_PER_INSTANCE, 16)
        GLES32.glVertexAttribDivisor(4, 1)

        GLES32.glDrawElementsInstanced(GLES32.GL_TRIANGLES, INDICES_PER_QUAD, GLES32.GL_UNSIGNED_SHORT, 0, liveCount)

        GLES32.glBindVertexArray(0)
        GLES32.glDepthMask(true)
    }

    fun destroy() {
        if (vao != 0) {
            GLES32.glDeleteVertexArrays(1, intArrayOf(vao), 0)
            vao = 0
        }
        if (staticQuadVbo != 0) {
            GLES32.glDeleteBuffers(1, intArrayOf(staticQuadVbo), 0)
            staticQuadVbo = 0
        }
        if (staticEbo != 0) {
            GLES32.glDeleteBuffers(1, intArrayOf(staticEbo), 0)
            staticEbo = 0
        }
        if (instanceVbos[0] != 0) {
            GLES32.glDeleteBuffers(2, instanceVbos, 0)
            instanceVbos[0] = 0
            instanceVbos[1] = 0
        }
        sources.clear()
    }

    // ── Internals ────────────────────────────────────────────────────────

    private fun allocateBuffers() {
        val maxInstanceBytes = MAX_PARTICLES * BYTES_PER_INSTANCE

        instanceByteBuffer = ByteBuffer.allocateDirect(maxInstanceBytes)
            .order(ByteOrder.nativeOrder())

        // Unit quad geometry (cornerPos: vec2, texCoord: vec2)
        val quadVertices = floatArrayOf(
            -0.5f, -0.5f, 0.0f, 0.0f, // BL
             0.5f, -0.5f, 1.0f, 0.0f, // BR
             0.5f,  0.5f, 1.0f, 1.0f, // TR
            -0.5f,  0.5f, 0.0f, 1.0f  // TL
        )
        val quadBuffer = ByteBuffer.allocateDirect(quadVertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(quadVertices)
        quadBuffer.flip()

        val quadIndices = shortArrayOf(0, 1, 2, 0, 2, 3)
        val quadIndexBuffer = ByteBuffer.allocateDirect(quadIndices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(quadIndices)
        quadIndexBuffer.flip()

        val vaoBuf = IntArray(1); GLES32.glGenVertexArrays(1, vaoBuf, 0); vao = vaoBuf[0]
        val staticVboBuf = IntArray(1); GLES32.glGenBuffers(1, staticVboBuf, 0); staticQuadVbo = staticVboBuf[0]
        val eboBuf = IntArray(1); GLES32.glGenBuffers(1, eboBuf, 0); staticEbo = eboBuf[0]
        GLES32.glGenBuffers(2, instanceVbos, 0)

        GLES32.glBindVertexArray(vao)

        // Static quad VBO
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, staticQuadVbo)
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, quadVertices.size * 4, quadBuffer, GLES32.GL_STATIC_DRAW)

        // aCornerPos (location = 0, vec2)
        GLES32.glEnableVertexAttribArray(0)
        GLES32.glVertexAttribPointer(0, 2, GLES32.GL_FLOAT, false, 16, 0)
        GLES32.glVertexAttribDivisor(0, 0)

        // aTexCoord (location = 1, vec2)
        GLES32.glEnableVertexAttribArray(1)
        GLES32.glVertexAttribPointer(1, 2, GLES32.GL_FLOAT, false, 16, 8)
        GLES32.glVertexAttribDivisor(1, 0)

        // Static EBO
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, staticEbo)
        GLES32.glBufferData(GLES32.GL_ELEMENT_ARRAY_BUFFER, quadIndices.size * 2, quadIndexBuffer, GLES32.GL_STATIC_DRAW)

        // Initialize instance VBOs
        for (i in 0..1) {
            GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, instanceVbos[i])
            GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, maxInstanceBytes, null, GLES32.GL_DYNAMIC_DRAW)
        }

        GLES32.glBindVertexArray(0)
    }

    // ── Inner classes ────────────────────────────────────────────────────

    enum class EmitPattern { DROP, EXPLODE, ANGLE_CONE }

    data class Particle(
        var alive: Boolean = false,
        var posX: Float = 0f, var posY: Float = 0f, var posZ: Float = 0f,
        var velX: Float = 0f, var velY: Float = 0f, var velZ: Float = 0f,
        var age: Float = 0f,
        var lifetime: Float = 5f
    )

    class ParticleSource(
        val id: Long,
        var posX: Float, var posY: Float, var posZ: Float,
        val pattern: EmitPattern,
        val maxCount: Int,
        val lifetime: Float,
        val rate: Float,
        val startColor: FloatArray,
        val endColor: FloatArray,
        val startScale: Float,
        val endScale: Float,
        val speed: Float
    ) {
        val particles = Array(maxCount) { Particle() }
        private var emitAccumulator = 0f
        private val rng = java.util.Random()

        fun update(dt: Float, topography: WorldTopographyProjection = PlanarTopographyProjection()) {
            // Update existing
            for (p in particles) {
                if (!p.alive) continue
                p.age += dt
                if (p.age >= p.lifetime) { p.alive = false; continue }
                val gVec = topography.getGravityVector(p.posX, p.posY, p.posZ, 0.5f)
                p.posX += p.velX * dt + gVec[0] * dt
                p.posY += p.velY * dt + gVec[1] * dt
                p.posZ += p.velZ * dt + gVec[2] * dt
            }

            // Emit new
            emitAccumulator += dt * rate
            while (emitAccumulator >= 1.0f) {
                emitAccumulator -= 1.0f
                emit()
            }
        }

        private fun emit() {
            val slot = particles.firstOrNull { !it.alive } ?: return
            slot.alive = true
            slot.posX = posX; slot.posY = posY; slot.posZ = posZ
            slot.age = 0f
            slot.lifetime = lifetime

            when (pattern) {
                EmitPattern.DROP -> {
                    slot.velX = 0f; slot.velY = 0f; slot.velZ = 0f
                }
                EmitPattern.EXPLODE -> {
                    val theta = rng.nextFloat() * Math.PI.toFloat() * 2f
                    val phi = rng.nextFloat() * Math.PI.toFloat()
                    slot.velX = sin(phi) * cos(theta) * speed
                    slot.velY = sin(phi) * sin(theta) * speed
                    slot.velZ = cos(phi) * speed
                }
                EmitPattern.ANGLE_CONE -> {
                    val theta = rng.nextFloat() * Math.PI.toFloat() * 2f
                    val spread = rng.nextFloat() * 0.5f
                    slot.velX = sin(spread) * cos(theta) * speed
                    slot.velY = sin(spread) * sin(theta) * speed
                    slot.velZ = cos(spread) * speed
                }
            }
        }
    }
}
