package com.lumiyaviewer.lumiya.slproto.prims

import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer

class PrimFlexibleInfo {
    fun getFlexedVertexBuffer(renderContext: RenderContext, buffer: GLLoadableBuffer, vertexCount: Int): GLLoadableBuffer {
        return buffer
    }
    
    fun getMatrices(): FloatArray? {
        return null
    }
}
