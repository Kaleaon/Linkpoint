package com.lumiyaviewer.lumiya.render.glres.textures;

import android.opengl.GLES11;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.render.glres.GLResource;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import com.lumiyaviewer.lumiya.render.glres.GLSizedResource;

public class GLResourceTexture extends GLSizedResource {
    /* access modifiers changed from: private */
    public static ThreadLocal<int[]> idBuffer = new ThreadLocal<int[]>() {
        /* access modifiers changed from: protected */
        public int[] initialValue() {
            return new int[1];
        }
    };

    private static class GLResourceTexturesReference extends GLResourceManager.GLResourceReference {
        private final int loadedSize;

        public GLResourceTexturesReference(GLResource gLResource, int i, GLResourceManager gLResourceManager, int i2) {
            super(gLResource, i, gLResourceManager);
            this.loadedSize = i2;
            TextureMemoryTracker.allocTextureMemory(i2);
        }

        public void GLFree() {
            TextureMemoryTracker.releaseTextureMemory(this.loadedSize);
            int[] iArr = (int[]) GLResourceTexture.idBuffer.get();
            iArr[0] = this.handle;
            GLES11.glDeleteTextures(1, iArr, 0);
        }
    }

    public GLResourceTexture(GLResourceManager gLResourceManager, int i) {
        super(gLResourceManager, i);
        new GLResourceTexturesReference(this, this.handle, gLResourceManager, i);
    }

    /* access modifiers changed from: protected */
    public int Allocate(GLResourceManager gLResourceManager) {
        int[] iArr = idBuffer.get();
        GLES11.glGenTextures(1, iArr, 0);
        return iArr[0];
    }
}
