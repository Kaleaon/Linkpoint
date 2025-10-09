package com.lumiyaviewer.lumiya.render.glres.textures

import android.opengl.GLES11
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker
import com.lumiyaviewer.lumiya.render.glres.GLResource
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager
import com.lumiyaviewer.lumiya.render.glres.GLSizedResource

open class GLResourceTexture(
    glResourceManager: GLResourceManager,
    loadedSize: Int
) : GLSizedResource(glResourceManager, loadedSize) {

    init {
        GLResourceTexturesReference(this, handle, glResourceManager, loadedSize)
    }

    override fun Allocate(glResourceManager: GLResourceManager): Int {
        val idArray = idBuffer.get()
        GLES11.glGenTextures(1, idArray, 0)
        return idArray[0]
    }

    private class GLResourceTexturesReference(
        glResource: GLResource,
        handle: Int,
        glResourceManager: GLResourceManager,
        private val loadedSize: Int
    ) : GLResourceManager.GLResourceReference(glResource, handle, glResourceManager) {

        init {
            TextureMemoryTracker.allocTextureMemory(loadedSize)
        }

        override fun GLFree() {
            TextureMemoryTracker.releaseTextureMemory(loadedSize)
            val idArray = idBuffer.get()
            idArray[0] = handle
            GLES11.glDeleteTextures(1, idArray, 0)
        }
    }

    companion object {
        private val idBuffer = object : ThreadLocal<IntArray>() {
            override fun initialValue(): IntArray {
                return IntArray(1)
            }
        }
    }
}
