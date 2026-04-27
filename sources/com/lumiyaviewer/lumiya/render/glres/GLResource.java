// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres;


// Referenced classes of package com.lumiyaviewer.lumiya.render.glres:
//            GLGenericResource, GLResourceManager

public abstract class GLResource
    implements GLGenericResource
{

    public final int handle;

    public GLResource(GLResourceManager glresourcemanager)
    {
        handle = Allocate(glresourcemanager);
    }

    protected abstract int Allocate(GLResourceManager glresourcemanager);
}
