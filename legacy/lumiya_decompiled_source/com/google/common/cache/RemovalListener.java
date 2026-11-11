package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
@GwtCompatible
/* loaded from: classes.dex */
public interface RemovalListener<K, V> {
    void onRemoval(RemovalNotification<K, V> removalNotification);
}
