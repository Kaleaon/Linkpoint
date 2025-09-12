// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres.textures;

import android.opengl.GLES11;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.render.glres.GLResource;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import com.lumiyaviewer.lumiya.render.glres.GLSizedResource;

public class GLResourceTexture extends GLSizedResource
{
    private static class GLResourceTexturesReference extends com.lumiyaviewer.lumiya.render.glres.GLResourceManager.GLResourceReference
    {

        private final int loadedSize;

        public void GLFree()
        {
            TextureMemoryTracker.releaseTextureMemory(loadedSize);
            int ai[] = (int[])GLResourceTexture._2D_get0().get();
            ai[0] = handle;
            GLES11.glDeleteTextures(1, ai, 0);
        }

        public GLResourceTexturesReference(GLResource glresource, int i, GLResourceManager glresourcemanager, int j)
        {
            super(glresource, i, glresourcemanager);
            loadedSize = j;
            TextureMemoryTracker.allocTextureMemory(j);
        }
    }


    private static ThreadLocal idBuffer = new ThreadLocal() {

        protected volatile Object initialValue()
        {
            return initialValue();
        }

        protected int[] initialValue()
        {
            return new int[1];
        }

    };

    static ThreadLocal _2D_get0()
    {
        return idBuffer;
    }

    public GLResourceTexture(GLResourceManager glresourcemanager, int i)
    {
        super(glresourcemanager, i);
        new GLResourceTexturesReference(this, handle, glresourcemanager, i);
    }

    protected int Allocate(GLResourceManager glresourcemanager)
    {
        glresourcemanager = (int[])idBuffer.get();
        GLES11.glGenTextures(1, glresourcemanager, 0);
        return glresourcemanager[0];
    }

}
