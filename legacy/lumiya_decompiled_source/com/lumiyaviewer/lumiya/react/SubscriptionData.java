package com.lumiyaviewer.lumiya.react;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
@ThreadSafe
/* loaded from: classes.dex */
public class SubscriptionData<K, T> implements Subscription.OnData<T>, Subscription.OnError, Loadable, RefreshableOne, UnsubscribableOne {
    @Nullable
    private T data;
    @Nullable
    private Throwable error;
    @Nullable
    private final Executor executor;
    private final AtomicBoolean inLoadableListeners;
    private final AtomicInteger listenersInvokeAgain;
    private final List<Loadable.LoadableStatusListener> loadableStatusListeners;
    private final Object lock;
    @Nullable
    private final Subscription.OnData<T> onData;
    @Nullable
    private final Subscription.OnError onError;
    private final AtomicReference<Subscription<K, T>> subscription;

    /* loaded from: classes.dex */
    public static class DataNotReadyException extends Exception {
        public DataNotReadyException(String str) {
            super(str);
        }

        public DataNotReadyException(String str, Throwable th) {
            super(str, th);
        }
    }

    public SubscriptionData(@Nullable Executor executor) {
        this.lock = new Object();
        this.subscription = new AtomicReference<>();
        this.data = null;
        this.error = null;
        this.loadableStatusListeners = new LinkedList();
        this.inLoadableListeners = new AtomicBoolean(false);
        this.listenersInvokeAgain = new AtomicInteger(0);
        this.executor = executor;
        this.onData = null;
        this.onError = null;
    }

    public SubscriptionData(@Nullable Executor executor, @Nullable Subscription.OnData<T> onData) {
        this.lock = new Object();
        this.subscription = new AtomicReference<>();
        this.data = null;
        this.error = null;
        this.loadableStatusListeners = new LinkedList();
        this.inLoadableListeners = new AtomicBoolean(false);
        this.listenersInvokeAgain = new AtomicInteger(0);
        this.executor = executor;
        this.onData = onData;
        this.onError = null;
    }

    public SubscriptionData(@Nullable Executor executor, @Nullable Subscription.OnData<T> onData, @Nullable Subscription.OnError onError) {
        this.lock = new Object();
        this.subscription = new AtomicReference<>();
        this.data = null;
        this.error = null;
        this.loadableStatusListeners = new LinkedList();
        this.inLoadableListeners = new AtomicBoolean(false);
        this.listenersInvokeAgain = new AtomicInteger(0);
        this.executor = executor;
        this.onData = onData;
        this.onError = onError;
    }

    private void invokeLoadableListeners() {
        ImmutableList<Loadable.LoadableStatusListener> copyOf;
        if (this.inLoadableListeners.getAndSet(true)) {
            this.listenersInvokeAgain.incrementAndGet();
            return;
        }
        do {
            synchronized (this.lock) {
                copyOf = ImmutableList.copyOf((Collection) this.loadableStatusListeners);
            }
            for (Loadable.LoadableStatusListener loadableStatusListener : copyOf) {
                loadableStatusListener.onLoadableStatusChange(this, getLoadableStatus());
            }
        } while (this.listenersInvokeAgain.getAndSet(0) != 0);
        this.inLoadableListeners.set(false);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable
    public void addLoadableStatusListener(Loadable.LoadableStatusListener loadableStatusListener) {
        synchronized (this.lock) {
            this.loadableStatusListeners.add(loadableStatusListener);
        }
    }

    public void assertHasData() throws DataNotReadyException {
        get();
    }

    @Nonnull
    public T get() throws DataNotReadyException {
        T t;
        synchronized (this.lock) {
            if (this.data == null) {
                if (this.error != null) {
                    throw new DataNotReadyException(this.error.getMessage(), this.error);
                }
                throw new DataNotReadyException("Data not ready");
            }
            t = this.data;
        }
        return t;
    }

    @Nullable
    public T getData() {
        T t;
        synchronized (this.lock) {
            t = this.data;
        }
        return t;
    }

    @Nullable
    public Throwable getError() {
        return this.error;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable
    @Nonnull
    public Loadable.Status getLoadableStatus() {
        return this.subscription.get() == null ? Loadable.Status.Idle : this.error != null ? Loadable.Status.Error : this.data != null ? Loadable.Status.Loaded : Loadable.Status.Loading;
    }

    public boolean hasData() {
        boolean z;
        synchronized (this.lock) {
            z = this.data != null;
        }
        return z;
    }

    public boolean isSubscribed() {
        return this.subscription.get() != null;
    }

    @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
    public void onData(T t) {
        synchronized (this.lock) {
            this.data = t;
            this.error = null;
        }
        if (this.onData != null) {
            this.onData.onData(t);
        }
        invokeLoadableListeners();
    }

    @Override // com.lumiyaviewer.lumiya.react.Subscription.OnError
    public void onError(Throwable th) {
        synchronized (this.lock) {
            this.data = null;
            this.error = th;
        }
        if (this.onError != null) {
            this.onError.onError(th);
        }
        invokeLoadableListeners();
    }

    @Override // com.lumiyaviewer.lumiya.react.RefreshableOne
    public void requestRefresh() {
        Subscription<K, T> subscription = this.subscription.get();
        if (subscription != null) {
            subscription.requestRefresh();
        }
    }

    public void subscribe(@Nonnull Subscribable<K, T> subscribable, @Nonnull K k) {
        Subscription<K, T> andSet = this.subscription.getAndSet(null);
        if (andSet != null) {
            andSet.unsubscribe();
            synchronized (this.lock) {
                this.data = null;
                this.error = null;
            }
        }
        this.subscription.set(subscribable.subscribe(k, this.executor, this, this));
        invokeLoadableListeners();
    }

    @Override // com.lumiyaviewer.lumiya.react.UnsubscribableOne
    public void unsubscribe() {
        Subscription<K, T> andSet = this.subscription.getAndSet(null);
        if (andSet != null) {
            andSet.unsubscribe();
        }
        synchronized (this.lock) {
            this.data = null;
            this.error = null;
        }
        invokeLoadableListeners();
    }
}
