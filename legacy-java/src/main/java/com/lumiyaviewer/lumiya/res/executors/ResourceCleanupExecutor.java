package com.lumiyaviewer.lumiya.res.executors;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/* loaded from: classes.dex */
public class ResourceCleanupExecutor extends ScheduledThreadPoolExecutor {

    /* loaded from: classes.dex */
    private static class InstanceHolder {
        private static final ResourceCleanupExecutor Instance = new ResourceCleanupExecutor();

        private InstanceHolder() {
        }
    }

    public ResourceCleanupExecutor() {
        super(1, new ThreadFactory() { // from class: com.lumiyaviewer.lumiya.res.executors.ResourceCleanupExecutor.1
            @Override // java.util.concurrent.ThreadFactory
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "ResourceCleanup");
            }
        });
    }

    public static ResourceCleanupExecutor getInstance() {
        return InstanceHolder.Instance;
    }
}
