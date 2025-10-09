package com.linkpoint.render.glres.buffers

import android.opengl.GLES11
import com.linkpoint.Debug
import com.linkpoint.render.TextureMemoryTracker
import com.linkpoint.render.glres.GLResource
import com.linkpoint.render.glres.GLResourceManager
import com.linkpoint.rawbuffers.DirectByteBuffer

class GLBuffer : GLResource() {
    /* access modifiers changed from: private */
    @JvmStatic
    ThreadLocal<Int[]> idBuffer = ThreadLocal<Int[]>() {
        /* access modifiers changed from: protected */
        public Int[] initialValue() {
            return Int[1]
        }
    }
    private val DirectByteBuffer rawBuffer

    @JvmStatic
private class GLResourceBufferReference : GLResourceManager().GLResourceReference {
        private val DirectByteBuffer rawBuffer

        public GLResourceBufferReference(GLResource gLResource, Int i, GLResourceManager gLResourceManager, DirectByteBuffer directByteBuffer) {
            super(gLResource, i, gLResourceManager)
            this.rawBuffer = directByteBuffer
        }

        fun GLFree() {
            Int[] iArr = (Int[]) GLBuffer.idBuffer.get()
            iArr[0] = this.handle
            Debug.Printf("GLBuffer: deleted buffer %d", Integer.valueOf(iArr[0]))
            GLES11.glDeleteBuffers(1, iArr, 0)
            if (this.rawBuffer != null) {
                TextureMemoryTracker.releaseBufferMemory(this.rawBuffer.getCapacity())
            }
        }
    }

    public GLBuffer(GLResourceManager gLResourceManager, DirectByteBuffer directByteBuffer) {
        super(gLResourceManager)
        this.rawBuffer = directByteBuffer
        if (directByteBuffer != null) {
            TextureMemoryTracker.allocBufferMemory(directByteBuffer.getCapacity())
        }
        GLResourceBufferReference(this, this.handle, gLResourceManager, this.rawBuffer)
    }

    /* access modifiers changed from: protected */
    public Int Allocate(GLResourceManager gLResourceManager) {
        Int[] iArr = idBuffer.get()
        GLES11.glGenBuffers(1, iArr, 0)
        Debug.Printf("GLBuffer: allocated buffer %d", Integer.valueOf(iArr[0]))
        return iArr[0]
    }
}
