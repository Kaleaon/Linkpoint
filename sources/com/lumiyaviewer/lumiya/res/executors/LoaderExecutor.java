// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.executors;


// Referenced classes of package com.lumiyaviewer.lumiya.res.executors:
//            WeakExecutor

public class LoaderExecutor extends WeakExecutor
{
    private static class InstanceHolder
    {

        private static final LoaderExecutor Instance = new LoaderExecutor(null);

        static LoaderExecutor _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }


    private LoaderExecutor()
    {
        super("ResourceLoader", 1);
    }

    LoaderExecutor(LoaderExecutor loaderexecutor)
    {
        this();
    }

    public static LoaderExecutor getInstance()
    {
        return InstanceHolder._2D_get0();
    }
}
