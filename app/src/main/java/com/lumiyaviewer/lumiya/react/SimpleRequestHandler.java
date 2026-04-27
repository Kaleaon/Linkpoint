package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;

public abstract class SimpleRequestHandler<K> implements RequestHandler<K> {
    public void onRequestCancelled(@Nonnull K k) {
    }
}
