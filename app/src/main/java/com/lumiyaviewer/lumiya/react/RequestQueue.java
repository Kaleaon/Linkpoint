package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface RequestQueue<K, T> extends RequestSource<K, T> {
    @Nullable
    K getNextRequest();

    @Nonnull
    ResultHandler<K, T> getResultHandler();

    void returnRequest(@Nonnull K k);

    @Nullable
    K waitForRequest() throws InterruptedException;
}
