// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;

public interface RequestSource<K, T>
{
    ResultHandler<K, T> attachRequestHandler(@Nonnull final RequestHandler<K> p0);
    
    void detachRequestHandler(@Nonnull final RequestHandler<K> p0);
}
