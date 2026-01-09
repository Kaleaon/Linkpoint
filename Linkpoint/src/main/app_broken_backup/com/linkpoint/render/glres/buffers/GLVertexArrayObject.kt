package com.linkpoint.render.glres.buffers

import android.annotation.TargetApi
import android.opengl.GLES30
import com.linkpoint.render.glres.GLGenericResource
import com.linkpoint.render.glres.GLResourceManager

@TargetApi(18)
class GLVertexArrayObject : GLGenericResource {
    Int size
    private IntArray vaoIndices

    private class GLVertexArrayObjectReference : GLResourceManager.GLGenericResourceReference {
        private IntArray vaoIndices

        GLVertexArrayObjectReference(GLGenericResource gLGenericResource, GLResourceManager gLResourceManager, IntArray iArr) {
            super(gLGenericResource, gLResourceManager)
            this.vaoIndices = iArr
        }

        fun GLFree()  {
            GLES30.glDeleteVertexArrays(this.vaoIndices.length, this.vaoIndices, 0)
        }
    }

    @TargetApi(18)
    constructor(gLResourceManager: GLResourceManager, i: Int) {
        this.size = i
        this.vaoIndices = Int[i]
        GLES30.glGenVertexArrays(i, this.vaoIndices, 0)
        GLVertexArrayObjectReference(this, gLResourceManager, this.vaoIndices)
    }

    fun Bind(i: Int)  {
        GLES30.glBindVertexArray(this.vaoIndices[i])
    }

    fun Unbind()  {
        GLES30.glBindVertexArray(0)
    }
}
