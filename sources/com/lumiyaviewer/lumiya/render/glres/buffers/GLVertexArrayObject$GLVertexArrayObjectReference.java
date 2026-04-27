// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres.buffers;

import android.opengl.GLES30;
import com.lumiyaviewer.lumiya.render.glres.GLGenericResource;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres.buffers:
//            GLVertexArrayObject

private static class vaoIndices extends com.lumiyaviewer.lumiya.render.glres.ference
{

    private final int vaoIndices[];

    public void GLFree()
    {
        GLES30.glDeleteVertexArrays(vaoIndices.length, vaoIndices, 0);
    }

    (GLGenericResource glgenericresource, GLResourceManager glresourcemanager, int ai[])
    {
        super(glgenericresource, glresourcemanager);
        vaoIndices = ai;
    }
}
