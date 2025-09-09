package com.lumiyaviewer.lumiya.render.glres.buffers;

import android.annotation.TargetApi;
import android.opengl.GLES30;
import com.lumiyaviewer.lumiya.render.glres.GLGenericResource;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager.GLGenericResourceReference;

@TargetApi(18)
public class GLVertexArrayObject implements GLGenericResource {
    public final int size;
    private final int[] vaoIndices;

    private static class GLVertexArrayObjectReference extends GLGenericResourceReference {
        private final int[] vaoIndices;

        GLVertexArrayObjectReference(GLGenericResource gLGenericResource, GLResourceManager gLResourceManager, int[] iArr) {
            super(gLGenericResource, gLResourceManager);
            this.vaoIndices = iArr;
        }

        public void GLFree() {
            GLES30.glDeleteVertexArrays(this.vaoIndices.length, this.vaoIndices, 0);
        }
    }

    @TargetApi(18)
    public GLVertexArrayObject(GLResourceManager gLResourceManager, int i) {
        this.size = i;
        this.vaoIndices = new int[i];
        GLES30.glGenVertexArrays(i, this.vaoIndices, 0);
        GLVertexArrayObjectReference gLVertexArrayObjectReference = new GLVertexArrayObjectReference(this, gLResourceManager, this.vaoIndices);
    }

    public void Bind(int i) {
        GLES30.glBindVertexArray(this.vaoIndices[i]);
    }

    public void Unbind() {
        GLES30.glBindVertexArray(0);
    }
}
