package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;

public interface RequestHandler<K> {
    void onRequest(@Nonnull K k);

    void onRequestCancelled(@Nonnull K k);
}
