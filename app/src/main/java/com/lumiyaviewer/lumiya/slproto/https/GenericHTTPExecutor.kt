package com.lumiyaviewer.lumiya.slproto.https

import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import androidx.annotation.NonNull

class GenericHTTPExecutor : ThreadPoolExecutor {

    private class InstanceHolder {
        /* access modifiers changed from: private */
        GenericHTTPExecutor instance = GenericHTTPExecutor((GenericHTTPExecutor) null)

        private InstanceHolder() {
        }
    }

    private GenericHTTPExecutor() {
        super(1, 3, 60, TimeUnit.SECONDS, LinkedBlockingQueue<>(), ThreadFactory() {
            Thread newThread(@NonNull Runnable runnable) {
                return Thread(runnable, "HTTPAccess")
            }
        })
        allowCoreThreadTimeOut(true)
    }

    // Removed synthetic constructor artifact from decompiler

    ExecutorService getInstance() {
        return InstanceHolder.instance
    }
}
