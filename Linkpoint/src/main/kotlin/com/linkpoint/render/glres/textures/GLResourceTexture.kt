package com.linkpoint.render.glres.textures

import android.opengl.GLES11
import com.linkpoint.render.TextureMemoryTracker
import com.linkpoint.render.glres.GLResource
import com.linkpoint.render.glres.GLResourceManager
import com.linkpoint.render.glres.GLSizedResource

class GLResourceTexture : GLSizedResource() {
    /* access modifiers changed from: private */
    @JvmStatic
    val idBuffer: ThreadLocal<IntArray> = ThreadLocal<IntArray>() {
        /* access modifiers changed from: protected */
         public fun initialValue(): IntArray {
            return Int[1]
        }
    }

    @JvmStatic
private class GLResourceTexturesReference : GLResourceManager().GLResourceReference {
        private val Int loadedSize

        public GLResourceTexturesReference(GLResource gLResource, Int i, GLResourceManager gLResourceManager, Int i2) {
            super(gLResource, i, gLResourceManager)
            this.loadedSize = i2
            TextureMemoryTracker.allocTextureMemory(i2)
        }

        fun GLFree() {
            TextureMemoryTracker.releaseTextureMemory(this.loadedSize)
            val iArr: IntArray = (IntArray) GLResourceTexture.idBuffer.get()
            iArr[0] = this.handle
            GLES11.glDeleteTextures(1, iArr, 0)
        }
    }

    public GLResourceTexture(GLResourceManager gLResourceManager, Int i) {
        super(gLResourceManager, i)
        GLResourceTexturesReference(this, this.handle, gLResourceManager, i)
    }

    /* access modifiers changed from: protected */
    public fun Allocate(gLResourceManager: GLResourceManager): Int {
        val iArr: IntArray = idBuffer.get()
        GLES11.glGenTextures(1, iArr, 0)
        return iArr[0]
    }
}
