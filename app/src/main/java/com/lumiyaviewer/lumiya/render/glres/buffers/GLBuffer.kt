package com.lumiyaviewer.lumiya.render.glres.buffers

import android.opengl.GLES11
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker
import com.lumiyaviewer.lumiya.render.glres.GLResource
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager
import com.lumiyaviewer.rawbuffers.DirectByteBuffer

class GLBuffer : GLResource {
    /* access modifiers changed from: private */
    ThreadLocal<IntArray> idBuffer = ThreadLocal<IntArray>() {
        /* access modifiers changed from: protected */
        IntArray initialValue() {
            return IntArray(1)
        }
    }
    private DirectByteBuffer rawBuffer

    private class GLResourceBufferReference : GLResourceManager.GLResourceReference {
        private DirectByteBuffer rawBuffer

        GLResourceBufferReference(GLResource gLResource, Int i, GLResourceManager gLResourceManager, DirectByteBuffer directByteBuffer) {
            super(gLResource, i, gLResourceManager)
            this.rawBuffer = directByteBuffer
        }

        fun GLFree(): Unit {
            IntArray iArr = (IntArray) GLBuffer.idBuffer.get()
            iArr[0] = this.handle
            Debug.Printf("GLBuffer: deleted buffer %d", Int.valueOf(iArr[0]))
            GLES11.glDeleteBuffers(1, iArr, 0)
            if (this.rawBuffer != null) {
                TextureMemoryTracker.releaseBufferMemory(this.rawBuffer.getCapacity())
            }
        }
    }

    constructor(gLResourceManager: GLResourceManager, directByteBuffer: DirectByteBuffer) {
        super(gLResourceManager)
        this.rawBuffer = directByteBuffer
        if (directByteBuffer != null) {
            TextureMemoryTracker.allocBufferMemory(directByteBuffer.getCapacity())
        }
        GLResourceBufferReference(this, this.handle, gLResourceManager, this.rawBuffer)
    }

    /* access modifiers changed from: protected */
    fun Allocate(gLResourceManager: GLResourceManager): Int {
        IntArray iArr = idBuffer.get()
        GLES11.glGenBuffers(1, iArr, 0)
        Debug.Printf("GLBuffer: allocated buffer %d", Int.valueOf(iArr[0]))
        return iArr[0]
    }
}
