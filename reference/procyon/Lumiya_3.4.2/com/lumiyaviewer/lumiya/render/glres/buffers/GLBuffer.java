// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres.buffers;

import com.lumiyaviewer.lumiya.Debug;
import android.opengl.GLES11;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import com.lumiyaviewer.lumiya.render.glres.GLResource;

public class GLBuffer extends GLResource
{
    private static ThreadLocal<int[]> idBuffer;
    private final DirectByteBuffer rawBuffer;
    
    static {
        GLBuffer.idBuffer = new ThreadLocal<int[]>() {
            @Override
            protected int[] initialValue() {
                return new int[1];
            }
        };
    }
    
    public GLBuffer(final GLResourceManager glResourceManager, final DirectByteBuffer rawBuffer) {
        super(glResourceManager);
        this.rawBuffer = rawBuffer;
        if (rawBuffer != null) {
            TextureMemoryTracker.allocBufferMemory(rawBuffer.getCapacity());
        }
        new GLResourceBufferReference(this, this.handle, glResourceManager, this.rawBuffer);
    }
    
    @Override
    protected int Allocate(final GLResourceManager glResourceManager) {
        final int[] array = GLBuffer.idBuffer.get();
        GLES11.glGenBuffers(1, array, 0);
        Debug.Printf("GLBuffer: allocated buffer %d", array[0]);
        return array[0];
    }
    
    private static class GLResourceBufferReference extends GLResourceReference
    {
        private final DirectByteBuffer rawBuffer;
        
        public GLResourceBufferReference(final GLResource glResource, final int n, final GLResourceManager glResourceManager, final DirectByteBuffer rawBuffer) {
            super(glResource, n, glResourceManager);
            this.rawBuffer = rawBuffer;
        }
        
        @Override
        public void GLFree() {
            final int[] array = GLBuffer.idBuffer.get();
            array[0] = this.handle;
            Debug.Printf("GLBuffer: deleted buffer %d", array[0]);
            GLES11.glDeleteBuffers(1, array, 0);
            if (this.rawBuffer != null) {
                TextureMemoryTracker.releaseBufferMemory(this.rawBuffer.getCapacity());
            }
        }
    }
}
