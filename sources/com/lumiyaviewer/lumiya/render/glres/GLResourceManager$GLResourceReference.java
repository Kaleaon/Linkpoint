// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres;

import java.util.Set;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres:
//            GLResourceManager, GLResource

public static abstract class handle extends erence
{

    protected final int handle;

    public erence(GLResource glresource, int i, GLResourceManager glresourcemanager)
    {
        super(glresource, glresourcemanager);
        handle = i;
        GLResourceManager._2D_get1(glresourcemanager).add(this);
    }
}
