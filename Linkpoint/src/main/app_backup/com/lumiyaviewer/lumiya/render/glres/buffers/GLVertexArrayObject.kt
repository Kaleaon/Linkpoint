package com.lumiyaviewer.lumiya.render.glres.buffers

import android.annotation.TargetApi
import android.opengl.GLES30
import com.lumiyaviewer.lumiya.render.glres.GLGenericResource
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager

@TargetApi(18)
class GLVertexArrayObject(
    glResourceManager: GLResourceManager,
    size: Int
) : GLGenericResource(glResourceManager) {

    val size: Int = size
    private val vaoIndices: IntArray

    private class GLVertexArrayObjectReference(
        glResource: GLGenericResource,
        glResourceManager: GLResourceManager,
        private val vaoIndices: IntArray
    ) : GLResourceManager.GLGenericResourceReference(glResource, glResourceManager) {

        override fun GLFree() {
            GLES30.glDeleteVertexArrays(vaoIndices.size, vaoIndices, 0)
        }
    }

    init {
        this.vaoIndices = IntArray(size)
        GLES30.glGenVertexArrays(size, this.vaoIndices, 0)
        GLVertexArrayObjectReference(this, glResourceManager, this.vaoIndices)
    }

    fun Bind(i: Int) {
        GLES30.glBindVertexArray(this.vaoIndices[i])
    }

    fun Unbind() {
        GLES30.glBindVertexArray(0)
    }
}
