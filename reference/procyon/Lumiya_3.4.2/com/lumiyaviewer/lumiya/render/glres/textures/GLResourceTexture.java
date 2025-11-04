// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres.textures;

import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import android.opengl.GLES11;
import com.lumiyaviewer.lumiya.render.glres.GLResource;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import com.lumiyaviewer.lumiya.render.glres.GLSizedResource;

public class GLResourceTexture extends GLSizedResource
{
    private static ThreadLocal<int[]> idBuffer;
    
    static {
        GLResourceTexture.idBuffer = new ThreadLocal<int[]>() {
            @Override
            protected int[] initialValue() {
                return new int[1];
            }
        };
    }
    
    public GLResourceTexture(final GLResourceManager glResourceManager, final int n) {
        super(glResourceManager, n);
        new GLResourceTexturesReference(this, this.handle, glResourceManager, n);
    }
    
    @Override
    protected int Allocate(final GLResourceManager glResourceManager) {
        final int[] array = GLResourceTexture.idBuffer.get();
        GLES11.glGenTextures(1, array, 0);
        return array[0];
    }
    
    private static class GLResourceTexturesReference extends GLResourceReference
    {
        private final int loadedSize;
        
        public GLResourceTexturesReference(final GLResource glResource, final int n, final GLResourceManager glResourceManager, final int loadedSize) {
            super(glResource, n, glResourceManager);
            TextureMemoryTracker.allocTextureMemory(this.loadedSize = loadedSize);
        }
        
        @Override
        public void GLFree() {
            TextureMemoryTracker.releaseTextureMemory(this.loadedSize);
            final int[] array = GLResourceTexture.idBuffer.get();
            array[0] = this.handle;
            GLES11.glDeleteTextures(1, array, 0);
        }
    }
}
