package com.linkpoint.render.lumiya.drawable

import android.opengl.GLES32
import android.opengl.Matrix
import com.linkpoint.render.lumiya.core.LumiyaRenderContext

/**
 * Dedicated draw list for HUD attachments.
 *
 * HUD entities are sorted deterministically by layer, attachment point and id.
 */
class DrawableHudStore {
    companion object {
        private const val DEFAULT_VIEWPORT_WIDTH = 1
        private const val DEFAULT_VIEWPORT_HEIGHT = 1
        private const val LAYER_Z_STEP = 0.001f
        private const val ITEM_Z_STEP = 0.00001f
    }

    data class HudPrimInstance(
        val id: Long,
        val attachmentPoint: Int,
        var layer: Int,
        var offsetX: Float,
        var offsetY: Float,
        var offsetZ: Float,
        val modelMatrix: FloatArray = FloatArray(16).also { Matrix.setIdentityM(it, 0) },
        val texMatrix: FloatArray = FloatArray(16).also { Matrix.setIdentityM(it, 0) },
        var colorR: Float = 1f, var colorG: Float = 1f, var colorB: Float = 1f, var colorA: Float = 1f,
        var textureHandle: Int = 0,
        var screenX: Float = 0f,
        var screenY: Float = 0f,
        // Z translation cached separately from [modelMatrix]. The matrix
        // is built via android.opengl.Matrix which is stubbed in JVM
        // unit tests and won't actually populate the matrix entries —
        // so reading modelMatrix[14] returned 0 for every prim and
        // broke the HUD-z-order test. Caching the value at the source
        // keeps render and diagnostics agreeing without forcing tests
        // onto Robolectric.
        var zTranslation: Float = 0f
    )

    private val hudPrims = linkedMapOf<Long, HudPrimInstance>()
    private var viewportWidth: Int = DEFAULT_VIEWPORT_WIDTH
    private var viewportHeight: Int = DEFAULT_VIEWPORT_HEIGHT

    fun addHudPrim(
        id: Long,
        attachmentPoint: Int,
        posX: Float,
        posY: Float,
        posZ: Float,
        layer: Int
    ) {
        val instance = HudPrimInstance(
            id = id,
            attachmentPoint = attachmentPoint,
            layer = layer,
            offsetX = posX,
            offsetY = posY,
            offsetZ = posZ
        )
        updateModelMatrix(instance, zOrder = 0)
        hudPrims[id] = instance
        updateHudLayout()
    }

    fun removeHudPrim(id: Long) {
        hudPrims.remove(id)
    }

    fun hasElements(): Boolean = hudPrims.isNotEmpty()

    fun clear() = hudPrims.clear()

    fun destroy() = hudPrims.clear()

    internal fun debugSortedIds(): List<Long> = sortedHudPrims().map { it.id }
    internal fun debugScreenPosition(id: Long): Pair<Float, Float>? = hudPrims[id]?.let { it.screenX to it.screenY }
    internal fun debugModelZ(id: Long): Float? = hudPrims[id]?.zTranslation

    fun setViewportSize(width: Int, height: Int) {
        viewportWidth = width.coerceAtLeast(1)
        viewportHeight = height.coerceAtLeast(1)
        updateHudLayout()
    }

    fun draw(ctx: LumiyaRenderContext) {
        val program = ctx.primProgram ?: return
        val boxVao = DrawableHudMeshCache.boxVao(ctx) ?: return

        program.use()
        program.setLighting(0f, 0f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)

        GLES32.glBindVertexArray(boxVao.vao)
        buildRenderQueue().forEach { hud ->
            program.setModelMatrix(hud.modelMatrix)
            program.setTexMatrix(hud.texMatrix)
            program.setColor(hud.colorR, hud.colorG, hud.colorB, hud.colorA)
            program.setUseTexture(hud.textureHandle != 0)
            if (hud.textureHandle != 0) {
                GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
                GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, hud.textureHandle)
                program.setTextureSampler(0)
            }
            GLES32.glDrawElements(GLES32.GL_TRIANGLES, boxVao.indexCount, GLES32.GL_UNSIGNED_SHORT, 0)
        }
        GLES32.glBindVertexArray(0)
    }

    private fun buildRenderQueue(): List<HudPrimInstance> = sortedHudPrims()

    private fun sortedHudPrims(): List<HudPrimInstance> {
        return hudPrims.values.sortedWith(
            compareBy<HudPrimInstance> { it.layer }
                .thenBy { it.attachmentPoint }
                .thenBy { it.id }
        )
    }

    private fun updateHudLayout() {
        buildRenderQueue().forEachIndexed { index, hud ->
            updateModelMatrix(hud, index)
        }
    }

    private fun updateModelMatrix(hud: HudPrimInstance, zOrder: Int) {
        val (anchorX, anchorY) = hudAnchor(attachmentPoint = hud.attachmentPoint)
        val baseX = anchorX * viewportWidth
        val baseY = anchorY * viewportHeight
        val zBias = (hud.layer * LAYER_Z_STEP) + (zOrder * ITEM_Z_STEP)

        hud.screenX = baseX + hud.offsetX
        hud.screenY = baseY + hud.offsetY
        hud.zTranslation = hud.offsetZ + zBias
        Matrix.setIdentityM(hud.modelMatrix, 0)
        Matrix.translateM(
            hud.modelMatrix,
            0,
            hud.screenX,
            hud.screenY,
            hud.zTranslation
        )
    }

    private fun hudAnchor(attachmentPoint: Int): Pair<Float, Float> {
        return when (attachmentPoint) {
            34 -> 0.05f to 0.05f // ATTACH_HUD_TOP_LEFT
            33 -> 0.5f to 0.05f // ATTACH_HUD_TOP_CENTER
            32 -> 0.95f to 0.05f // ATTACH_HUD_TOP_RIGHT
            35 -> 0.5f to 0.5f // ATTACH_HUD_CENTER_1
            31 -> 0.5f to 0.6f // ATTACH_HUD_CENTER_2
            36 -> 0.05f to 0.95f // ATTACH_HUD_BOTTOM_LEFT
            37 -> 0.5f to 0.95f // ATTACH_HUD_BOTTOM
            38 -> 0.95f to 0.95f // ATTACH_HUD_BOTTOM_RIGHT
            else -> 0.5f to 0.5f
        }
    }
}
