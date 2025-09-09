package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Callables {
    private Callables() {
    }

    public static <T> Callable<T> returning(@Nullable final T t) {
        return new Callable<T>() {
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
            public void run() {
                Thread currentThread = Thread.currentThread();
                String name = currentThread.getName();
                boolean access$000 = Callables.trySetName((String) supplier.get(), currentThread);
                try {
                    runnable.run();
                    if (access$000) {
                        boolean unused = Callables.trySetName(name, currentThread);
                    }
                } catch (Throwable th) {
                    if (access$000) {
                        boolean unused2 = Callables.trySetName(name, currentThread);
                    }
                    throw th;
                }
            }
        };
    }

    @GwtIncompatible("threads")
    static <T> Callable<T> threadRenaming(final Callable<T> callable, final Supplier<String> supplier) {
        Preconditions.checkNotNull(supplier);
        Preconditions.checkNotNull(callable);
        return new Callable<T>() {
            public T call() throws Exception {
                Thread currentThread = Thread.currentThread();
                String name = currentThread.getName();
                boolean access$000 = Callables.trySetName((String) supplier.get(), currentThread);
                try {
                    T call = callable.call();
                    if (access$000) {
                        boolean unused = Callables.trySetName(name, currentThread);
                    }
                    return call;
                } catch (Throwable th) {
                    if (access$000) {
                        boolean unused2 = Callables.trySetName(name, currentThread);
                    }
                    throw th;
                }
            }
        };
    }

    /* access modifiers changed from: private */
    @GwtIncompatible("threads")
    public static boolean trySetName(String str, Thread thread) {
        try {
            thread.setName(str);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }
}
