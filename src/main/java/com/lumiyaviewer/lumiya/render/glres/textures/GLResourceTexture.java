package com.lumiyaviewer.lumiya.render.glres.textures;

import android.opengl.GLES11;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.render.glres.GLResource;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager.GLResourceReference;
import com.lumiyaviewer.lumiya.render.glres.GLSizedResource;

public class GLResourceTexture extends GLSizedResource {
    private static ThreadLocal<int[]> idBuffer = new ThreadLocal<int[]>() {
        protected int[] initialValue() {
            return new int[1];
        }
    };

    private static class GLResourceTexturesReference extends GLResourceReference {
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
        GLResourceTexturesReference gLResourceTexturesReference = new GLResourceTexturesReference(this, this.handle, gLResourceManager, i);
    }

    protected int Allocate(GLResourceManager gLResourceManager) {
        int[] iArr = (int[]) idBuffer.get();
        GLES11.glGenTextures(1, iArr, 0);
        return iArr[0];
    }
}
