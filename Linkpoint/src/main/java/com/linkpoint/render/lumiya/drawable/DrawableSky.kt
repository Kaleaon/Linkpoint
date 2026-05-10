package com.linkpoint.render.lumiya.drawable

import android.opengl.GLES32
import android.opengl.Matrix
import com.linkpoint.render.lumiya.core.LumiyaRenderContext
import com.linkpoint.render.lumiya.glres.GLBufferManager
import kotlin.math.sqrt

/**
 * Windlight sky dome + star field.
 *
 * Design lineage: Lumiya `WindlightSky.java`, modernised.
 *
 * Renders an icosahedron-subdivided hemisphere for the sky gradient and
 * a random point-cloud of 500 stars (visible at night).
 */
class DrawableSky(private val ctx: LumiyaRenderContext) {

    companion object {
        private const val STAR_COUNT = 500
        private const val SKY_RADIUS = 500.0f
    }

    private val bufferManager = GLBufferManager(ctx.resourceManager)
    private var skyMesh: GLBufferManager.MeshVAO? = null
    private var starVAO = 0
    private var starVBO = 0
    private val modelMatrix = FloatArray(16)

    init {
        buildIcosphere()
        buildStars()
        Matrix.setIdentityM(modelMatrix, 0)
    }

    fun draw(ctx: LumiyaRenderContext) {
        // Keep dome and stars centred on the camera each frame
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, ctx.cameraPositionX, ctx.cameraPositionY, ctx.cameraPositionZ)
        Matrix.scaleM(modelMatrix, 0, SKY_RADIUS, SKY_RADIUS, SKY_RADIUS)
        drawSkyDome(ctx)
        drawStars(ctx)
    }

    fun destroy() {
        skyMesh?.let { bufferManager.destroyVAO(it) }
        skyMesh = null
        if (starVAO != 0) { GLES32.glDeleteVertexArrays(1, intArrayOf(starVAO), 0); starVAO = 0 }
        if (starVBO != 0) { GLES32.glDeleteBuffers(1, intArrayOf(starVBO), 0); starVBO = 0 }
    }

    // ── Sky dome ─────────────────────────────────────────────────────────

    private fun drawSkyDome(ctx: LumiyaRenderContext) {
        val m = skyMesh ?: return
        val program = ctx.skyProgram ?: return

        program.use()
        program.setModelMatrix(modelMatrix)
        program.setSkyColor(ctx.skyColorR, ctx.skyColorG, ctx.skyColorB)
        program.setHaze(ctx.hazeHorizon, ctx.hazeColorR, ctx.hazeColorG, ctx.hazeColorB)
        program.setSunDirection(ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ)
        program.setSunColor(ctx.sunColorR, ctx.sunColorG, ctx.sunColorB)
        program.setUseClouds(false) // cloud cubemap not loaded yet

        GLES32.glDisable(GLES32.GL_CULL_FACE)
        GLES32.glBindVertexArray(m.vao)
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, m.indexCount, GLES32.GL_UNSIGNED_SHORT, 0)
        GLES32.glBindVertexArray(0)
        GLES32.glEnable(GLES32.GL_CULL_FACE)
    }

    // ── Stars ────────────────────────────────────────────────────────────

    private fun drawStars(ctx: LumiyaRenderContext) {
        if (starVAO == 0) return
        val program = ctx.starsProgram ?: return

        // Dim stars during the day
        val sunZ = ctx.sunDirectionZ
        val brightness = (1.0f - sunZ).coerceIn(0f, 1f)
        if (brightness < 0.05f) return

        program.use()
        program.setModelMatrix(modelMatrix)
        program.setStarColor(1f, 1f, 1f, brightness * 0.8f)

        GLES32.glBindVertexArray(starVAO)
        GLES32.glDrawArrays(GLES32.GL_POINTS, 0, STAR_COUNT)
        GLES32.glBindVertexArray(0)
    }

    // ── Geometry generation ──────────────────────────────────────────────

    /**
     * Build a subdivided icosahedron for a smooth sky dome.
     */
    private fun buildIcosphere() {
        val t = ((1.0 + sqrt(5.0)) / 2.0).toFloat()

        // 12 icosahedron vertices (normalised to unit sphere)
        val baseVerts = arrayOf(
            normalise(-1f, t, 0f), normalise(1f, t, 0f),
            normalise(-1f, -t, 0f), normalise(1f, -t, 0f),
            normalise(0f, -1f, t), normalise(0f, 1f, t),
            normalise(0f, -1f, -t), normalise(0f, 1f, -t),
            normalise(t, 0f, -1f), normalise(t, 0f, 1f),
            normalise(-t, 0f, -1f), normalise(-t, 0f, 1f)
        )

        // 20 triangles
        val baseTris = intArrayOf(
            0,11,5, 0,5,1, 0,1,7, 0,7,10, 0,10,11,
            1,5,9, 5,11,4, 11,10,2, 10,7,6, 7,1,8,
            3,9,4, 3,4,2, 3,2,6, 3,6,8, 3,8,9,
            4,9,5, 2,4,11, 6,2,10, 8,6,7, 9,8,1
        )

        // One subdivision pass for ~80 triangles
        val vertices = mutableListOf<FloatArray>()
        val midCache = mutableMapOf<Long, Int>()
        for (v in baseVerts) vertices.add(v)

        fun midpoint(i1: Int, i2: Int): Int {
            val key = if (i1 < i2) i1.toLong() shl 32 or i2.toLong() else i2.toLong() shl 32 or i1.toLong()
            midCache[key]?.let { return it }
            val v1 = vertices[i1]; val v2 = vertices[i2]
            val mid = normalise(
                (v1[0] + v2[0]) / 2f,
                (v1[1] + v2[1]) / 2f,
                (v1[2] + v2[2]) / 2f
            )
            val idx = vertices.size
            vertices.add(mid)
            midCache[key] = idx
            return idx
        }

        var tris = baseTris.toList()
        // subdivide once
        val newTris = mutableListOf<Int>()
        for (i in tris.indices step 3) {
            val a = tris[i]; val b = tris[i + 1]; val c = tris[i + 2]
            val ab = midpoint(a, b); val bc = midpoint(b, c); val ca = midpoint(c, a)
            newTris.addAll(listOf(a, ab, ca, b, bc, ab, c, ca, bc, ab, bc, ca))
        }
        tris = newTris

        // Pack into float array: pos(3) per vertex
        val vertData = FloatArray(vertices.size * 3)
        for (i in vertices.indices) {
            vertData[i * 3] = vertices[i][0]
            vertData[i * 3 + 1] = vertices[i][1]
            vertData[i * 3 + 2] = vertices[i][2]
        }
        val indexData = ShortArray(tris.size) { tris[it].toShort() }

        skyMesh = bufferManager.buildVAO(
            vertData, indexData,
            listOf(0 to 3)   // position only
        )
    }

    private fun buildStars() {
        val data = FloatArray(STAR_COUNT * 3)
        val rng = java.util.Random(42)
        for (i in 0 until STAR_COUNT) {
            // Random direction on upper hemisphere
            var x: Float; var y: Float; var z: Float
            var attempts = 0
            do {
                x = rng.nextFloat() * 2f - 1f
                y = rng.nextFloat() * 2f - 1f
                z = rng.nextFloat()   // upper hemisphere only
                if (++attempts > 1000) { x = 0f; y = 0f; z = 1f; break }
            } while (x * x + y * y + z * z > 1f || z < 0.1f)
            val len = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            data[i * 3] = x / len
            data[i * 3 + 1] = y / len
            data[i * 3 + 2] = z / len
        }

        val fb = java.nio.ByteBuffer.allocateDirect(data.size * 4)
            .order(java.nio.ByteOrder.nativeOrder()).asFloatBuffer().put(data)
        fb.flip()

        val vaoBuf = IntArray(1); GLES32.glGenVertexArrays(1, vaoBuf, 0); starVAO = vaoBuf[0]
        val vboBuf = IntArray(1); GLES32.glGenBuffers(1, vboBuf, 0); starVBO = vboBuf[0]

        GLES32.glBindVertexArray(starVAO)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, starVBO)
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, data.size * 4, fb, GLES32.GL_STATIC_DRAW)
        GLES32.glEnableVertexAttribArray(0)
        GLES32.glVertexAttribPointer(0, 3, GLES32.GL_FLOAT, false, 12, 0)
        GLES32.glBindVertexArray(0)
    }

    private fun normalise(x: Float, y: Float, z: Float): FloatArray {
        val len = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        return floatArrayOf(x / len, y / len, z / len)
    }
}
