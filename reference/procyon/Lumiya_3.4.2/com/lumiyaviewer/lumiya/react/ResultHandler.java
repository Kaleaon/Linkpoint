// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;

public interface ResultHandler<K, T>
{
    void onResultData(@Nonnull final K p0, final T p1);
    
    void onResultError(@Nonnull final K p0, final Throwable p1);
}
