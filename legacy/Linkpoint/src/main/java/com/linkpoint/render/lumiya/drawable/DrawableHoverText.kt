package com.linkpoint.render.lumiya.drawable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.opengl.GLES32
import android.opengl.GLUtils
import android.opengl.Matrix
import com.linkpoint.render.lumiya.core.LumiyaRenderContext
import com.linkpoint.render.lumiya.glres.GLBufferManager
import java.util.concurrent.ConcurrentHashMap

/**
 * Renders 3D hover-text labels above prims, mirroring the LL viewer's
 * `LLHUDText` and Lumiya's hover-text overlay.
 *
 * Each label is rendered to a small Bitmap via Android Canvas, uploaded
 * as a GL texture, and drawn as a screen-aligned billboard at the prim's
 * world anchor point. The shared quad VAO + the existing PrimShader
 * program handle the per-frame draw — no new shader required.
 *
 * Memory: one texture per unique label string, freed when the label is
 * removed or the store is destroyed. A short LRU keeps reuse cheap.
 */
class DrawableHoverText {

    companion object {
        /**
         * Distance (m) at which hover text starts to fade. Closer
         * than this and labels render at full alpha. Mirrors the
         * LL viewer hover-text dimensions: labels are crisp inside
         * the conversation radius and fall off past 16m.
         */
        const val FADE_START_DISTANCE = 16.0f

        /**
         * Distance (m) at which hover text reaches zero alpha.
         * Beyond this we skip the draw entirely. The LL viewer
         * hard-cuts at the avatar chat radius (32m) to keep
         * cluttered regions readable; matches our default.
         */
        const val FADE_END_DISTANCE = 32.0f

        /**
         * Linear distance fade in [0, 1]. The geometric scaling in
         * [draw] keeps text legible up to ~4x the texture pixel size,
         * but label clutter at long range is unreadable regardless —
         * this tapers alpha to zero so the screen stays clean.
         *
         * Pure math; no Android dependency so it can be called from
         * unit tests without instantiating a [DrawableHoverText]
         * (whose `Paint` field requires the Android runtime).
         */
        @JvmStatic
        fun fadeFactorAt(dist: Float): Float {
            if (dist <= FADE_START_DISTANCE) return 1f
            if (dist >= FADE_END_DISTANCE) return 0f
            val span = FADE_END_DISTANCE - FADE_START_DISTANCE
            return 1f - (dist - FADE_START_DISTANCE) / span
        }

        private val IDENTITY = FloatArray(16).also { Matrix.setIdentityM(it, 0) }
    }

    private data class Label(
        val primId: Long,
        val text: String,
        var anchorX: Float, var anchorY: Float, var anchorZ: Float,
        var colorR: Float, var colorG: Float, var colorB: Float, var colorA: Float,
        var textureHandle: Int = 0,
        var textureWidth: Int = 0,
        var textureHeight: Int = 0
    )

