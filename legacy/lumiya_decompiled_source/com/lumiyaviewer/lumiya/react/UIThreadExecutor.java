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
/* loaded from: classes.dex */
public class UIThreadExecutor implements Executor {
    private final Lock lock = new ReentrantLock();
    private final AtomicBoolean runnablePosted = new AtomicBoolean(false);
    private final Looper mainLooper = Looper.getMainLooper();
    private final Handler handler = new Handler(this.mainLooper);
    private final Queue<Runnable> queue = new ConcurrentLinkedQueue();
    private final Executor serialExecutor = new Executor() { // from class: com.lumiyaviewer.lumiya.react.UIThreadExecutor.1
        @Override // java.util.concurrent.Executor
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
    private final Runnable queueRunnable = new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$D7hbGKuty0crHscG-TIL-CtFXqo
        private final /* synthetic */ void $m$0() {
            ((UIThreadExecutor) this).m58lambda$com_lumiyaviewer_lumiya_react_UIThreadExecutor_2348();
        }

        @Override // java.lang.Runnable
        public final void run() {
            $m$0();
        }
    };

    /* loaded from: classes.dex */
    private static class InstanceHolder {
        private static final UIThreadExecutor Instance = new UIThreadExecutor();

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

    @Override // java.util.concurrent.Executor
    public void execute(@Nonnull Runnable runnable) {
        try {
            this.lock.lock();
            if (Looper.myLooper() == this.mainLooper && this.queue.isEmpty()) {
                this.lock.unlock();
                try {
                    runnable.run();
                } catch (Exception e) {
                    Debug.Warning(e);
                }
                this.lock.lock();
            } else {
                this.queue.add(runnable);
                if (!this.runnablePosted.getAndSet(true)) {
                    this.handler.post(this.queueRunnable);
                }
            }
        } finally {
            this.lock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_UIThreadExecutor_2348  reason: not valid java name */
    public /* synthetic */ void m58lambda$com_lumiyaviewer_lumiya_react_UIThreadExecutor_2348() {
        try {
            this.lock.lock();
            this.runnablePosted.set(false);
            while (true) {
                Runnable poll = this.queue.poll();
                if (poll == null) {
                    return;
                }
                this.lock.unlock();
                try {
                    poll.run();
                } catch (Exception e) {
                    Debug.Warning(e);
                }
                this.lock.lock();
            }
        } finally {
            this.lock.unlock();
        }
    }
}
