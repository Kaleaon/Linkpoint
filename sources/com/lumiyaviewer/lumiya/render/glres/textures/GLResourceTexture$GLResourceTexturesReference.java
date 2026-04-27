// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres.textures;

import android.opengl.GLES11;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.render.glres.GLResource;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres.textures:
//            GLResourceTexture

private static class loadedSize extends com.lumiyaviewer.lumiya.render.glres.eference
{

    private final int loadedSize;

    public void GLFree()
    {
        TextureMemoryTracker.releaseTextureMemory(loadedSize);
        int ai[] = (int[])GLResourceTexture._2D_get0().get();
        ai[0] = handle;
        GLES11.glDeleteTextures(1, ai, 0);
    }

    public a(GLResource glresource, int i, GLResourceManager glresourcemanager, int j)
    {
        super(glresource, i, glresourcemanager);
        loadedSize = j;
        TextureMemoryTracker.allocTextureMemory(j);
    }
}