    private val labels = ConcurrentHashMap<Long, Label>()
    private var quadVao: GLBufferManager.MeshVAO? = null
    private var bufferManager: GLBufferManager? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 32f
        setShadowLayer(2f, 1f, 1f, Color.BLACK)
    }

    // ── Mutation ─────────────────────────────────────────────────────────

    fun upsertLabel(
        primId: Long,
        text: String,
        anchorX: Float, anchorY: Float, anchorZ: Float,
        r: Float, g: Float, b: Float, a: Float
    ) {
        val existing = labels[primId]
        if (existing != null && existing.text == text) {
            existing.anchorX = anchorX
            existing.anchorY = anchorY
            existing.anchorZ = anchorZ
            existing.colorR = r; existing.colorG = g; existing.colorB = b; existing.colorA = a
            return
        }
        // New text or first time — defer GL allocation to draw().
        existing?.let { freeTexture(it) }
        labels[primId] = Label(primId, text, anchorX, anchorY, anchorZ, r, g, b, a)
    }

    fun removeLabel(primId: Long) {
        labels.remove(primId)?.let { freeTexture(it) }
    }

    fun clear() {
        labels.values.forEach { freeTexture(it) }
        labels.clear()
    }

    fun destroy() {
        clear()
        quadVao?.let { bufferManager?.destroyVAO(it) }
        quadVao = null
    }

    // ── Draw ─────────────────────────────────────────────────────────────

    fun draw(ctx: LumiyaRenderContext, viewportWidth: Int, viewportHeight: Int) {
        if (labels.isEmpty()) return
        val program = ctx.primProgram ?: return
        ensureQuad(ctx)
        val vao = quadVao ?: return

        program.use()
        // Hover text is unlit — feed flat ambient.
        program.setLighting(0f, 0f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)
        program.setTexMatrix(IDENTITY)

        // Camera right + up vectors (world-space) for billboard alignment.
        val rightX = ctx.viewMatrix[0]; val rightY = ctx.viewMatrix[4]; val rightZ = ctx.viewMatrix[8]
        val upX = ctx.viewMatrix[1]; val upY = ctx.viewMatrix[5]; val upZ = ctx.viewMatrix[9]

        GLES32.glBindVertexArray(vao.vao)
        GLES32.glEnable(GLES32.GL_BLEND)
        // Separate alpha blend matches LL viewer LLDrawPoolAlpha so the
        // FXAA FBO's alpha channel is preserved across the hover-text
        // pass.
        GLES32.glBlendFuncSeparate(
            GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA,
            GLES32.GL_ZERO, GLES32.GL_ONE_MINUS_SRC_ALPHA
        )

        for (label in labels.values) {
            ensureLabelTexture(label)
            if (label.textureHandle == 0) continue

            // Distance from camera. Drives both geometric scaling
            // and alpha fade.
            val dx = label.anchorX - ctx.cameraPositionX
            val dy = label.anchorY - ctx.cameraPositionY
            val dz = label.anchorZ - ctx.cameraPositionZ
            val dist = Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble()).toFloat()

            // Distance fade: 1 at FADE_START, 0 at FADE_END, linear
            // in between. Skip the draw when fully faded — the
            // texture stays cached so re-entering the radius doesn't
            // re-rasterise. Mirrors LL viewer LLHUDText::draw which
            // multiplies LLHUDObject::mUserAlpha by a distance term
            // before issuing the quad.
            val fadeAlpha = fadeFactorAt(dist)
            if (fadeAlpha <= 0f) continue

            // Scale the billboard so the bitmap maps to a sensible
            // world-size. 32px text ≈ 0.5m at the prim. Distance scaling
            // keeps text legible far away (bounded between 0.5x and 4x).
            val pixelSize = (dist * 0.0015f).coerceIn(0.4f, 4.0f)
            val halfW = label.textureWidth * pixelSize * 0.5f
            val halfH = label.textureHeight * pixelSize * 0.5f

            val m = FloatArray(16)
            Matrix.setIdentityM(m, 0)
            // Build a billboard model matrix: position + (right * scale) on
            // X axis, (up * scale) on Y axis. Z axis is perpendicular but
            // we don't draw depth so the cross-product isn't needed here.
            m[0] = rightX * halfW; m[1] = rightY * halfW; m[2] = rightZ * halfW
            m[4] = upX * halfH;    m[5] = upY * halfH;    m[6] = upZ * halfH
            m[12] = label.anchorX
            m[13] = label.anchorY
            m[14] = label.anchorZ
            m[15] = 1f
            program.setModelMatrix(m)
            program.setColor(label.colorR, label.colorG, label.colorB, label.colorA * fadeAlpha)
            program.setUseTexture(true)
            GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, label.textureHandle)
            program.setTextureSampler(0)
            GLES32.glDrawElements(GLES32.GL_TRIANGLES, vao.indexCount, GLES32.GL_UNSIGNED_SHORT, 0)
        }
        GLES32.glBindVertexArray(0)
    }

    // ── Resources ────────────────────────────────────────────────────────

    private fun ensureQuad(ctx: LumiyaRenderContext) {
        if (quadVao != null) return
        val bm = bufferManager ?: GLBufferManager(ctx.resourceManager).also { bufferManager = it }
        // Quad spans -1..1 on X/Y so the per-label model matrix's right/up
        // half-extents map directly to world units. Normal points +Z.
        val verts = floatArrayOf(
            -1f, -1f, 0f,  0f, 0f, 1f,  0f, 1f,
             1f, -1f, 0f,  0f, 0f, 1f,  1f, 1f,
             1f,  1f, 0f,  0f, 0f, 1f,  1f, 0f,
            -1f,  1f, 0f,  0f, 0f, 1f,  0f, 0f
        )
        val idx = shortArrayOf(0, 1, 2, 0, 2, 3)
        quadVao = bm.buildVAO(verts, idx, listOf(0 to 3, 1 to 3, 2 to 2))
    }

    private fun ensureLabelTexture(label: Label) {
        if (label.textureHandle != 0) return
        val bitmap = renderTextToBitmap(label.text)
        val handle = IntArray(1)
        GLES32.glGenTextures(1, handle, 0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, handle[0])
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_EDGE)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_EDGE)
        GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, bitmap, 0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0)
        label.textureHandle = handle[0]
        label.textureWidth = bitmap.width
        label.textureHeight = bitmap.height
        bitmap.recycle()
    }

    private fun renderTextToBitmap(text: String): Bitmap {
        // Word-wrap onto multiple lines if too wide.
        val maxLineWidthPx = 512
        val lines = wrap(text, maxLineWidthPx)
        val padding = 8
        val ascent = -paint.ascent()
        val descent = paint.descent()
        val lineHeight = (ascent + descent).toInt() + 2
        val width = (lines.maxOf { paint.measureText(it) }.toInt() + padding * 2).coerceAtLeast(16)
        val height = (lineHeight * lines.size + padding * 2).coerceAtLeast(16)
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        var y = padding + ascent
        for (line in lines) {
            canvas.drawText(line, padding.toFloat(), y, paint)
            y += lineHeight
        }
        return bmp
    }

    private fun wrap(text: String, maxWidthPx: Int): List<String> {
        if (paint.measureText(text) <= maxWidthPx) return listOf(text)
        val words = text.split(' ')
        val out = mutableListOf<String>()
        var current = StringBuilder()
        for (w in words) {
            val candidate = if (current.isEmpty()) w else "$current $w"
            if (paint.measureText(candidate) <= maxWidthPx) {
                current = StringBuilder(candidate)
            } else {
                if (current.isNotEmpty()) out.add(current.toString())
                current = StringBuilder(w)
            }
        }
        if (current.isNotEmpty()) out.add(current.toString())
        return out
    }

    private fun freeTexture(label: Label) {
        if (label.textureHandle != 0) {
            GLES32.glDeleteTextures(1, intArrayOf(label.textureHandle), 0)
            label.textureHandle = 0
        }
    }

}
