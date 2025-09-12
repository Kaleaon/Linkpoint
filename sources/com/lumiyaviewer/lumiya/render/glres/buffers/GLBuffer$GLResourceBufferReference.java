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

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres.buffers:
//            GLBuffer

private static class rawBuffer extends com.lumiyaviewer.lumiya.render.glres.ce
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

    public (GLResource glresource, int i, GLResourceManager glresourcemanager, DirectByteBuffer directbytebuffer)
    {
        super(glresource, i, glresourcemanager);
        rawBuffer = directbytebuffer;
    }
}
