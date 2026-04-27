// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import java.util.Iterator;
import javax.annotation.Nonnull;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;

@ThreadSafe
public class SubscriptionData<K, T> implements OnData<T>, OnError, Loadable, RefreshableOne, UnsubscribableOne
{
    @Nullable
    private T data;
    @Nullable
    private Throwable error;
    @Nullable
    private final Executor executor;
    private final AtomicBoolean inLoadableListeners;
    private final AtomicInteger listenersInvokeAgain;
    private final List<LoadableStatusListener> loadableStatusListeners;
    private final Object lock;
    @Nullable
    private final OnData<T> onData;
    @Nullable
    private final OnError onError;
    private final AtomicReference<Subscription<K, T>> subscription;
    
    public SubscriptionData(@Nullable final Executor executor) {
        this.lock = new Object();
        this.subscription = new AtomicReference<Subscription<K, T>>();
        this.data = null;
        this.error = null;
        this.loadableStatusListeners = new LinkedList<LoadableStatusListener>();
        this.inLoadableListeners = new AtomicBoolean(false);
        this.listenersInvokeAgain = new AtomicInteger(0);
        this.executor = executor;
        this.onData = null;
        this.onError = null;
    }
    
    public SubscriptionData(@Nullable final Executor executor, @Nullable final OnData<T> onData) {
        this.lock = new Object();
        this.subscription = new AtomicReference<Subscription<K, T>>();
        this.data = null;
        this.error = null;
        this.loadableStatusListeners = new LinkedList<LoadableStatusListener>();
        this.inLoadableListeners = new AtomicBoolean(false);
        this.listenersInvokeAgain = new AtomicInteger(0);
        this.executor = executor;
        this.onData = onData;
        this.onError = null;
    }
    
    public SubscriptionData(@Nullable final Executor executor, @Nullable final OnData<T> onData, @Nullable final OnError onError) {
        this.lock = new Object();
        this.subscription = new AtomicReference<Subscription<K, T>>();
        this.data = null;
        this.error = null;
        this.loadableStatusListeners = new LinkedList<LoadableStatusListener>();
        this.inLoadableListeners = new AtomicBoolean(false);
        this.listenersInvokeAgain = new AtomicInteger(0);
        this.executor = executor;
        this.onData = onData;
        this.onError = onError;
    }
    
    private void invokeLoadableListeners() {
        if (!this.inLoadableListeners.getAndSet(true)) {
            do {
                Object o = this.lock;
                synchronized (o) {
                    final ImmutableList<Object> copy = ImmutableList.copyOf((Collection<?>)this.loadableStatusListeners);
                    monitorexit(o);
                    o = copy.iterator();
                    while (((Iterator)o).hasNext()) {
                        ((LoadableStatusListener)((Iterator)o).next()).onLoadableStatusChange(this, this.getLoadableStatus());
                    }
                }
            } while (this.listenersInvokeAgain.getAndSet(0) != 0);
            this.inLoadableListeners.set(false);
        }
        else {
            this.listenersInvokeAgain.incrementAndGet();
        }
    }
    
    @Override
    public void addLoadableStatusListener(final LoadableStatusListener loadableStatusListener) {
        synchronized (this.lock) {
            this.loadableStatusListeners.add(loadableStatusListener);
        }
    }
    
    public void assertHasData() throws DataNotReadyException {
        this.get();
    }
    
    @Nonnull
    public T get() throws DataNotReadyException {
        while (true) {
            while (true) {
                synchronized (this.lock) {
                    if (this.data != null) {
                        return this.data;
                    }
                    if (this.error != null) {
                        throw new DataNotReadyException(this.error.getMessage(), this.error);
                    }
                }
                final DataNotReadyException ex = new DataNotReadyException("Data not ready");
                continue;
            }
        }
    }
    
    @Nullable
    public T getData() {
        synchronized (this.lock) {
            return this.data;
        }
    }
    
    @Nullable
    public Throwable getError() {
        return this.error;
    }
    
    @Nonnull
    @Override
    public Status getLoadableStatus() {
        if (this.subscription.get() == null) {
            return Status.Idle;
        }
        if (this.error != null) {
            return Status.Error;
        }
        if (this.data != null) {
            return Status.Loaded;
        }
        return Status.Loading;
    }
    
    public boolean hasData() {
        synchronized (this.lock) {
            return this.data != null;
        }
    }
    
    public boolean isSubscribed() {
        return this.subscription.get() != null;
    }
    
    @Override
    public void onData(final T data) {
        synchronized (this.lock) {
            this.data = data;
            this.error = null;
            monitorexit(this.lock);
            if (this.onData != null) {
                this.onData.onData(data);
            }
            this.invokeLoadableListeners();
        }
    }
    
    @Override
    public void onError(final Throwable error) {
        synchronized (this.lock) {
            this.data = null;
            this.error = error;
            monitorexit(this.lock);
            if (this.onError != null) {
                this.onError.onError(error);
            }
            this.invokeLoadableListeners();
        }
    }
    
    @Override
    public void requestRefresh() {
        final Subscription subscription = this.subscription.get();
        if (subscription != null) {
            subscription.requestRefresh();
        }
    }
    
    public void subscribe(@Nonnull final Subscribable<K, T> subscribable, @Nonnull final K k) {
        final Subscription<K, T> subscription = this.subscription.getAndSet(null);
        Label_0039: {
            if (subscription == null) {
                break Label_0039;
            }
            subscription.unsubscribe();
            synchronized (this.lock) {
                this.data = null;
                this.error = null;
                monitorexit(this.lock);
                this.subscription.set(subscribable.subscribe(k, this.executor, this, this));
                this.invokeLoadableListeners();
            }
        }
    }
    
    @Override
    public void unsubscribe() {
        final Subscription<K, T> subscription = this.subscription.getAndSet(null);
        if (subscription != null) {
            subscription.unsubscribe();
        }
        synchronized (this.lock) {
            this.data = null;
            this.error = null;
            monitorexit(this.lock);
            this.invokeLoadableListeners();
        }
    }
    
    public static class DataNotReadyException extends Exception
    {
        public DataNotReadyException(final String message) {
            super(message);
        }
        
        public DataNotReadyException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
