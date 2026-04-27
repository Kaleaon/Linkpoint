// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface RequestQueue<K, T> extends RequestSource<K, T>
{
    @Nullable
    K getNextRequest();
    
    @Nonnull
    ResultHandler<K, T> getResultHandler();
    
    void returnRequest(@Nonnull final K p0);
    
    @Nullable
    K waitForRequest() throws InterruptedException;
}
