// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres.buffers;

import android.opengl.GLES30;
import com.lumiyaviewer.lumiya.render.glres.GLGenericResource;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;

public class GLVertexArrayObject
    implements GLGenericResource
{
    private static class GLVertexArrayObjectReference extends com.lumiyaviewer.lumiya.render.glres.GLResourceManager.GLGenericResourceReference
    {

        private final int vaoIndices[];

        public void GLFree()
        {
            GLES30.glDeleteVertexArrays(vaoIndices.length, vaoIndices, 0);
        }

        GLVertexArrayObjectReference(GLGenericResource glgenericresource, GLResourceManager glresourcemanager, int ai[])
        {
            super(glgenericresource, glresourcemanager);
            vaoIndices = ai;
        }
    }


    public final int size;
    private final int vaoIndices[];

    public GLVertexArrayObject(GLResourceManager glresourcemanager, int i)
    {
        size = i;
        vaoIndices = new int[i];
        GLES30.glGenVertexArrays(i, vaoIndices, 0);
        new GLVertexArrayObjectReference(this, glresourcemanager, vaoIndices);
    }

    public void Bind(int i)
    {
        GLES30.glBindVertexArray(vaoIndices[i]);
    }

    public void Unbind()
    {
        GLES30.glBindVertexArray(0);
    }
}
