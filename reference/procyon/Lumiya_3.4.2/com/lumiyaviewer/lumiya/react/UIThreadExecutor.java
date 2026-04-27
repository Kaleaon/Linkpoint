// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.Debug;
import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Queue;
import android.os.Looper;
import java.util.concurrent.locks.Lock;
import android.os.Handler;
import java.util.concurrent.Executor;

public class UIThreadExecutor implements Executor
{
    private final Handler handler;
    private final Lock lock;
    private final Looper mainLooper;
    private final Queue<Runnable> queue;
    private final Runnable queueRunnable;
    private final AtomicBoolean runnablePosted;
    private final Executor serialExecutor;
    
    public UIThreadExecutor() {
        this.lock = new ReentrantLock();
        this.runnablePosted = new AtomicBoolean(false);
        this.mainLooper = Looper.getMainLooper();
        this.handler = new Handler(this.mainLooper);
        this.queue = new ConcurrentLinkedQueue<Runnable>();
        this.serialExecutor = new Executor() {
            @Override
            public void execute(@Nonnull final Runnable runnable) {
                try {
                    UIThreadExecutor.this.lock.lock();
                    UIThreadExecutor.this.queue.add(runnable);
                    if (!UIThreadExecutor.this.runnablePosted.getAndSet(true)) {
                        UIThreadExecutor.this.handler.post(UIThreadExecutor.this.queueRunnable);
                    }
                }
                finally {
                    UIThreadExecutor.this.lock.unlock();
                }
            }
        };
        this.queueRunnable = new _$Lambda$D7hbGKuty0crHscG_TIL_CtFXqo(this);
    }
    
    @Nonnull
    public static Executor getInstance() {
        return InstanceHolder.Instance;
    }
    
    @Nonnull
    public static Executor getSerialInstance() {
        return InstanceHolder.Instance.serialExecutor;
    }
    
    @Override
    public void execute(@Nonnull final Runnable runnable) {
        while (true) {
            try {
                this.lock.lock();
                if (Looper.myLooper() == this.mainLooper && this.queue.isEmpty()) {
                    try {
                        this.lock.unlock();
                        try {
                            runnable.run();
                            return;
                        }
                        catch (final Exception ex) {
                            Debug.Warning(ex);
                        }
                    }
                    finally {
                        this.lock.lock();
                    }
                }
            }
            finally {
                this.lock.unlock();
            }
            final Runnable runnable2;
            this.queue.add(runnable2);
            if (!this.runnablePosted.getAndSet(true)) {
                this.handler.post(this.queueRunnable);
            }
        }
    }
    
    private static class InstanceHolder
    {
        private static final UIThreadExecutor Instance;
        
        static {
            Instance = new UIThreadExecutor();
        }
    }
}
