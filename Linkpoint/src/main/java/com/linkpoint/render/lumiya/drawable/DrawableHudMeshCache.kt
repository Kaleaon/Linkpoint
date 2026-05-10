package com.linkpoint.render.lumiya.drawable

import com.linkpoint.render.lumiya.core.LumiyaRenderContext
import com.linkpoint.render.lumiya.glres.GLBufferManager

internal object DrawableHudMeshCache {
    private var bufferManager: GLBufferManager? = null
    private var box: GLBufferManager.MeshVAO? = null

    fun boxVao(ctx: LumiyaRenderContext): GLBufferManager.MeshVAO? {
        if (box != null) return box
        val bm = GLBufferManager(ctx.resourceManager)
        bufferManager = bm
        box = buildBox(bm)
        return box
    }

    /** Drop cached handles without issuing GL calls — call after GL context loss. */
    fun reset() {
        box = null
        bufferManager = null
    }

    private fun buildBox(bm: GLBufferManager): GLBufferManager.MeshVAO {
        val h = 0.5f
        val v = floatArrayOf(
            -h,-h, h,  0f,0f,1f,  0f,0f,   h,-h, h,  0f,0f,1f,  1f,0f,
            h, h, h,  0f,0f,1f,  1f,1f,  -h, h, h,  0f,0f,1f,  0f,1f,
            h,-h,-h,  0f,0f,-1f, 0f,0f,  -h,-h,-h,  0f,0f,-1f, 1f,0f,
            -h, h,-h,  0f,0f,-1f, 1f,1f,   h, h,-h,  0f,0f,-1f, 0f,1f,
            -h, h, h,  0f,1f,0f,  0f,0f,   h, h, h,  0f,1f,0f,  1f,0f,
            h, h,-h,  0f,1f,0f,  1f,1f,  -h, h,-h,  0f,1f,0f,  0f,1f,
            -h,-h,-h,  0f,-1f,0f, 0f,0f,   h,-h,-h,  0f,-1f,0f, 1f,0f,
            h,-h, h,  0f,-1f,0f, 1f,1f,  -h,-h, h,  0f,-1f,0f, 0f,1f,
            h,-h, h,  1f,0f,0f,  0f,0f,   h,-h,-h,  1f,0f,0f,  1f,0f,
            h, h,-h,  1f,0f,0f,  1f,1f,   h, h, h,  1f,0f,0f,  0f,1f,
            -h,-h,-h, -1f,0f,0f,  0f,0f,  -h,-h, h, -1f,0f,0f,  1f,0f,
            -h, h, h, -1f,0f,0f,  1f,1f,  -h, h,-h, -1f,0f,0f,  0f,1f
        )
        val idx = shortArrayOf(
            0,1,2, 0,2,3,   4,5,6, 4,6,7,   8,9,10, 8,10,11,
            12,13,14, 12,14,15, 16,17,18, 16,18,19, 20,21,22, 20,22,23
        )
        return bm.buildVAO(v, idx, listOf(0 to 3, 1 to 3, 2 to 2))
    }
}
