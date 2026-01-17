package com.lumiyaviewer.lumiya.react;

import com.google.common.base.Predicate;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.Subscription;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class SubscriptionPool<K, T> implements Unsubscribable<K, T>, Refreshable<K>, ResultHandler<K, T>, Subscribable<K, T>, RequestSource<K, T> {
    @Nullable
    private RequestHandler<K> requestHandler;
    private final Map<K, SubscriptionRequestedList<K, T>> entries = new HashMap();
    private final Object lock = new Object();
    private final ReferenceQueue<Subscription<K, T>> refQueue = new ReferenceQueue<>();
    @Nullable
    private DisposeHandler<T> disposeHandler = null;
    @Nullable
    private Executor disposeExecutor = null;
    @Nullable
    private Executor cacheInvalidateExecutor = null;
    @Nullable
    private Refreshable<K> cacheInvalidateHandler = null;
    private boolean requestOnce = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SubscriptionRequestedList<K, T> extends SubscriptionList<K, T> {
        boolean requested;

        private SubscriptionRequestedList() {
            this.requested = false;
        }

        /* synthetic */ SubscriptionRequestedList(SubscriptionRequestedList subscriptionRequestedList) {
            this();
        }
    }

    public SubscriptionPool() {
        this.requestHandler = null;
        this.requestHandler = null;
    }

    public SubscriptionPool(@Nullable RequestHandler<K> requestHandler) {
        this.requestHandler = null;
        this.requestHandler = requestHandler;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void collectReferences() {
        Subscription.SubscriptionReference subscriptionReference;
        Object key;
        while (true) {
            Reference<? extends Subscription<K, T>> poll = this.refQueue.poll();
            if (poll == null) {
                synchronized (this.lock) {
                    Debug.Printf("UserPic: subscriptions = %d", Integer.valueOf(this.entries.size()));
                }
                return;
            } else if ((poll instanceof Subscription.SubscriptionReference) && (key = (subscriptionReference = (Subscription.SubscriptionReference) poll).getKey()) != null) {
                Debug.Printf("UserPic: collecting reference for %s", key.toString());
                synchronized (this.lock) {
                    SubscriptionRequestedList<K, T> subscriptionRequestedList = this.entries.get(key);
                    if (subscriptionRequestedList != null) {
                        subscriptionRequestedList.removeByReference(subscriptionReference);
                        if (subscriptionRequestedList.isEmpty()) {
                            this.entries.remove(key);
                            if (this.requestHandler != null) {
                                this.requestHandler.onRequestCancelled(key);
                            }
                            disposeOldData(subscriptionRequestedList);
                        }
                    }
                }
            }
        }
    }

    private void disposeOldData(SubscriptionList<K, T> subscriptionList) {
        final T data;
        if (this.disposeHandler == null || (data = subscriptionList.getData()) == null) {
            return;
        }
        if (this.disposeExecutor != null) {
            this.disposeExecutor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$FQ4ueWG6sVQMwgP3YGPP2nbRyFo
                private final /* synthetic */ void $m$0() {
                    ((SubscriptionPool) this).m51lambda$com_lumiyaviewer_lumiya_react_SubscriptionPool_8106(data);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
        } else {
            this.disposeHandler.onDispose(data);
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestSource
    public ResultHandler<K, T> attachRequestHandler(@Nonnull RequestHandler<K> requestHandler) {
        synchronized (this.lock) {
            this.requestHandler = requestHandler;
        }
        return this;
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestSource
    public void detachRequestHandler(@Nonnull RequestHandler<K> requestHandler) {
        synchronized (this.lock) {
            if (this.requestHandler == requestHandler) {
                this.requestHandler = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_SubscriptionPool_8106  reason: not valid java name */
    public /* synthetic */ void m51lambda$com_lumiyaviewer_lumiya_react_SubscriptionPool_8106(Object obj) {
        this.disposeHandler.onDispose(obj);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_SubscriptionPool_8749  reason: not valid java name */
    public /* synthetic */ void m52lambda$com_lumiyaviewer_lumiya_react_SubscriptionPool_8749(Object obj) {
        this.cacheInvalidateHandler.requestUpdate(obj);
    }

    @Override // com.lumiyaviewer.lumiya.react.ResultHandler
    public void onResultData(@Nonnull K k, T t) {
        List<Subscription<K, T>> list;
        collectReferences();
        synchronized (this.lock) {
            SubscriptionRequestedList<K, T> subscriptionRequestedList = this.entries.get(k);
            if (subscriptionRequestedList != null) {
                subscriptionRequestedList.setData(t);
                subscriptionRequestedList.requested = false;
                list = subscriptionRequestedList.getSubscriptions(false);
            } else {
                list = null;
            }
        }
        if (list != null) {
            Iterator<T> it = list.iterator();
            while (it.hasNext()) {
                ((Subscription) it.next()).onData(t);
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.ResultHandler
    public void onResultError(@Nonnull K k, Throwable th) {
        List<Subscription<K, T>> list;
        collectReferences();
        synchronized (this.lock) {
            SubscriptionRequestedList<K, T> subscriptionRequestedList = this.entries.get(k);
            if (subscriptionRequestedList != null) {
                subscriptionRequestedList.setError(th);
                subscriptionRequestedList.requested = false;
                list = subscriptionRequestedList.getSubscriptions(false);
            } else {
                list = null;
            }
        }
        if (list != null) {
            Iterator<T> it = list.iterator();
            while (it.hasNext()) {
                ((Subscription) it.next()).onError(th);
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.Refreshable
    public void requestUpdate(final K k) {
        synchronized (this.lock) {
            if (this.cacheInvalidateHandler != null) {
                if (this.cacheInvalidateExecutor != null) {
                    this.cacheInvalidateExecutor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$FQ4ueWG6sVQMwgP3YGPP2nbRyFo.1
                        private final /* synthetic */ void $m$0() {
                            ((SubscriptionPool) this).m52lambda$com_lumiyaviewer_lumiya_react_SubscriptionPool_8749(k);
                        }

                        @Override // java.lang.Runnable
                        public final void run() {
                            $m$0();
                        }
                    });
                } else {
                    this.cacheInvalidateHandler.requestUpdate(k);
                }
            }
            SubscriptionRequestedList<K, T> subscriptionRequestedList = this.entries.get(k);
            if (subscriptionRequestedList != null && this.requestHandler != null) {
                if (this.requestHandler instanceof Refreshable) {
                    ((Refreshable) this.requestHandler).requestUpdate(k);
                } else if (!this.requestOnce || (!subscriptionRequestedList.requested)) {
                    subscriptionRequestedList.requested = true;
                    this.requestHandler.onRequest(k);
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void requestUpdateAll() {
        synchronized (this.lock) {
            Iterator<T> it = this.entries.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                SubscriptionRequestedList subscriptionRequestedList = (SubscriptionRequestedList) entry.getValue();
                if (subscriptionRequestedList != null && this.requestHandler != null && (!this.requestOnce || (!subscriptionRequestedList.requested))) {
                    subscriptionRequestedList.requested = true;
                    this.requestHandler.onRequest(key);
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void requestUpdateSome(Predicate<K> predicate) {
        HashSet hashSet;
        HashSet<T> hashSet2 = null;
        RequestHandler<K> requestHandler = this.requestHandler;
        if (requestHandler != null) {
            synchronized (this.lock) {
                Iterator<T> it = this.entries.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    Object key = entry.getKey();
                    SubscriptionRequestedList subscriptionRequestedList = (SubscriptionRequestedList) entry.getValue();
                    if (subscriptionRequestedList == null || !predicate.apply(key) || (this.requestOnce && !(!subscriptionRequestedList.requested))) {
                        hashSet = hashSet2;
                    } else {
                        subscriptionRequestedList.requested = true;
                        hashSet = hashSet2 == null ? new HashSet() : hashSet2;
                        hashSet.add(key);
                    }
                    hashSet2 = hashSet;
                }
            }
            if (hashSet2 != null) {
                for (T t : hashSet2) {
                    requestHandler.onRequest(t);
                }
            }
        }
    }

    public SubscriptionPool<K, T> setCacheInvalidateHandler(Refreshable<K> refreshable, @Nullable Executor executor) {
        this.cacheInvalidateHandler = refreshable;
        this.cacheInvalidateExecutor = executor;
        return this;
    }

    public SubscriptionPool<K, T> setDisposeHandler(DisposeHandler<T> disposeHandler, @Nullable Executor executor) {
        this.disposeHandler = disposeHandler;
        this.disposeExecutor = executor;
        return this;
    }

    public SubscriptionPool<K, T> setRequestOnce(boolean z) {
        this.requestOnce = z;
        return this;
    }

    @Override // com.lumiyaviewer.lumiya.react.Subscribable
    public Subscription<K, T> subscribe(@Nonnull K k, @Nonnull Subscription.OnData<T> onData) {
        return subscribe(k, null, onData, null);
    }

    @Override // com.lumiyaviewer.lumiya.react.Subscribable
    public Subscription<K, T> subscribe(@Nonnull K k, @Nonnull Subscription.OnData<T> onData, @Nullable Subscription.OnError onError) {
        return subscribe(k, null, onData, onError);
    }

    @Override // com.lumiyaviewer.lumiya.react.Subscribable
    public Subscription<K, T> subscribe(@Nonnull K k, @Nullable Executor executor, @Nonnull Subscription.OnData<T> onData) {
        return subscribe(k, executor, onData, null);
    }

    @Override // com.lumiyaviewer.lumiya.react.Subscribable
    public Subscription<K, T> subscribe(@Nonnull K k, @Nullable Executor executor, @Nonnull Subscription.OnData<T> onData, @Nullable Subscription.OnError onError) {
        Subscription<K, T> subscription = new Subscription<>(k, this, executor, onData, onError, this.refQueue);
        synchronized (this.lock) {
            boolean z = false;
            SubscriptionRequestedList<K, T> subscriptionRequestedList = this.entries.get(k);
            if (subscriptionRequestedList == null) {
                subscriptionRequestedList = new SubscriptionRequestedList<>(null);
                this.entries.put(k, subscriptionRequestedList);
                z = true;
            }
            subscriptionRequestedList.addSubscription(subscription);
            Throwable error = subscriptionRequestedList.getError();
            if (error != null) {
                subscription.onError(error);
            } else {
                T data = subscriptionRequestedList.getData();
                if (data != null) {
                    subscription.onData(data);
                }
            }
            if (z && this.requestHandler != null) {
                subscriptionRequestedList.requested = true;
                this.requestHandler.onRequest(k);
            }
        }
        collectReferences();
        return subscription;
    }

    @Override // com.lumiyaviewer.lumiya.react.Unsubscribable
    public void unsubscribe(Subscription<K, T> subscription) {
        K key = subscription.getKey();
        synchronized (this.lock) {
            SubscriptionRequestedList<K, T> subscriptionRequestedList = this.entries.get(key);
            if (subscriptionRequestedList != null) {
                subscriptionRequestedList.removeSubscription(subscription);
                if (subscriptionRequestedList.isEmpty()) {
                    this.entries.remove(key);
                    if (this.requestHandler != null) {
                        this.requestHandler.onRequestCancelled(key);
                    }
                    disposeOldData(subscriptionRequestedList);
                }
            }
        }
        collectReferences();
    }

    public SubscriptionPool<K, T> withRequestHandler(@Nonnull RequestHandler<K> requestHandler) {
        synchronized (this.lock) {
            this.requestHandler = requestHandler;
        }
        return this;
    }
}
