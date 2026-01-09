package com.linkpoint.render.glres.buffers

import android.annotation.TargetApi
import android.opengl.GLES10
import android.opengl.GLES11
import android.opengl.GLES20
import android.opengl.GLES30
import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.GLCleanable
import com.linkpoint.rawbuffers.DirectByteBuffer
import androidx.annotation.NonNull

class GLLoadableBuffer : GLCleanable {
    private var glBuffer: GLBuffer = null
    @NonNull
    private DirectByteBuffer rawBuffer

    constructor(directByteBuffer: DirectByteBuffer) {
        directByteBuffer.position(0)
        this.rawBuffer = directByteBuffer
    }

    fun Bind(RenderContext renderContext, Int i, Int i2, Int i3, Int i4, Int i5)  {
        if (renderContext.useVBO) {
            if (this.glBuffer == null) {
                renderContext.KeepBuffer(this.rawBuffer)
                this.glBuffer = GLBuffer(renderContext.glResourceManager, this.rawBuffer)
                renderContext.glBindArrayBuffer(this.glBuffer.handle)
                renderContext.glBufferArrayData(this.rawBuffer.getCapacity(), this.rawBuffer.asByteBuffer(), false)
                renderContext.glResourceManager.addCleanable(this)
            } else {
                renderContext.glBindArrayBuffer(this.glBuffer.handle)
            }
            GLES10.glEnableClientState(i)
            switch (i) {
                case 32884:
                    GLES11.glVertexPointer(i2, i3, i4, i5)
                    return
                case 32885:
                    GLES11.glNormalPointer(i3, i4, i5)
                    return
                case 32888:
                    GLES11.glTexCoordPointer(i2, i3, i4, i5)
                    return
                default:
                    return
            }
        } else {
            renderContext.KeepBuffer(this.rawBuffer)
            GLES10.glEnableClientState(i)
            switch (i) {
                case 32884:
                    GLES10.glVertexPointer(i2, i3, i4, this.rawBuffer.positionFloat(i5))
                    return
                case 32885:
                    GLES10.glNormalPointer(i3, i4, this.rawBuffer.positionFloat(i5))
                    return
                case 32888:
                    GLES10.glTexCoordPointer(i2, i3, i4, this.rawBuffer.positionFloat(i5))
                    return
                default:
                    return
            }
        }
    }

    fun Bind20(RenderContext renderContext, Int i, Int i2, Int i3, Int i4, Int i5)  {
        if (this.glBuffer == null) {
            renderContext.KeepBuffer(this.rawBuffer)
            this.glBuffer = GLBuffer(renderContext.glResourceManager, this.rawBuffer)
            GLES20.glBindBuffer(34962, this.glBuffer.handle)
            GLES20.glBufferData(34962, this.rawBuffer.getCapacity(), this.rawBuffer.asByteBuffer(), 35044)
            renderContext.glResourceManager.addCleanable(this)
        } else {
            GLES20.glBindBuffer(34962, this.glBuffer.handle)
        }
        GLES20.glEnableVertexAttribArray(i)
        GLES20.glVertexAttribPointer(i, i2, i3, false, i4, i5)
    }

    @TargetApi(18)
    fun Bind30Integer(RenderContext renderContext, Int i, Int i2, Int i3, Int i4, Int i5)  {
        if (this.glBuffer == null) {
            renderContext.KeepBuffer(this.rawBuffer)
            this.glBuffer = GLBuffer(renderContext.glResourceManager, this.rawBuffer)
            GLES20.glBindBuffer(34962, this.glBuffer.handle)
            GLES20.glBufferData(34962, this.rawBuffer.getCapacity(), this.rawBuffer.asByteBuffer(), 35044)
            renderContext.glResourceManager.addCleanable(this)
        } else {
            GLES20.glBindBuffer(34962, this.glBuffer.handle)
        }
        GLES30.glEnableVertexAttribArray(i)
        GLES30.glVertexAttribIPointer(i, i2, i3, i4, i5)
    }

