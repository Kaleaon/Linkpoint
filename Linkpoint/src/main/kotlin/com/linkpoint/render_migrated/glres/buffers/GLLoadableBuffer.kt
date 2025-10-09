package com.linkpoint.render.glres.buffers

import android.annotation.TargetApi
import android.opengl.GLES10
import android.opengl.GLES11
import android.opengl.GLES20
import android.opengl.GLES30
import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.GLCleanable
import com.linkpoint.rawbuffers.DirectByteBuffer
import javax.annotation.Nonnull

class GLLoadableBuffer : GLCleanable {
    private GLBuffer glBuffer = null
    private val DirectByteBuffer rawBuffer

    public GLLoadableBuffer(DirectByteBuffer directByteBuffer) {
        directByteBuffer.position(0)
        this.rawBuffer = directByteBuffer
    }

    val Unit Bind(RenderContext renderContext, Int i, Int i2, Int i3, Int i4, Int i5) {
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

    val Unit Bind20(RenderContext renderContext, Int i, Int i2, Int i3, Int i4, Int i5) {
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
    val Unit Bind30Integer(RenderContext renderContext, Int i, Int i2, Int i3, Int i4, Int i5) {
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

    val Unit BindElements(RenderContext renderContext) {
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

    val Unit BindElements20(RenderContext renderContext) {
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
    val Unit BindUniform(RenderContext renderContext, Int i) {
        Boolean z = false
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
    val Unit BindUniformDynamic(RenderContext renderContext, Int i, Boolean z) {
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

    val Unit DrawElements(RenderContext renderContext, Int i, Int i2, Int i3, Int i4) {
        if (renderContext.useVBO) {
            GLES11.glDrawElements(i, i2, i3, i4)
        } else {
            GLES10.glDrawElements(i, i2, 5123, this.rawBuffer.position(i4))
        }
    }

    val Unit DrawElements20(Int i, Int i2, Int i3, Int i4) {
        GLES20.glDrawElements(i, i2, i3, i4)
    }

    fun GLCleanup() {
        this.glBuffer = null
    }

    val Unit Reload(RenderContext renderContext) {
        if (renderContext.useVBO && this.glBuffer != null) {
            renderContext.KeepBuffer(this.rawBuffer)
            renderContext.glBindArrayBuffer(this.glBuffer.handle)
            renderContext.glBufferArrayData(this.rawBuffer.getCapacity(), this.rawBuffer.asByteBuffer(), false)
        }
    }

    public Float getFloat(Int i) {
        return this.rawBuffer.getFloat(i)
    }

    public DirectByteBuffer getRawBuffer() {
        return this.rawBuffer
    }

    public Int getShort(Int i) {
        return this.rawBuffer.getShort(i)
    }
}
