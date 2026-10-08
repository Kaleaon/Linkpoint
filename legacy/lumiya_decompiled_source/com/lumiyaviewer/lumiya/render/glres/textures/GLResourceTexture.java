package com.lumiyaviewer.lumiya.render.glres.textures;

import android.opengl.GLES11;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.render.glres.GLResource;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import com.lumiyaviewer.lumiya.render.glres.GLSizedResource;
/* loaded from: classes.dex */
public class GLResourceTexture extends GLSizedResource {
    private static ThreadLocal<int[]> idBuffer = new ThreadLocal<int[]>() { // from class: com.lumiyaviewer.lumiya.render.glres.textures.GLResourceTexture.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public int[] initialValue() {
            return new int[1];
        }
    };

    /* loaded from: classes.dex */
    private static class GLResourceTexturesReference extends GLResourceManager.GLResourceReference {
        private final int loadedSize;

        public GLResourceTexturesReference(GLResource gLResource, int i, GLResourceManager gLResourceManager, int i2) {
            super(gLResource, i, gLResourceManager);
            this.loadedSize = i2;
            TextureMemoryTracker.allocTextureMemory(i2);
        }

        @Override // com.lumiyaviewer.lumiya.render.glres.GLResourceManager.GLGenericResourceReference
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

    @Override // com.lumiyaviewer.lumiya.render.glres.GLResource
    protected int Allocate(GLResourceManager gLResourceManager) {
        int[] iArr = idBuffer.get();
        GLES11.glGenTextures(1, iArr, 0);
        return iArr[0];
    }
}
