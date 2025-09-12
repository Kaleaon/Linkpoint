// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.executors;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class ResourceCleanupExecutor extends ScheduledThreadPoolExecutor
{
    private static class InstanceHolder
    {

        private static final ResourceCleanupExecutor Instance = new ResourceCleanupExecutor();

        static ResourceCleanupExecutor _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }


    public ResourceCleanupExecutor()
    {
        super(1, new ThreadFactory() {

            public Thread newThread(Runnable runnable)
            {
                return new Thread(runnable, "ResourceCleanup");
            }

        });
    }

    public static ResourceCleanupExecutor getInstance()
    {
        return InstanceHolder._2D_get0();
    }
}
