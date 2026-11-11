package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import javax.annotation.Nullable;
@Beta
/* loaded from: classes.dex */
public final class Closer implements Closeable {
    private static final Suppressor SUPPRESSOR;
    private final Deque<Closeable> stack = new ArrayDeque(4);
    @VisibleForTesting
    final Suppressor suppressor;
    private Throwable thrown;

    @VisibleForTesting
    /* loaded from: classes.dex */
    static final class LoggingSuppressor implements Suppressor {
        static final LoggingSuppressor INSTANCE = new LoggingSuppressor();

        LoggingSuppressor() {
        }

        @Override // com.google.common.io.Closer.Suppressor
        public void suppress(Closeable closeable, Throwable th, Throwable th2) {
            Closeables.logger.log(Level.WARNING, "Suppressing exception thrown when closing " + closeable, th2);
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    static final class SuppressingSuppressor implements Suppressor {
        static final SuppressingSuppressor INSTANCE = new SuppressingSuppressor();
        static final Method addSuppressed = getAddSuppressed();

        SuppressingSuppressor() {
        }

        private static Method getAddSuppressed() {
            try {
                return Throwable.class.getMethod("addSuppressed", Throwable.class);
            } catch (Throwable th) {
                return null;
            }
        }

        static boolean isAvailable() {
            return addSuppressed != null;
        }

        @Override // com.google.common.io.Closer.Suppressor
        public void suppress(Closeable closeable, Throwable th, Throwable th2) {
            if (th != th2) {
                try {
                    addSuppressed.invoke(th, th2);
                } catch (Throwable th3) {
                    LoggingSuppressor.INSTANCE.suppress(closeable, th, th2);
                }
            }
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    interface Suppressor {
        void suppress(Closeable closeable, Throwable th, Throwable th2);
    }

    static {
        SUPPRESSOR = !SuppressingSuppressor.isAvailable() ? LoggingSuppressor.INSTANCE : SuppressingSuppressor.INSTANCE;
    }

    @VisibleForTesting
    Closer(Suppressor suppressor) {
        this.suppressor = (Suppressor) Preconditions.checkNotNull(suppressor);
    }

    public static Closer create() {
        return new Closer(SUPPRESSOR);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        Throwable th;
        Throwable th2 = this.thrown;
        while (true) {
            th = th2;
            if (this.stack.isEmpty()) {
                break;
            }
            Closeable removeFirst = this.stack.removeFirst();
            try {
                removeFirst.close();
                th2 = th;
            } catch (Throwable th3) {
                if (th != null) {
                    this.suppressor.suppress(removeFirst, th, th3);
                    th2 = th;
                } else {
                    th2 = th3;
                }
            }
        }
        if (this.thrown == null && th != null) {
            Throwables.propagateIfPossible(th, IOException.class);
            throw new AssertionError(th);
        }
    }

    public <C extends Closeable> C register(@Nullable C c) {
        if (c != null) {
            this.stack.addFirst(c);
        }
        return c;
    }

    public RuntimeException rethrow(Throwable th) throws IOException {
        Preconditions.checkNotNull(th);
        this.thrown = th;
        Throwables.propagateIfPossible(th, IOException.class);
        throw new RuntimeException(th);
    }

    public <X extends Exception> RuntimeException rethrow(Throwable th, Class<X> cls) throws IOException, Exception {
        Preconditions.checkNotNull(th);
        this.thrown = th;
        Throwables.propagateIfPossible(th, IOException.class);
        Throwables.propagateIfPossible(th, cls);
        throw new RuntimeException(th);
    }

    public <X1 extends Exception, X2 extends Exception> RuntimeException rethrow(Throwable th, Class<X1> cls, Class<X2> cls2) throws IOException, Exception, Exception {
        Preconditions.checkNotNull(th);
        this.thrown = th;
        Throwables.propagateIfPossible(th, IOException.class);
        Throwables.propagateIfPossible(th, cls, cls2);
        throw new RuntimeException(th);
    }
}
