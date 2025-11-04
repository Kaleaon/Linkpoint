package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public interface RequestSource<K, T> {
    ResultHandler<K, T> attachRequestHandler(@Nonnull RequestHandler<K> requestHandler);

    void detachRequestHandler(@Nonnull RequestHandler<K> requestHandler);
}
