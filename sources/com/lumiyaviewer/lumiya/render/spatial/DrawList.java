// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import java.util.ArrayList;

public class DrawList
{

    public final int avatarCountLimit;
    public final ArrayList avatarStubs;
    public final ArrayList avatars;
    final DrawableStore drawableStore;
    public DrawableAvatar myAvatar;
    public final ArrayList objects;
    public int renderPasses[];
    public final ArrayList terrain;

    private DrawList(DrawableStore drawablestore, int i)
    {
        drawableStore = drawablestore;
        myAvatar = null;
        objects = new ArrayList();
        avatars = new ArrayList();
        avatarStubs = new ArrayList();
        terrain = new ArrayList();
        avatarCountLimit = i;
    }

    private DrawList(DrawableStore drawablestore, int i, int j, int k, int l, int i1)
    {
        drawableStore = drawablestore;
        myAvatar = null;
        objects = new ArrayList(i);
        avatars = new ArrayList(j);
        avatarStubs = new ArrayList(k);
        terrain = new ArrayList(l);
        avatarCountLimit = i1;
    }

    public static DrawList create(DrawableStore drawablestore, DrawList drawlist, int i)
    {
        if (drawlist == null)
        {
            return new DrawList(drawablestore, i);
        } else
        {
            return new DrawList(drawablestore, (drawlist.objects.size() * 4) / 3, (drawlist.avatars.size() * 4) / 3, (drawlist.avatarStubs.size() * 4) / 3, (drawlist.terrain.size() * 4) / 3, i);
        }
    }

    void initRenderPasses()
    {
        renderPasses = new int[objects.size()];
    }
}
