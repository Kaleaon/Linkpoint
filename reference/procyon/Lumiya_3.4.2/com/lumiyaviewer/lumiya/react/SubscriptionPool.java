// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import java.util.Set;
import java.util.HashSet;
import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import java.lang.ref.Reference;
import com.lumiyaviewer.lumiya.Debug;
import java.util.HashMap;
import java.lang.ref.ReferenceQueue;
import java.util.Map;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;

public class SubscriptionPool<K, T> implements Unsubscribable<K, T>, Refreshable<K>, ResultHandler<K, T>, Subscribable<K, T>, RequestSource<K, T>
{
    @Nullable
    private Executor cacheInvalidateExecutor;
    @Nullable
    private Refreshable<K> cacheInvalidateHandler;
    @Nullable
    private Executor disposeExecutor;
    @Nullable
    private DisposeHandler<T> disposeHandler;
    private final Map<K, SubscriptionRequestedList<K, T>> entries;
    private final Object lock;
    private final ReferenceQueue<Subscription<K, T>> refQueue;
    @Nullable
    private RequestHandler<K> requestHandler;
    private boolean requestOnce;
    
    public SubscriptionPool() {
        this.entries = new HashMap<K, SubscriptionRequestedList<K, T>>();
        this.lock = new Object();
        this.refQueue = new ReferenceQueue<Subscription<K, T>>();
        this.requestHandler = null;
        this.disposeHandler = null;
        this.disposeExecutor = null;
        this.cacheInvalidateExecutor = null;
        this.cacheInvalidateHandler = null;
        this.requestOnce = false;
        this.requestHandler = null;
    }
    
    public SubscriptionPool(@Nullable final RequestHandler<K> requestHandler) {
        this.entries = new HashMap<K, SubscriptionRequestedList<K, T>>();
        this.lock = new Object();
        this.refQueue = new ReferenceQueue<Subscription<K, T>>();
        this.requestHandler = null;
        this.disposeHandler = null;
        this.disposeExecutor = null;
        this.cacheInvalidateExecutor = null;
        this.cacheInvalidateHandler = null;
        this.requestOnce = false;
        this.requestHandler = requestHandler;
    }
    
    private void collectReferences() {
        while (true) {
            final Reference<? extends Subscription<K, T>> poll = this.refQueue.poll();
            if (poll == null) {
                break;
            }
            if (!(poll instanceof Subscription.SubscriptionReference)) {
                continue;
            }
            final Subscription.SubscriptionReference<Object, T> subscriptionReference = (Subscription.SubscriptionReference<Object, T>)poll;
            final Object key = subscriptionReference.getKey();
            if (key != null) {
                Debug.Printf("UserPic: collecting reference for %s", key.toString());
                synchronized (this.lock) {
                    final SubscriptionList list = this.entries.get(key);
                    if (list == null) {
                        continue;
                    }
                    list.removeByReference(subscriptionReference);
                    if (!list.isEmpty()) {
                        continue;
                    }
                    this.entries.remove(key);
                    if (this.requestHandler != null) {
                        this.requestHandler.onRequestCancelled((K)key);
                    }
                    this.disposeOldData(list);
                    continue;
                }
                break;
            }
        }
        synchronized (this.lock) {
            Debug.Printf("UserPic: subscriptions = %d", this.entries.size());
        }
    }
    
    private void disposeOldData(final SubscriptionList<K, T> list) {
        if (this.disposeHandler != null) {
            final T data = list.getData();
            if (data != null) {
                if (this.disposeExecutor != null) {
                    this.disposeExecutor.execute(new _$Lambda$FQ4ueWG6sVQMwgP3YGPP2nbRyFo(this, data));
                }
                else {
                    this.disposeHandler.onDispose(data);
                }
            }
        }
    }
    
    @Override
    public ResultHandler<K, T> attachRequestHandler(@Nonnull final RequestHandler<K> requestHandler) {
        synchronized (this.lock) {
            this.requestHandler = requestHandler;
            return this;
        }
    }
    
    @Override
    public void detachRequestHandler(@Nonnull final RequestHandler<K> requestHandler) {
        synchronized (this.lock) {
            if (this.requestHandler == requestHandler) {
                this.requestHandler = null;
            }
        }
    }
    