    fun BindElements(RenderContext renderContext)  {
        if (!renderContext.useVBO) {
            return
        }
        if (this.glBuffer == null) {
            renderContext.KeepBuffer(this.rawBuffer)
            this.glBuffer = GLBuffer(renderContext.glResourceManager, this.rawBuffer)
            renderContext.glBindElementArrayBuffer(this.glBuffer.handle)
            renderContext.glBufferElementArrayData(this.rawBuffer.getCapacity(), this.rawBuffer.asByteBuffer(), false)
            renderContext.glResourceManager.addCleanable(this)
            return
        }
        renderContext.glBindElementArrayBuffer(this.glBuffer.handle)
    }

    fun BindElements20(RenderContext renderContext)  {
        if (this.glBuffer == null) {
            renderContext.KeepBuffer(this.rawBuffer)
            this.glBuffer = GLBuffer(renderContext.glResourceManager, this.rawBuffer)
            GLES20.glBindBuffer(34963, this.glBuffer.handle)
            GLES20.glBufferData(34963, this.rawBuffer.getCapacity(), this.rawBuffer.asByteBuffer(), 35044)
            renderContext.glResourceManager.addCleanable(this)
            return
        }
        GLES20.glBindBuffer(34963, this.glBuffer.handle)
    }

    @TargetApi(18)
    fun BindUniform(RenderContext renderContext, Int i)  {
        var z: Boolean = false
        if (this.glBuffer == null) {
            this.glBuffer = GLBuffer(renderContext.glResourceManager, this.rawBuffer)
            renderContext.glResourceManager.addCleanable(this)
            z = true
        }
        GLES30.glBindBufferBase(35345, i, this.glBuffer.handle)
        if (z) {
            GLES20.glBufferData(35345, this.rawBuffer.getCapacity(), this.rawBuffer.asByteBuffer(), 35044)
        }
    }

    @TargetApi(18)
    fun BindUniformDynamic(RenderContext renderContext, Int i, Boolean z)  {
        if ((this.glBuffer == null || z) && this.glBuffer == null) {
            this.glBuffer = GLBuffer(renderContext.glResourceManager, this.rawBuffer)
            renderContext.glResourceManager.addCleanable(this)
            z2 = true
        } else {
            z2 = false
        }
        GLES30.glBindBufferBase(35345, i, this.glBuffer.handle)
        if (z2) {
            GLES20.glBufferData(35345, this.rawBuffer.getCapacity(), this.rawBuffer.asByteBuffer(), 35048)
        } else if (z) {
            GLES20.glBufferSubData(35345, 0, this.rawBuffer.getCapacity(), this.rawBuffer.asByteBuffer())
        }
    }

    fun DrawElements(RenderContext renderContext, Int i, Int i2, Int i3, Int i4)  {
        if (renderContext.useVBO) {
            GLES11.glDrawElements(i, i2, i3, i4)
        } else {
            GLES10.glDrawElements(i, i2, 5123, this.rawBuffer.position(i4))
        }
    }

    fun DrawElements20(Int i, Int i2, Int i3, Int i4)  {
        GLES20.glDrawElements(i, i2, i3, i4)
    }

    fun GLCleanup()  {
        this.glBuffer = null
    }

    fun Reload(RenderContext renderContext)  {
        if (renderContext.useVBO && this.glBuffer != null) {
            renderContext.KeepBuffer(this.rawBuffer)
            renderContext.glBindArrayBuffer(this.glBuffer.handle)
            renderContext.glBufferArrayData(this.rawBuffer.getCapacity(), this.rawBuffer.asByteBuffer(), false)
        }
    }

    fun getFloat(i: Int): Float {
        return this.rawBuffer.getFloat(i)
    }

    @NonNull
    fun getRawBuffer(): DirectByteBuffer {
        return this.rawBuffer
    }

    fun getShort(i: Int): Int {
        return this.rawBuffer.getShort(i)
    }
}
