package com.lumiyaviewer.lumiya.render.glres.buffers

import android.opengl.GLES11
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker
import com.lumiyaviewer.lumiya.render.glres.GLResource
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager
import com.lumiyaviewer.rawbuffers.DirectByteBuffer

class GLBuffer(
    glResourceManager: GLResourceManager,
    private val rawBuffer: DirectByteBuffer?
) : GLResource(glResourceManager) {

    companion object {
        private val idBuffer = object : ThreadLocal<IntArray>() {
            override fun initialValue(): IntArray {
                return IntArray(1)
            }
        }
    }

    private class GLResourceBufferReference(
        glResource: GLResource,
        handle: Int,
        glResourceManager: GLResourceManager,
        private val rawBuffer: DirectByteBuffer?
    ) : GLResourceManager.GLResourceReference(glResource, handle, glResourceManager) {

        override fun GLFree() {
            val ids = idBuffer.get()
            ids!![0] = handle
            Debug.Printf("GLBuffer: deleted buffer %d", ids[0])
            GLES11.glDeleteBuffers(1, ids, 0)
            if (rawBuffer != null) {
                TextureMemoryTracker.releaseBufferMemory(rawBuffer.getCapacity())
            }
        }
    }

    init {
        if (rawBuffer != null) {
            TextureMemoryTracker.allocBufferMemory(rawBuffer.getCapacity())
        }
        // Register for cleanup
        // Note: In Kotlin, we can't easily replicate the inner class constructor call side-effect 
        // that registers itself with the manager unless we expose that mechanism.
        // Assuming GLResourceReference constructor does the registration.
        GLResourceBufferReference(this, handle, glResourceManager, rawBuffer)
    }

    override fun Allocate(glResourceManager: GLResourceManager): Int {
        val ids = idBuffer.get()
        GLES11.glGenBuffers(1, ids, 0)
        Debug.Printf("GLBuffer: allocated buffer %d", ids!![0])
        return ids[0]
    }
}
