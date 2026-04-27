package com.lumiyaviewer.lumiya.slproto.https;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public class GenericHTTPExecutor extends ThreadPoolExecutor {

    /* loaded from: classes.dex */
    private static class InstanceHolder {
        private static final GenericHTTPExecutor instance = new GenericHTTPExecutor(null);

        private InstanceHolder() {
        }
    }

    private GenericHTTPExecutor() {
        super(1, 3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new ThreadFactory() { // from class: com.lumiyaviewer.lumiya.slproto.https.GenericHTTPExecutor.1
            @Override // java.util.concurrent.ThreadFactory
            public Thread newThread(@Nonnull Runnable runnable) {
                return new Thread(runnable, "HTTPAccess");
            }
        });
        allowCoreThreadTimeOut(true);
    }

    /* synthetic */ GenericHTTPExecutor(GenericHTTPExecutor genericHTTPExecutor) {
        this();
    }

    public static ExecutorService getInstance() {
        return InstanceHolder.instance;
    }
}
