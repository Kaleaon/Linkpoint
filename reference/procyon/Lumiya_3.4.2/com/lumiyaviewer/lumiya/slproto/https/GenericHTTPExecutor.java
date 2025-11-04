// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.https;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.BlockingQueue;
import javax.annotation.Nonnull;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;

public class GenericHTTPExecutor extends ThreadPoolExecutor
{
    private GenericHTTPExecutor() {
        super(1, 3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(@Nonnull final Runnable task) {
                return new Thread(task, "HTTPAccess");
            }
        });
        this.allowCoreThreadTimeOut(true);
    }
    
    public static ExecutorService getInstance() {
        return InstanceHolder.instance;
    }
    
    private static class InstanceHolder
    {
        private static final GenericHTTPExecutor instance;
        
        static {
            instance = new GenericHTTPExecutor(null);
        }
    }
}
