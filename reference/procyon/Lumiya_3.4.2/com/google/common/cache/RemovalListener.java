// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface RemovalListener<K, V>
{
    void onRemoval(final RemovalNotification<K, V> p0);
}
