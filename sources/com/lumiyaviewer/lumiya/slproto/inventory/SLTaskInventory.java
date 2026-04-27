// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.google.common.collect.ImmutableList;
import java.util.Collection;

public class SLTaskInventory
{

    public final ImmutableList entries;

    public SLTaskInventory()
    {
        entries = ImmutableList.of();
    }

    public SLTaskInventory(Collection collection)
    {
        entries = ImmutableList.copyOf(collection);
    }
}
