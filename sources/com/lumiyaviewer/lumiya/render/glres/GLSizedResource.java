// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres;


// Referenced classes of package com.lumiyaviewer.lumiya.render.glres:
//            GLResource, GLResourceManager

public abstract class GLSizedResource extends GLResource
{

    private final int loadedSize;

    protected GLSizedResource(GLResourceManager glresourcemanager, int i)
    {
        super(glresourcemanager);
        loadedSize = i;
    }

    public final int getLoadedSize()
    {
        return loadedSize;
    }
}
