// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ObjectsManager

public static class isLoading
{

    public final boolean isLoading;
    public final ImmutableList objects;

    public (ImmutableList immutablelist, boolean flag)
    {
        objects = immutablelist;
        isLoading = flag;
    }
}
