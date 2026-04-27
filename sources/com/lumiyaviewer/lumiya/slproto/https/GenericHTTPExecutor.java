// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.https;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GenericHTTPExecutor extends ThreadPoolExecutor
{
    private static class InstanceHolder
    {

        private static final GenericHTTPExecutor instance = new GenericHTTPExecutor(null);

        static GenericHTTPExecutor _2D_get0()
        {
            return instance;
        }


        private InstanceHolder()
        {
        }
    }


    private GenericHTTPExecutor()
    {
        super(1, 3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new ThreadFactory() {

            public Thread newThread(Runnable runnable)
            {
                return new Thread(runnable, "HTTPAccess");
            }

        });
        allowCoreThreadTimeOut(true);
    }

    GenericHTTPExecutor(GenericHTTPExecutor generichttpexecutor)
    {
        this();
    }

    public static ExecutorService getInstance()
    {
        return InstanceHolder._2D_get0();
    }
}
