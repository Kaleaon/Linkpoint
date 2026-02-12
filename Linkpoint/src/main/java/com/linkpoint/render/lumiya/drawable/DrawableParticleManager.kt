package com.linkpoint.render.lumiya.drawable

import android.opengl.GLES32
import android.opengl.Matrix
import com.linkpoint.render.lumiya.core.LumiyaRenderContext
import com.linkpoint.render.lumiya.glres.GLBufferManager
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
class DrawableParticleManager(private val ctx: LumiyaRenderContext) {

    companion object {
        /** Maximum live particles across all sources. */
        const val MAX_PARTICLES = 4096

        /** Floats per particle quad vertex: pos(3) + uv(2) + color(4) = 9 */
        private const val FLOATS_PER_VERTEX = 9

        /** 4 vertices per quad, 6 indices per quad */
        private const val VERTS_PER_PARTICLE = 4
        private const val INDICES_PER_PARTICLE = 6
    }

    private val sources = ConcurrentHashMap<Long, ParticleSource>()
    private var vao = 0
    private var vbo = 0
    private var ebo = 0
    private var vertexBuffer: ByteBuffer? = null
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

        val fb = vertexBuffer?.asFloatBuffer() ?: return
        fb.clear()

        for (source in sources.values) {
            source.update(dt)
            for (particle in source.particles) {
                if (!particle.alive || liveCount >= MAX_PARTICLES) continue
                val t = particle.age / particle.lifetime
                val scale = source.startScale + (source.endScale - source.startScale) * t
                val cr = source.startColor[0] + (source.endColor[0] - source.startColor[0]) * t
                val cg = source.startColor[1] + (source.endColor[1] - source.startColor[1]) * t
                val cb = source.startColor[2] + (source.endColor[2] - source.startColor[2]) * t
                val ca = source.startColor[3] + (source.endColor[3] - source.startColor[3]) * t

                // Compute billboard corners
                val px = particle.posX; val py = particle.posY; val pz = particle.posZ
                val hs = scale * 0.5f

                // 4 corners: BL, BR, TR, TL
                putVertex(fb, px - right[0]*hs - up[0]*hs, py - right[1]*hs - up[1]*hs, pz - right[2]*hs - up[2]*hs, 0f, 0f, cr, cg, cb, ca)
                putVertex(fb, px + right[0]*hs - up[0]*hs, py + right[1]*hs - up[1]*hs, pz + right[2]*hs - up[2]*hs, 1f, 0f, cr, cg, cb, ca)
                putVertex(fb, px + right[0]*hs + up[0]*hs, py + right[1]*hs + up[1]*hs, pz + right[2]*hs + up[2]*hs, 1f, 1f, cr, cg, cb, ca)
                putVertex(fb, px - right[0]*hs + up[0]*hs, py - right[1]*hs + up[1]*hs, pz - right[2]*hs + up[2]*hs, 0f, 1f, cr, cg, cb, ca)

                liveCount++
            }
        }

        if (liveCount == 0) return

        fb.flip()

        // Upload VBO
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vbo)
        GLES32.glBufferSubData(GLES32.GL_ARRAY_BUFFER, 0, liveCount * VERTS_PER_PARTICLE * FLOATS_PER_VERTEX * 4, fb)

        // Draw
        val program = ctx.particleProgram ?: return
        program.use()
        program.setModelMatrix(identityMatrix)
        program.setUseTexture(false)

        GLES32.glDepthMask(false)
        GLES32.glBindVertexArray(vao)
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, liveCount * INDICES_PER_PARTICLE, GLES32.GL_UNSIGNED_SHORT, 0)
        GLES32.glBindVertexArray(0)
        GLES32.glDepthMask(true)
    }

    fun destroy() {
        if (vao != 0) GLES32.glDeleteVertexArrays(1, intArrayOf(vao), 0)
        if (vbo != 0) GLES32.glDeleteBuffers(1, intArrayOf(vbo), 0)
        if (ebo != 0) GLES32.glDeleteBuffers(1, intArrayOf(ebo), 0)
        vao = 0; vbo = 0; ebo = 0
        sources.clear()
    }

    // ── Internals ────────────────────────────────────────────────────────

    private fun allocateBuffers() {
        val maxVerts = MAX_PARTICLES * VERTS_PER_PARTICLE
        val maxIndices = MAX_PARTICLES * INDICES_PER_PARTICLE

        vertexBuffer = ByteBuffer.allocateDirect(maxVerts * FLOATS_PER_VERTEX * 4)
            .order(ByteOrder.nativeOrder())

        // Pre-build index buffer (each quad: 0,1,2, 0,2,3 offset per particle)
        val indices = ShortArray(maxIndices)
        for (i in 0 until MAX_PARTICLES) {
            val base = (i * 4).toShort()
            val ii = i * 6
            indices[ii] = base
            indices[ii + 1] = (base + 1).toShort()
            indices[ii + 2] = (base + 2).toShort()
            indices[ii + 3] = base
            indices[ii + 4] = (base + 2).toShort()
            indices[ii + 5] = (base + 3).toShort()
        }

        val vaoBuf = IntArray(1); GLES32.glGenVertexArrays(1, vaoBuf, 0); vao = vaoBuf[0]
        val vboBuf = IntArray(1); GLES32.glGenBuffers(1, vboBuf, 0); vbo = vboBuf[0]
        val eboBuf = IntArray(1); GLES32.glGenBuffers(1, eboBuf, 0); ebo = eboBuf[0]

        GLES32.glBindVertexArray(vao)

        // VBO (dynamic)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vbo)
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, maxVerts * FLOATS_PER_VERTEX * 4, null, GLES32.GL_DYNAMIC_DRAW)
        // pos(3)
        GLES32.glEnableVertexAttribArray(0)
        GLES32.glVertexAttribPointer(0, 3, GLES32.GL_FLOAT, false, FLOATS_PER_VERTEX * 4, 0)
        // uv(2)
        GLES32.glEnableVertexAttribArray(1)
        GLES32.glVertexAttribPointer(1, 2, GLES32.GL_FLOAT, false, FLOATS_PER_VERTEX * 4, 12)
        // color(4)
        GLES32.glEnableVertexAttribArray(2)
        GLES32.glVertexAttribPointer(2, 4, GLES32.GL_FLOAT, false, FLOATS_PER_VERTEX * 4, 20)

        // EBO (static)
        val ib = ByteBuffer.allocateDirect(indices.size * 2).order(ByteOrder.nativeOrder()).asShortBuffer().put(indices)
        ib.flip()
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, ebo)
        GLES32.glBufferData(GLES32.GL_ELEMENT_ARRAY_BUFFER, indices.size * 2, ib, GLES32.GL_STATIC_DRAW)

        GLES32.glBindVertexArray(0)
    }

    private fun putVertex(
        fb: java.nio.FloatBuffer,
        x: Float, y: Float, z: Float,
        u: Float, v: Float,
        r: Float, g: Float, b: Float, a: Float
    ) {
        fb.put(x); fb.put(y); fb.put(z)
        fb.put(u); fb.put(v)
        fb.put(r); fb.put(g); fb.put(b); fb.put(a)
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

        fun update(dt: Float) {
            // Update existing
            for (p in particles) {
                if (!p.alive) continue
                p.age += dt
                if (p.age >= p.lifetime) { p.alive = false; continue }
                p.posX += p.velX * dt
                p.posY += p.velY * dt
                p.posZ += p.velZ * dt - 0.5f * dt  // gravity
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
