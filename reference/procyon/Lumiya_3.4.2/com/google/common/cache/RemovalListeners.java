// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.cache;

import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;

public final class RemovalListeners
{
    private RemovalListeners() {
    }
    
    public static <K, V> RemovalListener<K, V> asynchronous(final RemovalListener<K, V> removalListener, final Executor executor) {
        Preconditions.checkNotNull(removalListener);
        Preconditions.checkNotNull(executor);
        return new RemovalListener<K, V>() {
            @Override
            public void onRemoval(final RemovalNotification<K, V> removalNotification) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        removalListener.onRemoval(removalNotification);
                    }
                });
            }
        };
    }
}