    @Override
    public void onResultData(@Nonnull final K k, final T data) {
        while (true) {
            this.collectReferences();
            while (true) {
                Label_0089: {
                    synchronized (this.lock) {
                        final SubscriptionRequestedList list = this.entries.get(k);
                        if (list == null) {
                            break Label_0089;
                        }
                        list.setData(data);
                        list.requested = false;
                        final List subscriptions = list.getSubscriptions(false);
                        monitorexit(this.lock);
                        if (subscriptions != null) {
                            final Iterator iterator = subscriptions.iterator();
                            while (iterator.hasNext()) {
                                ((Subscription<K, T>)iterator.next()).onData(data);
                            }
                        }
                    }
                    break;
                }
                final List subscriptions = null;
                continue;
            }
        }
    }
    
    @Override
    public void onResultError(@Nonnull final K k, final Throwable error) {
        while (true) {
            this.collectReferences();
            while (true) {
                Label_0089: {
                    synchronized (this.lock) {
                        final SubscriptionRequestedList list = this.entries.get(k);
                        if (list == null) {
                            break Label_0089;
                        }
                        list.setError(error);
                        list.requested = false;
                        final List subscriptions = list.getSubscriptions(false);
                        monitorexit(this.lock);
                        if (subscriptions != null) {
                            final Iterator iterator = subscriptions.iterator();
                            while (iterator.hasNext()) {
                                ((Subscription)iterator.next()).onError(error);
                            }
                        }
                    }
                    break;
                }
                final List subscriptions = null;
                continue;
            }
        }
    }
    
    @Override
    public void requestUpdate(final K k) {
        while (true) {
            Object o = null;
            Label_0117: {
                synchronized (this.lock) {
                    if (this.cacheInvalidateHandler != null) {
                        if (this.cacheInvalidateExecutor != null) {
                            final Executor cacheInvalidateExecutor = this.cacheInvalidateExecutor;
                            o = new _$Lambda$FQ4ueWG6sVQMwgP3YGPP2nbRyFo$1(this, k);
                            cacheInvalidateExecutor.execute((Runnable)o);
                        }
                        else {
                            this.cacheInvalidateHandler.requestUpdate(k);
                        }
                    }
                    o = this.entries.get(k);
                    if (o != null && this.requestHandler != null) {
                        if (!(this.requestHandler instanceof Refreshable)) {
                            break Label_0117;
                        }
                        ((Refreshable)this.requestHandler).requestUpdate(k);
                    }
                    return;
                }
            }
            if (!this.requestOnce || (((SubscriptionRequestedList)o).requested ^ true)) {
                ((SubscriptionRequestedList)o).requested = true;
                final K i;
                this.requestHandler.onRequest(i);
            }
        }
    }
    
    public void requestUpdateAll() {
        synchronized (this.lock) {
            for (final Map.Entry<Object, V> entry : this.entries.entrySet()) {
                final Object key = entry.getKey();
                final SubscriptionRequestedList list = (SubscriptionRequestedList)entry.getValue();
                if (list != null && this.requestHandler != null && (!this.requestOnce || (list.requested ^ true))) {
                    list.requested = true;
                    this.requestHandler.onRequest((K)key);
                }
            }
        }
        final Object o;
        monitorexit(o);
    }
    
    public void requestUpdateSome(final Predicate<K> predicate) {
        Set<Object> set = null;
        final RequestHandler<K> requestHandler = this.requestHandler;
        if (requestHandler != null) {
        Label_0139_Outer:
            while (true) {
                while (true) {
                    Label_0190: {
                        while (true) {
                            Label_0187: {
                                synchronized (this.lock) {
                                    for (final Map.Entry<Object, V> entry : this.entries.entrySet()) {
                                        final Object key = entry.getKey();
                                        final SubscriptionRequestedList list = (SubscriptionRequestedList)entry.getValue();
                                        if (list == null || !predicate.apply((K)key) || (this.requestOnce && !(list.requested ^ true))) {
                                            break Label_0190;
                                        }
                                        list.requested = true;
                                        if (set != null) {
                                            break Label_0187;
                                        }
                                        set = (Set<Object>)new HashSet<K>();
                                        set.add(key);
                                    }
                                    monitorexit(this.lock);
                                    if (set != null) {
                                        final Iterator<Object> iterator2 = set.iterator();
                                        while (iterator2.hasNext()) {
                                            requestHandler.onRequest(iterator2.next());
                                        }
                                    }
                                }
                                break;
                            }
                            continue Label_0139_Outer;
                        }
                    }
                    continue;
                }
            }
        }
    }
    
