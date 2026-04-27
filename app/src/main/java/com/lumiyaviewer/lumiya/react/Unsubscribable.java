package com.lumiyaviewer.lumiya.react;

public interface Unsubscribable<K, T> {
    void unsubscribe(Subscription<K, T> subscription);
}