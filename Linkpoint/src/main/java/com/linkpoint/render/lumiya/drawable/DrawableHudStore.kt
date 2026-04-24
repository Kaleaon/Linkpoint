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

    data class HudPrimInstance(
        val id: Long,
        val attachmentPoint: Int,
        var layer: Int,
        val modelMatrix: FloatArray = FloatArray(16).also { Matrix.setIdentityM(it, 0) },
        val texMatrix: FloatArray = FloatArray(16).also { Matrix.setIdentityM(it, 0) },
        var colorR: Float = 1f, var colorG: Float = 1f, var colorB: Float = 1f, var colorA: Float = 1f,
        var textureHandle: Int = 0
    )

    private val hudPrims = linkedMapOf<Long, HudPrimInstance>()

    fun addHudPrim(
        id: Long,
        attachmentPoint: Int,
        posX: Float,
        posY: Float,
        posZ: Float,
        layer: Int
    ) {
        val instance = HudPrimInstance(id = id, attachmentPoint = attachmentPoint, layer = layer)
        Matrix.translateM(instance.modelMatrix, 0, posX, posY, posZ)
        hudPrims[id] = instance
    }

    fun removeHudPrim(id: Long) {
        hudPrims.remove(id)
    }

    fun hasElements(): Boolean = hudPrims.isNotEmpty()

    fun clear() = hudPrims.clear()

    fun destroy() = hudPrims.clear()

    internal fun debugSortedIds(): List<Long> = sortedHudPrims().map { it.id }

    fun draw(ctx: LumiyaRenderContext) {
        val program = ctx.primProgram ?: return
        val boxVao = DrawableHudMeshCache.boxVao(ctx) ?: return

        program.use()
        program.setLighting(0f, 0f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)

        GLES32.glBindVertexArray(boxVao.vao)
        sortedHudPrims().forEach { hud ->
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

    private fun sortedHudPrims(): List<HudPrimInstance> {
        return hudPrims.values.sortedWith(
            compareBy<HudPrimInstance> { it.layer }
                .thenBy { it.attachmentPoint }
                .thenBy { it.id }
        )
    }
}
