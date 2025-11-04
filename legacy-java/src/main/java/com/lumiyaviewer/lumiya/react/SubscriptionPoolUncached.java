package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.react.Subscription;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class SubscriptionPoolUncached<K, T> implements Subscribable<K, T> {
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
        return null;
    }
}
