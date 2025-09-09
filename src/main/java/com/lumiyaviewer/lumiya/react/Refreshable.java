package com.lumiyaviewer.lumiya.react;

public interface Refreshable<K> {
    void requestUpdate(K k);
}