    public SubscriptionPool<K, T> setCacheInvalidateHandler(final Refreshable<K> cacheInvalidateHandler, @Nullable final Executor cacheInvalidateExecutor) {
        this.cacheInvalidateHandler = cacheInvalidateHandler;
        this.cacheInvalidateExecutor = cacheInvalidateExecutor;
        return this;
    }
    
    public SubscriptionPool<K, T> setDisposeHandler(final DisposeHandler<T> disposeHandler, @Nullable final Executor disposeExecutor) {
        this.disposeHandler = disposeHandler;
        this.disposeExecutor = disposeExecutor;
        return this;
    }
    
    public SubscriptionPool<K, T> setRequestOnce(final boolean requestOnce) {
        this.requestOnce = requestOnce;
        return this;
    }
    
    @Override
    public Subscription<K, T> subscribe(@Nonnull final K k, @Nonnull final Subscription.OnData<T> onData) {
        return this.subscribe(k, null, onData, null);
    }
    
    @Override
    public Subscription<K, T> subscribe(@Nonnull final K k, @Nonnull final Subscription.OnData<T> onData, @Nullable final Subscription.OnError onError) {
        return this.subscribe(k, null, onData, onError);
    }
    
    @Override
    public Subscription<K, T> subscribe(@Nonnull final K k, @Nullable final Executor executor, @Nonnull final Subscription.OnData<T> onData) {
        return this.subscribe(k, executor, onData, null);
    }
    
    @Override
    public Subscription<K, T> subscribe(@Nonnull final K k, @Nullable final Executor executor, @Nonnull final Subscription.OnData<T> onData, @Nullable Subscription.OnError lock) {
        final Subscription subscription = new Subscription((K)k, (Unsubscribable<K, T>)this, executor, (Subscription.OnData<T>)onData, lock, (ReferenceQueue<Subscription<K, T>>)this.refQueue);
        lock = (Subscription.OnError)this.lock;
        monitorenter(lock);
        boolean b = false;
        try {
            SubscriptionRequestedList list;
            if ((list = this.entries.get(k)) == null) {
                list = new SubscriptionRequestedList(null);
                this.entries.put(k, list);
                b = true;
            }
            list.addSubscription(subscription);
            final Throwable error = list.getError();
            if (error != null) {
                subscription.onError(error);
            }
            else {
                final Object data = list.getData();
                if (data != null) {
                    subscription.onData(data);
                }
            }
            if (b && this.requestHandler != null) {
                list.requested = true;
                this.requestHandler.onRequest(k);
            }
            monitorexit(lock);
            this.collectReferences();
            return subscription;
        }
        finally {
            monitorexit(lock);
        }
    }
    
    @Override
    public void unsubscribe(final Subscription<K, T> subscription) {
        final K key = subscription.getKey();
        synchronized (this.lock) {
            final SubscriptionList list = this.entries.get(key);
            if (list != null) {
                list.removeSubscription(subscription);
                if (list.isEmpty()) {
                    this.entries.remove(key);
                    if (this.requestHandler != null) {
                        this.requestHandler.onRequestCancelled(key);
                    }
                    this.disposeOldData(list);
                }
            }
            monitorexit(this.lock);
            this.collectReferences();
        }
    }
    
    public SubscriptionPool<K, T> withRequestHandler(@Nonnull final RequestHandler<K> requestHandler) {
        synchronized (this.lock) {
            this.requestHandler = requestHandler;
            return this;
        }
    }
    
    private static class SubscriptionRequestedList<K, T> extends SubscriptionList<K, T>
    {
        boolean requested;
        
        private SubscriptionRequestedList() {
            this.requested = false;
        }
    }
}
