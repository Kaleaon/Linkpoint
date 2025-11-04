// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import com.google.common.annotations.Beta;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@GwtCompatible
public final class Suppliers
{
    private Suppliers() {
    }
    
    public static <F, T> Supplier<T> compose(final Function<? super F, T> function, final Supplier<F> supplier) {
        Preconditions.checkNotNull(function);
        Preconditions.checkNotNull(supplier);
        return new SupplierComposition<Object, T>(function, supplier);
    }
    
    public static <T> Supplier<T> memoize(final Supplier<T> supplier) {
        Supplier<T> supplier2 = supplier;
        if (!(supplier instanceof MemoizingSupplier)) {
            supplier2 = new MemoizingSupplier<T>(Preconditions.checkNotNull(supplier));
        }
        return supplier2;
    }
    
    public static <T> Supplier<T> memoizeWithExpiration(final Supplier<T> supplier, final long n, final TimeUnit timeUnit) {
        return new ExpiringMemoizingSupplier<T>(supplier, n, timeUnit);
    }
    
    public static <T> Supplier<T> ofInstance(@Nullable final T t) {
        return new SupplierOfInstance<T>(t);
    }
    
    @Beta
    public static <T> Function<Supplier<T>, T> supplierFunction() {
        return (Function<Supplier<T>, T>)SupplierFunctionImpl.INSTANCE;
    }
    
    public static <T> Supplier<T> synchronizedSupplier(final Supplier<T> supplier) {
        return new ThreadSafeSupplier<T>(Preconditions.checkNotNull(supplier));
    }
    
    @VisibleForTesting
    static class ExpiringMemoizingSupplier<T> implements Supplier<T>, Serializable
    {
        private static final long serialVersionUID = 0L;
        final Supplier<T> delegate;
        final long durationNanos;
        transient volatile long expirationNanos;
        transient volatile T value;
        
        ExpiringMemoizingSupplier(final Supplier<T> supplier, final long duration, final TimeUnit timeUnit) {
            this.delegate = Preconditions.checkNotNull(supplier);
            this.durationNanos = timeUnit.toNanos(duration);
            boolean b;
            if (duration <= 0L) {
                b = true;
            }
            else {
                b = false;
            }
            Preconditions.checkArgument(!b);
        }
        
        @Override
        public T get() {
            long expirationNanos = this.expirationNanos;
            long systemNanoTime = Platform.systemNanoTime();
            Label_0086: {
                while (true) {
                    Label_0094: {
                        Label_0031: {
                            if (expirationNanos == 0L) {
                                break Label_0031;
                            }
                            if (systemNanoTime - expirationNanos >= 0L) {
                                break Label_0086;
                            }
                            final int n = 1;
                            if (n != 0) {
                                break Label_0094;
                            }
                        }
                        synchronized (this) {
                            if (expirationNanos == this.expirationNanos) {
                                final T value = this.delegate.get();
                                this.value = value;
                                expirationNanos = (systemNanoTime += this.durationNanos);
                                if (expirationNanos == 0L) {
                                    systemNanoTime = 1L;
                                }
                                this.expirationNanos = systemNanoTime;
                                return value;
                            }
                            monitorexit(this);
                            return this.value;
                            final int n = 0;
                            continue;
                        }
                    }
                    break;
                }
            }
        }
        
        @Override
        public String toString() {
            return "Suppliers.memoizeWithExpiration(" + this.delegate + ", " + this.durationNanos + ", NANOS)";
        }
    }
    
    @VisibleForTesting
    static class MemoizingSupplier<T> implements Supplier<T>, Serializable
    {
        private static final long serialVersionUID = 0L;
        final Supplier<T> delegate;
        transient volatile boolean initialized;
        transient T value;
        
        MemoizingSupplier(final Supplier<T> delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public T get() {
            if (!this.initialized) {
                synchronized (this) {
                    if (this.initialized) {
                        return this.value;
                    }
                }
                final T value = this.delegate.get();
                this.value = value;
                this.initialized = true;
                monitorexit(this);
                return value;
            }
            return this.value;
        }
        
        @Override
        public String toString() {
            return "Suppliers.memoize(" + this.delegate + ")";
        }
    }
    
    private static class SupplierComposition<F, T> implements Supplier<T>, Serializable
    {
        private static final long serialVersionUID = 0L;
        final Function<? super F, T> function;
        final Supplier<F> supplier;
        
        SupplierComposition(final Function<? super F, T> function, final Supplier<F> supplier) {
            this.function = function;
            this.supplier = supplier;
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            boolean b = false;
            if (!(o instanceof SupplierComposition)) {
                return false;
            }
            final SupplierComposition supplierComposition = (SupplierComposition)o;
            if (this.function.equals(supplierComposition.function) && this.supplier.equals(supplierComposition.supplier)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public T get() {
            return this.function.apply(this.supplier.get());
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.function, this.supplier);
        }
        
        @Override
        public String toString() {
            return "Suppliers.compose(" + this.function + ", " + this.supplier + ")";
        }
    }
    
    private interface SupplierFunction<T> extends Function<Supplier<T>, T>
    {
    }
    
    private enum SupplierFunctionImpl implements SupplierFunction<Object>
    {
        INSTANCE;
        
        @Override
        public Object apply(final Supplier<Object> supplier) {
            return supplier.get();
        }
        
        @Override
        public String toString() {
            return "Suppliers.supplierFunction()";
        }
    }
    
    private static class SupplierOfInstance<T> implements Supplier<T>, Serializable
    {
        private static final long serialVersionUID = 0L;
        final T instance;
        
        SupplierOfInstance(@Nullable final T instance) {
            this.instance = instance;
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            return o instanceof SupplierOfInstance && Objects.equal(this.instance, ((SupplierOfInstance)o).instance);
        }
        
        @Override
        public T get() {
            return this.instance;
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.instance);
        }
        
        @Override
        public String toString() {
            return "Suppliers.ofInstance(" + this.instance + ")";
        }
    }
    
    private static class ThreadSafeSupplier<T> implements Supplier<T>, Serializable
    {
        private static final long serialVersionUID = 0L;
        final Supplier<T> delegate;
        
        ThreadSafeSupplier(final Supplier<T> delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public T get() {
            synchronized (this.delegate) {
                return this.delegate.get();
            }
        }
        
        @Override
        public String toString() {
            return "Suppliers.synchronizedSupplier(" + this.delegate + ")";
        }
    }
}
