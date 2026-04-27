// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres;

import android.opengl.GLES30;
import com.lumiyaviewer.lumiya.Debug;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres:
//            GLQuery, GLResource, GLResourceManager

private static class rceReference extends rceReference
{

    public void GLFree()
    {
        int ai[] = (int[])GLQuery._2D_get0().get();
        ai[0] = handle;
        Debug.Printf("GLBuffer: deleted buffer %d", new Object[] {
            Integer.valueOf(ai[0])
        });
        GLES30.glDeleteQueries(1, ai, 0);
    }

    rceReference(GLResource glresource, int i, GLResourceManager glresourcemanager)
    {
        super(glresource, i, glresourcemanager);
    }
}
