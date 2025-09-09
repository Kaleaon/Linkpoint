package com.lumiyaviewer.lumiya.react;

import android.os.Handler;
import android.os.Looper;
import com.lumiyaviewer.lumiya.Debug;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nonnull;

public class UIThreadExecutor implements Executor {
    /* access modifiers changed from: private */
    public final Handler handler = new Handler(this.mainLooper);
    /* access modifiers changed from: private */
    public final Lock lock = new ReentrantLock();
    private final Looper mainLooper = Looper.getMainLooper();
    /* access modifiers changed from: private */
    public final Queue<Runnable> queue = new ConcurrentLinkedQueue();
    /* access modifiers changed from: private */
    public final Runnable queueRunnable = new $Lambda$D7hbGKuty0crHscGTILCtFXqo(this);
    /* access modifiers changed from: private */
    public final AtomicBoolean runnablePosted = new AtomicBoolean(false);
    private final Executor serialExecutor = new Executor() {
        public void execute(@Nonnull Runnable runnable) {
            try {
                UIThreadExecutor.this.lock.lock();
                UIThreadExecutor.this.queue.add(runnable);
                if (!UIThreadExecutor.this.runnablePosted.getAndSet(true)) {
                    UIThreadExecutor.this.handler.post(UIThreadExecutor.this.queueRunnable);
                }
            } finally {
                UIThreadExecutor.this.lock.unlock();
            }
        }
    };

    private static class InstanceHolder {
        /* access modifiers changed from: private */
        public static final UIThreadExecutor Instance = new UIThreadExecutor();

        private InstanceHolder() {
        }
    }

    @Nonnull
    public static Executor getInstance() {
        return InstanceHolder.Instance;
    }

    @Nonnull
    public static Executor getSerialInstance() {
        return InstanceHolder.Instance.serialExecutor;
    }

    public void execute(@Nonnull Runnable runnable) {
        try {
            this.lock.lock();
            if (Looper.myLooper() != this.mainLooper || !this.queue.isEmpty()) {
                this.queue.add(runnable);
                if (!this.runnablePosted.getAndSet(true)) {
                    this.handler.post(this.queueRunnable);
                }
            } else {
                try {
                    runnable.run();
                } catch (Exception e) {
                    Debug.Warning(e);
                } catch (Throwable th) {
                    this.lock.lock();
                    throw th;
                }
                this.lock.lock();
            }
            this.lock.unlock();
        } finally {
            this.lock.unlock();
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_UIThreadExecutor_2348  reason: not valid java name */
    public /* synthetic */ void m47lambda$com_lumiyaviewer_lumiya_react_UIThreadExecutor_2348() {
        try {
            this.lock.lock();
            this.runnablePosted.set(false);
            while (true) {
                Runnable poll = this.queue.poll();
                if (poll != null) {
                    try {
                        poll.run();
                    } catch (Exception e) {
                        Debug.Warning(e);
                    } catch (Throwable th) {
                        this.lock.lock();
                        throw th;
                    }
                    this.lock.lock();
                } else {
                    this.lock.unlock();
                    return;
                }
            }
        } finally {
            this.lock.unlock();
        }
    }
}
