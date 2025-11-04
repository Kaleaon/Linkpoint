// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class Callables
{
    private Callables() {
    }
    
    public static <T> Callable<T> returning(@Nullable final T t) {
        return new Callable<T>() {
            @Override
            public T call() {
                return t;
            }
        };
    }
    
    @GwtIncompatible("threads")
    static Runnable threadRenaming(final Runnable runnable, final Supplier<String> supplier) {
        Preconditions.checkNotNull(supplier);
        Preconditions.checkNotNull(runnable);
        return new Runnable() {
            @Override
            public void run() {
                final Thread currentThread = Thread.currentThread();
                final String name = currentThread.getName();
                final boolean access$000 = trySetName(supplier.get(), currentThread);
                try {
                    runnable.run();
                    if (access$000) {
                        trySetName(name, currentThread);
                    }
                }
                finally {
                    while (true) {
                        if (!access$000) {
                            break Label_0055;
                        }
                        trySetName(name, currentThread);
                        break Label_0055;
                        continue;
                    }
                }
            }
        };
    }
    
    @GwtIncompatible("threads")
    static <T> Callable<T> threadRenaming(final Callable<T> callable, final Supplier<String> supplier) {
        Preconditions.checkNotNull(supplier);
        Preconditions.checkNotNull(callable);
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                final Thread currentThread = Thread.currentThread();
                final String name = currentThread.getName();
                final boolean access$000 = trySetName(supplier.get(), currentThread);
                try {
                    final T call = callable.call();
                    if (access$000) {
                        trySetName(name, currentThread);
                    }
                    return call;
                }
                finally {
                    while (true) {
                        if (!access$000) {
                            break Label_0059;
                        }
                        trySetName(name, currentThread);
                        break Label_0059;
                        continue;
                    }
                }
            }
        };
    }
    
    @GwtIncompatible("threads")
    private static boolean trySetName(final String name, final Thread thread) {
        try {
            thread.setName(name);
            return true;
        }
        catch (final SecurityException ex) {
            return false;
        }
    }
}
