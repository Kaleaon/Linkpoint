// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres.buffers;

import android.opengl.GLES11;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.render.glres.GLResource;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

public class GLBuffer extends GLResource
{
    private static class GLResourceBufferReference extends com.lumiyaviewer.lumiya.render.glres.GLResourceManager.GLResourceReference
    {

        private final DirectByteBuffer rawBuffer;

        public void GLFree()
        {
            int ai[] = (int[])GLBuffer._2D_get0().get();
            ai[0] = handle;
            Debug.Printf("GLBuffer: deleted buffer %d", new Object[] {
                Integer.valueOf(ai[0])
            });
            GLES11.glDeleteBuffers(1, ai, 0);
            if (rawBuffer != null)
            {
                TextureMemoryTracker.releaseBufferMemory(rawBuffer.getCapacity());
            }
        }

        public GLResourceBufferReference(GLResource glresource, int i, GLResourceManager glresourcemanager, DirectByteBuffer directbytebuffer)
        {
            super(glresource, i, glresourcemanager);
            rawBuffer = directbytebuffer;
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
    private final DirectByteBuffer rawBuffer;

    static ThreadLocal _2D_get0()
    {
        return idBuffer;
    }

    public GLBuffer(GLResourceManager glresourcemanager, DirectByteBuffer directbytebuffer)
    {
        super(glresourcemanager);
        rawBuffer = directbytebuffer;
        if (directbytebuffer != null)
        {
            TextureMemoryTracker.allocBufferMemory(directbytebuffer.getCapacity());
        }
        new GLResourceBufferReference(this, handle, glresourcemanager, rawBuffer);
    }

    protected int Allocate(GLResourceManager glresourcemanager)
    {
        glresourcemanager = (int[])idBuffer.get();
        GLES11.glGenBuffers(1, glresourcemanager, 0);
        Debug.Printf("GLBuffer: allocated buffer %d", new Object[] {
            Integer.valueOf(glresourcemanager[0])
        });
        return glresourcemanager[0];
    }

}
