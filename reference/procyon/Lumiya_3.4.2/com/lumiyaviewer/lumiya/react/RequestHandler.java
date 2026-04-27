// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;

public interface RequestHandler<K>
{
    void onRequest(@Nonnull final K p0);
    
    void onRequestCancelled(@Nonnull final K p0);
}
