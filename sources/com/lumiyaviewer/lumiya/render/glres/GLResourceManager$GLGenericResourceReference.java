// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres;

import java.lang.ref.PhantomReference;
import java.util.Set;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres:
//            GLResourceManager, GLGenericResource

public static abstract class  extends PhantomReference
{

    public abstract void GLFree();

    public (GLGenericResource glgenericresource, GLResourceManager glresourcemanager)
    {
        super(glgenericresource, GLResourceManager._2D_get0(glresourcemanager));
        GLResourceManager._2D_get1(glresourcemanager).add(this);
    }
}
