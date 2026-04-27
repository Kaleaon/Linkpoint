// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres;

import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.res.collections.WeakQueue;

public abstract class GLLoadQueue
{
    static interface GLLoadHandler
    {

        public abstract void GLResourceLoaded(GLLoadable glloadable);
    }

    static interface GLLoadable
    {

        public abstract void GLCompleteLoad();

        public abstract int GLGetLoadSize();

        public abstract int GLLoad(RenderContext rendercontext, GLLoadHandler glloadhandler);
    }


    final WeakQueue loadQueue = new WeakQueue();

    public GLLoadQueue()
    {
    }

    public abstract void RunLoadQueue(RenderContext rendercontext);

    public void StopLoadQueue()
    {
        loadQueue.clear();
    }

    public void add(GLLoadable glloadable)
    {
        loadQueue.offer(glloadable);
    }

    public void remove(GLLoadable glloadable)
    {
        loadQueue.remove(glloadable);
    }
}
