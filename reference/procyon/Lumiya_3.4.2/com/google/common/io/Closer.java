// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import java.lang.reflect.Method;
import java.util.logging.Level;
import javax.annotation.Nullable;
import com.google.common.base.Throwables;
import java.io.IOException;
import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import com.google.common.annotations.VisibleForTesting;
import java.util.Deque;
import com.google.common.annotations.Beta;
import java.io.Closeable;

@Beta
public final class Closer implements Closeable
{
    private static final Suppressor SUPPRESSOR;
    private final Deque<Closeable> stack;
    @VisibleForTesting
    final Suppressor suppressor;
    private Throwable thrown;
    
    static {
        Suppressor suppressor;
        if (!SuppressingSuppressor.isAvailable()) {
            suppressor = LoggingSuppressor.INSTANCE;
        }
        else {
            suppressor = SuppressingSuppressor.INSTANCE;
        }
        SUPPRESSOR = suppressor;
    }
    
    @VisibleForTesting
    Closer(final Suppressor suppressor) {
        this.stack = new ArrayDeque<Closeable>(4);
        this.suppressor = Preconditions.checkNotNull(suppressor);
    }
    
    public static Closer create() {
        return new Closer(Closer.SUPPRESSOR);
    }
    
    @Override
    public void close() throws IOException {
        Throwable thrown = this.thrown;
        while (true) {
        Label_0044_Outer:
            while (!this.stack.isEmpty()) {
                final Closeable closeable = this.stack.removeFirst();
                while (true) {
                    try {
                        closeable.close();
                        continue Label_0044_Outer;
                    }
                    catch (final Throwable t) {
                        if (thrown != null) {
                            this.suppressor.suppress(closeable, thrown, t);
                            continue;
                        }
                        thrown = t;
                        continue;
                    }
                    break;
                }
                if (thrown != null) {
                    Throwables.propagateIfPossible(thrown, IOException.class);
                    throw new AssertionError((Object)thrown);
                }
                return;
            }
            if (this.thrown != null) {
                return;
            }
            continue;
        }
    }
    
    public <C extends Closeable> C register(@Nullable final C c) {
        if (c != null) {
            this.stack.addFirst(c);
        }
        return c;
    }
    
    public RuntimeException rethrow(final Throwable t) throws IOException {
        Preconditions.checkNotNull(t);
        Throwables.propagateIfPossible(this.thrown = t, IOException.class);
        throw new RuntimeException(t);
    }
    
    public <X extends Exception> RuntimeException rethrow(final Throwable t, final Class<X> clazz) throws IOException, X, Exception {
        Preconditions.checkNotNull(t);
        Throwables.propagateIfPossible(this.thrown = t, IOException.class);
        Throwables.propagateIfPossible(t, clazz);
        throw new RuntimeException(t);
    }
    
    public <X1 extends Exception, X2 extends Exception> RuntimeException rethrow(final Throwable t, final Class<X1> clazz, final Class<X2> clazz2) throws IOException, X1, X2, Exception {
        Preconditions.checkNotNull(t);
        Throwables.propagateIfPossible(this.thrown = t, IOException.class);
        Throwables.propagateIfPossible(t, clazz, clazz2);
        throw new RuntimeException(t);
    }
    
    @VisibleForTesting
    static final class LoggingSuppressor implements Suppressor
    {
        static final LoggingSuppressor INSTANCE;
        
        static {
            INSTANCE = new LoggingSuppressor();
        }
        
        @Override
        public void suppress(final Closeable obj, final Throwable t, final Throwable thrown) {
            Closeables.logger.log(Level.WARNING, "Suppressing exception thrown when closing " + obj, thrown);
        }
    }
    
    @VisibleForTesting
    static final class SuppressingSuppressor implements Suppressor
    {
        static final SuppressingSuppressor INSTANCE;
        static final Method addSuppressed;
        
        static {
            INSTANCE = new SuppressingSuppressor();
            addSuppressed = getAddSuppressed();
        }
        
        private static Method getAddSuppressed() {
            try {
                return Throwable.class.getMethod("addSuppressed", Throwable.class);
            }
            catch (final Throwable t) {
                return null;
            }
        }
        
        static boolean isAvailable() {
            return SuppressingSuppressor.addSuppressed != null;
        }
        
        @Override
        public void suppress(final Closeable closeable, final Throwable obj, final Throwable t) {
            if (obj == t) {
                return;
            }
            try {
                SuppressingSuppressor.addSuppressed.invoke(obj, t);
            }
            catch (final Throwable t2) {
                LoggingSuppressor.INSTANCE.suppress(closeable, obj, t);
            }
        }
    }
    
    @VisibleForTesting
    interface Suppressor
    {
        void suppress(final Closeable p0, final Throwable p1, final Throwable p2);
    }
}
