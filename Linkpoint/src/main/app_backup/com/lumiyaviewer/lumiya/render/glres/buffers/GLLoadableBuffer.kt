package com.lumiyaviewer.lumiya.render.glres.buffers

import android.annotation.TargetApi
import android.opengl.GLES10
import android.opengl.GLES11
import android.opengl.GLES20
import android.opengl.GLES30
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.GLCleanable
import com.lumiyaviewer.rawbuffers.DirectByteBuffer
import androidx.annotation.NonNull

class GLLoadableBuffer(
    @NonNull private val rawBuffer: DirectByteBuffer
) : GLCleanable {

    private var glBuffer: GLBuffer? = null

    init {
        rawBuffer.position(0)
    }

    fun Bind(renderContext: RenderContext, target: Int, size: Int, type: Int, stride: Int, offset: Int) {
        if (renderContext.useVBO) {
            if (glBuffer == null) {
                renderContext.KeepBuffer(rawBuffer)
                glBuffer = GLBuffer(renderContext.glResourceManager, rawBuffer)
                renderContext.glBindArrayBuffer(glBuffer!!.handle)
                renderContext.glBufferArrayData(rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), false)
                renderContext.glResourceManager.addCleanable(this)
            } else {
                renderContext.glBindArrayBuffer(glBuffer!!.handle)
            }
            GLES10.glEnableClientState(target)
            when (target) {
                32884 -> GLES11.glVertexPointer(size, type, stride, offset) // GL_VERTEX_ARRAY
                32885 -> GLES11.glNormalPointer(type, stride, offset) // GL_NORMAL_ARRAY
                32888 -> GLES11.glTexCoordPointer(size, type, stride, offset) // GL_TEXTURE_COORD_ARRAY
            }
        } else {
            renderContext.KeepBuffer(rawBuffer)
            GLES10.glEnableClientState(target)
            // rawBuffer.positionFloat(offset) logic needs to be handled carefully if it returns a Buffer
            // Assuming positionFloat adjusts position and returns self or buffer
            when (target) {
                32884 -> GLES10.glVertexPointer(size, type, stride, rawBuffer.positionFloat(offset))
                32885 -> GLES10.glNormalPointer(type, stride, rawBuffer.positionFloat(offset))
                32888 -> GLES10.glTexCoordPointer(size, type, stride, rawBuffer.positionFloat(offset))
            }
        }
    }

    fun Bind20(renderContext: RenderContext, index: Int, size: Int, type: Int, stride: Int, offset: Int) {
        if (glBuffer == null) {
            renderContext.KeepBuffer(rawBuffer)
            glBuffer = GLBuffer(renderContext.glResourceManager, rawBuffer)
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, glBuffer!!.handle)
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), GLES20.GL_STATIC_DRAW)
            renderContext.glResourceManager.addCleanable(this)
        } else {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, glBuffer!!.handle)
        }
        GLES20.glEnableVertexAttribArray(index)
        GLES20.glVertexAttribPointer(index, size, type, false, stride, offset)
    }

    @TargetApi(18)
    fun Bind30Integer(renderContext: RenderContext, index: Int, size: Int, type: Int, stride: Int, offset: Int) {
        if (glBuffer == null) {
            renderContext.KeepBuffer(rawBuffer)
            glBuffer = GLBuffer(renderContext.glResourceManager, rawBuffer)
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, glBuffer!!.handle)
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), GLES20.GL_STATIC_DRAW)
            renderContext.glResourceManager.addCleanable(this)
        } else {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, glBuffer!!.handle)
        }
        GLES30.glEnableVertexAttribArray(index)
        GLES30.glVertexAttribIPointer(index, size, type, stride, offset)
    }

    fun BindElements(renderContext: RenderContext) {
        if (!renderContext.useVBO) {
            return
        }
        if (glBuffer == null) {
            renderContext.KeepBuffer(rawBuffer)
            glBuffer = GLBuffer(renderContext.glResourceManager, rawBuffer)
            renderContext.glBindElementArrayBuffer(glBuffer!!.handle)
            renderContext.glBufferElementArrayData(rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), false)
            renderContext.glResourceManager.addCleanable(this)
            return
        }
        renderContext.glBindElementArrayBuffer(glBuffer!!.handle)
    }

    fun BindElements20(renderContext: RenderContext) {
        if (glBuffer == null) {
            renderContext.KeepBuffer(rawBuffer)
            glBuffer = GLBuffer(renderContext.glResourceManager, rawBuffer)
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, glBuffer!!.handle)
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), GLES20.GL_STATIC_DRAW)
            renderContext.glResourceManager.addCleanable(this)
            return
        }
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, glBuffer!!.handle)
    }

    @TargetApi(18)
    fun BindUniform(renderContext: RenderContext, index: Int) {
        var created = false
        if (glBuffer == null) {
            glBuffer = GLBuffer(renderContext.glResourceManager, rawBuffer)
            renderContext.glResourceManager.addCleanable(this)
            created = true
        }
        GLES30.glBindBufferBase(GLES30.GL_UNIFORM_BUFFER, index, glBuffer!!.handle)
        if (created) {
            GLES20.glBufferData(GLES30.GL_UNIFORM_BUFFER, rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), GLES20.GL_STATIC_DRAW)
        }
    }

    @TargetApi(18)
    fun BindUniformDynamic(renderContext: RenderContext, index: Int, update: Boolean) {
        var created = false
        if (glBuffer == null) {
            glBuffer = GLBuffer(renderContext.glResourceManager, rawBuffer)
            renderContext.glResourceManager.addCleanable(this)
            created = true
        }
        
        GLES30.glBindBufferBase(GLES30.GL_UNIFORM_BUFFER, index, glBuffer!!.handle)
        if (created) {
            GLES20.glBufferData(GLES30.GL_UNIFORM_BUFFER, rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), GLES30.GL_DYNAMIC_DRAW)
        } else if (update) {
            GLES20.glBufferSubData(GLES30.GL_UNIFORM_BUFFER, 0, rawBuffer.getCapacity(), rawBuffer.asByteBuffer())
        }
    }

    fun DrawElements(renderContext: RenderContext, mode: Int, count: Int, type: Int, indices: Int) {
        if (renderContext.useVBO) {
            GLES11.glDrawElements(mode, count, type, indices)
        } else {
            GLES10.glDrawElements(mode, count, GLES10.GL_UNSIGNED_SHORT, rawBuffer.position(indices))
        }
    }

    fun DrawElements20(mode: Int, count: Int, type: Int, indices: Int) {
        GLES20.glDrawElements(mode, count, type, indices)
    }

    override fun GLCleanup() {
        glBuffer = null
    }

    fun Reload(renderContext: RenderContext) {
        if (renderContext.useVBO && glBuffer != null) {
            renderContext.KeepBuffer(rawBuffer)
            renderContext.glBindArrayBuffer(glBuffer!!.handle)
            renderContext.glBufferArrayData(rawBuffer.getCapacity(), rawBuffer.asByteBuffer(), false)
        }
    }

    fun getFloat(index: Int): Float {
        return rawBuffer.getFloat(index)
    }

    fun getRawBuffer(): DirectByteBuffer {
        return rawBuffer
    }

    fun getShort(index: Int): Int {
        return rawBuffer.getShort(index).toInt()
    }
}
