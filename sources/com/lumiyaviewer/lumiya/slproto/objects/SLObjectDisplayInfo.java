// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.collect.ImmutableList;

public class SLObjectDisplayInfo
{
    public static interface HasChildrenObjects
    {

        public abstract ImmutableList getChildren();

        public abstract boolean isImplicitlyAdded();
    }


    public final float distance;
    public final int hierarchyLevel;
    public final int localID;
    public final String name;

    public SLObjectDisplayInfo(int i, String s, float f, int j)
    {
        localID = i;
        name = s;
        distance = f;
        hierarchyLevel = j;
    }
}
