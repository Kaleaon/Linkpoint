package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.react.Subscription;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SubscriptionPoolUncached<K, T> implements Subscribable<K, T> {
    public Subscription<K, T> subscribe(@Nonnull K k, @Nonnull Subscription.OnData<T> onData) {
        return subscribe(k, (Executor) null, onData, (Subscription.OnError) null);
    }

    public Subscription<K, T> subscribe(@Nonnull K k, @Nonnull Subscription.OnData<T> onData, @Nullable Subscription.OnError onError) {
        return subscribe(k, (Executor) null, onData, onError);
    }

    public Subscription<K, T> subscribe(@Nonnull K k, @Nullable Executor executor, @Nonnull Subscription.OnData<T> onData) {
        return subscribe(k, executor, onData, (Subscription.OnError) null);
    }

    public Subscription<K, T> subscribe(@Nonnull K k, @Nullable Executor executor, @Nonnull Subscription.OnData<T> onData, @Nullable Subscription.OnError onError) {
        return null;
    }
}
