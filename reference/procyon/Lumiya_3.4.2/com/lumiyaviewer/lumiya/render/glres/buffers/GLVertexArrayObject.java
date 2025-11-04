// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres.buffers;

import android.opengl.GLES30;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import android.annotation.TargetApi;
import com.lumiyaviewer.lumiya.render.glres.GLGenericResource;

@TargetApi(18)
public class GLVertexArrayObject implements GLGenericResource
{
    public final int size;
    private final int[] vaoIndices;
    
    @TargetApi(18)
    public GLVertexArrayObject(final GLResourceManager glResourceManager, final int size) {
        GLES30.glGenVertexArrays(this.size = size, this.vaoIndices = new int[size], 0);
        new GLVertexArrayObjectReference(this, glResourceManager, this.vaoIndices);
    }
    
    public void Bind(final int n) {
        GLES30.glBindVertexArray(this.vaoIndices[n]);
    }
    
    public void Unbind() {
        GLES30.glBindVertexArray(0);
    }
    
    private static class GLVertexArrayObjectReference extends GLGenericResourceReference
    {
        private final int[] vaoIndices;
        
        GLVertexArrayObjectReference(final GLGenericResource glGenericResource, final GLResourceManager glResourceManager, final int[] vaoIndices) {
            super(glGenericResource, glResourceManager);
            this.vaoIndices = vaoIndices;
        }
        
        @Override
        public void GLFree() {
            GLES30.glDeleteVertexArrays(this.vaoIndices.length, this.vaoIndices, 0);
        }
    }
}
