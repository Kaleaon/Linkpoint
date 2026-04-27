package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.react.Subscription.OnData;
import com.lumiyaviewer.lumiya.react.Subscription.OnError;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Subscribable<K, T> {
    Subscription<K, T> subscribe(@Nonnull K k, @Nonnull OnData<T> onData);

    Subscription<K, T> subscribe(@Nonnull K k, @Nonnull OnData<T> onData, @Nullable OnError onError);

    Subscription<K, T> subscribe(@Nonnull K k, @Nullable Executor executor, @Nonnull OnData<T> onData);

    Subscription<K, T> subscribe(@Nonnull K k, @Nullable Executor executor, @Nonnull OnData<T> onData, @Nullable OnError onError);
}
