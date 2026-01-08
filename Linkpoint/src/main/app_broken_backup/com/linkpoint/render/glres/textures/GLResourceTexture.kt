package com.linkpoint.render.glres.textures

import android.opengl.GLES11
import com.linkpoint.render.TextureMemoryTracker
import com.linkpoint.render.glres.GLResource
import com.linkpoint.render.glres.GLResourceManager
import com.linkpoint.render.glres.GLSizedResource

class GLResourceTexture : GLSizedResource {
    /* access modifiers changed from: private */
    ThreadLocal<IntArray> idBuffer = ThreadLocal<IntArray>() {
        /* access modifiers changed from: protected */
        fun initialValue(): IntArray {
            return IntArray(1)
        }
    }

    private class GLResourceTexturesReference : GLResourceManager.GLResourceReference {
        private Int loadedSize

        GLResourceTexturesReference(GLResource gLResource, Int i, GLResourceManager gLResourceManager, Int i2) {
            super(gLResource, i, gLResourceManager)
            this.loadedSize = i2
            TextureMemoryTracker.allocTextureMemory(i2)
        }

        fun GLFree(): Unit {
            TextureMemoryTracker.releaseTextureMemory(this.loadedSize)
            IntArray iArr = (IntArray) GLResourceTexture.idBuffer.get()
            iArr[0] = this.handle
            GLES11.glDeleteTextures(1, iArr, 0)
        }
    }

    constructor(gLResourceManager: GLResourceManager, i: Int) {
        super(gLResourceManager, i)
        GLResourceTexturesReference(this, this.handle, gLResourceManager, i)
    }

    /* access modifiers changed from: protected */
    fun Allocate(gLResourceManager: GLResourceManager): Int {
        IntArray iArr = idBuffer.get()
        GLES11.glGenTextures(1, iArr, 0)
        return iArr[0]
    }
}
