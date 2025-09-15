package com.lumiyaviewer.lumiya.res.executors;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class ResourceCleanupExecutor extends ScheduledThreadPoolExecutor {

    private static class InstanceHolder {
        /* access modifiers changed from: private */
        public static final ResourceCleanupExecutor Instance = new ResourceCleanupExecutor();

        private InstanceHolder() {
        }
    }

    public ResourceCleanupExecutor() {
        super(1, new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "ResourceCleanup");
            }
    }

    public static ResourceCleanupExecutor getInstance() {
        return InstanceHolder.Instance;
    }
}
