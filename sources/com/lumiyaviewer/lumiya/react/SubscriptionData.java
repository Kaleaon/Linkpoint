package com.lumiyaviewer.lumiya.react;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
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
public class SubscriptionData<K, T> implements Subscription.OnData<T>, Subscription.OnError, Loadable, RefreshableOne, UnsubscribableOne {
    @Nullable
    private T data = null;
    @Nullable
    private Throwable error = null;
    @Nullable
    private final Executor executor;
    private final AtomicBoolean inLoadableListeners = new AtomicBoolean(false);
    private final AtomicInteger listenersInvokeAgain = new AtomicInteger(0);
    private final List<Loadable.LoadableStatusListener> loadableStatusListeners = new LinkedList();
    private final Object lock = new Object();
    @Nullable
    private final Subscription.OnData<T> onData;
    @Nullable
    private final Subscription.OnError onError;
    private final AtomicReference<Subscription<K, T>> subscription = new AtomicReference<>();

    public static class DataNotReadyException extends Exception {
        public DataNotReadyException(String str) {
            super(str);
        }

        public DataNotReadyException(String str, Throwable th) {
            super(str, th);
        }
    }

    public SubscriptionData(@Nullable Executor executor2) {
        this.executor = executor2;
        this.onData = null;
        this.onError = null;
    }

    public SubscriptionData(@Nullable Executor executor2, @Nullable Subscription.OnData<T> onData2) {
        this.executor = executor2;
        this.onData = onData2;
        this.onError = null;
    }

    public SubscriptionData(@Nullable Executor executor2, @Nullable Subscription.OnData<T> onData2, @Nullable Subscription.OnError onError2) {
        this.executor = executor2;
        this.onData = onData2;
        this.onError = onError2;
    }

    private void invokeLoadableListeners() {
        ImmutableList<Loadable.LoadableStatusListener> copyOf;
        if (!this.inLoadableListeners.getAndSet(true)) {
            do {
                synchronized (this.lock) {
                    copyOf = ImmutableList.copyOf(this.loadableStatusListeners);
                }
                for (Loadable.LoadableStatusListener onLoadableStatusChange : copyOf) {
                    onLoadableStatusChange.onLoadableStatusChange(this, getLoadableStatus());
                }
            } while (this.listenersInvokeAgain.getAndSet(0) != 0);
            this.inLoadableListeners.set(false);
            return;
        }
        this.listenersInvokeAgain.incrementAndGet();
    }

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
            if (this.data != null) {
                t = this.data;
            } else {
                throw (this.error != null ? new DataNotReadyException(this.error.getMessage(), this.error) : new DataNotReadyException("Data not ready"));
            }
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

    public void requestRefresh() {
        Subscription subscription2 = this.subscription.get();
        if (subscription2 != null) {
            subscription2.requestRefresh();
        }
    }

    public void subscribe(@Nonnull Subscribable<K, T> subscribable, @Nonnull K k) {
        Subscription andSet = this.subscription.getAndSet((Object) null);
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

    public void unsubscribe() {
        Subscription andSet = this.subscription.getAndSet((Object) null);
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
