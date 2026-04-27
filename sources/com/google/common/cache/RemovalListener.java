package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface RemovalListener<K, V> {
    void onRemoval(RemovalNotification<K, V> removalNotification);
}
