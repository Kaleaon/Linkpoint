package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;

public interface ResultHandler<K, T> {
    void onResultData(@Nonnull K k, T t);

    void onResultError(@Nonnull K k, Throwable th);
}
