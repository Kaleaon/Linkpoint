// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.executors;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ResourceCleanupExecutor extends ScheduledThreadPoolExecutor
{
    public ResourceCleanupExecutor() {
        super(1, new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable task) {
                return new Thread(task, "ResourceCleanup");
            }
        });
    }
    
    public static ResourceCleanupExecutor getInstance() {
        return InstanceHolder.Instance;
    }
    
    private static class InstanceHolder
    {
        private static final ResourceCleanupExecutor Instance;
        
        static {
            Instance = new ResourceCleanupExecutor();
        }
    }
}
