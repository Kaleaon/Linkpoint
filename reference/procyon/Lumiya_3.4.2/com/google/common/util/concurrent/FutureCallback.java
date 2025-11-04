// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface FutureCallback<V>
{
    void onFailure(final Throwable p0);
    
    void onSuccess(@Nullable final V p0);
}
