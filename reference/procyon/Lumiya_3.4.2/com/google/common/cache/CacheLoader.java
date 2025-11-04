// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.cache;

import java.io.Serializable;
import com.google.common.util.concurrent.Futures;
import com.google.common.base.Supplier;
import com.google.common.base.Function;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.util.concurrent.ListenableFutureTask;
import java.util.concurrent.Callable;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Map;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public abstract class CacheLoader<K, V>
{
    protected CacheLoader() {
    }
    
    @GwtIncompatible("Executor + Futures")
    public static <K, V> CacheLoader<K, V> asyncReloading(final CacheLoader<K, V> cacheLoader, final Executor executor) {
        Preconditions.checkNotNull(cacheLoader);
        Preconditions.checkNotNull(executor);
        return new CacheLoader<K, V>() {
            @Override
            public V load(final K k) throws Exception {
                return cacheLoader.load(k);
            }
            
            @Override
            public Map<K, V> loadAll(final Iterable<? extends K> iterable) throws Exception {
                return cacheLoader.loadAll(iterable);
            }
            
            @Override
            public ListenableFuture<V> reload(final K k, final V v) throws Exception {
                final ListenableFutureTask<Object> create = ListenableFutureTask.create((Callable<Object>)new Callable<V>() {
                    @Override
                    public V call() throws Exception {
                        return (V)cacheLoader.reload(k, v).get();
                    }
                });
                executor.execute(create);
                return (ListenableFuture<V>)create;
            }
        };
    }
    
    public static <K, V> CacheLoader<K, V> from(final Function<K, V> function) {
        return new FunctionToCacheLoader<K, V>(function);
    }
    
    public static <V> CacheLoader<Object, V> from(final Supplier<V> supplier) {
        return (CacheLoader<Object, V>)new SupplierToCacheLoader((Supplier<Object>)supplier);
    }
    
    public abstract V load(final K p0) throws Exception;
    
    public Map<K, V> loadAll(final Iterable<? extends K> iterable) throws Exception {
        throw new UnsupportedLoadingOperationException();
    }
    
    @GwtIncompatible("Futures")
    public ListenableFuture<V> reload(final K k, final V v) throws Exception {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(v);
        return Futures.immediateFuture(this.load(k));
    }
    
    private static final class FunctionToCacheLoader<K, V> extends CacheLoader<K, V> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Function<K, V> computingFunction;
        
        public FunctionToCacheLoader(final Function<K, V> function) {
            this.computingFunction = Preconditions.checkNotNull(function);
        }
        
        @Override
        public V load(final K k) {
            return this.computingFunction.apply(Preconditions.checkNotNull(k));
        }
    }
    
    public static final class InvalidCacheLoadException extends RuntimeException
    {
        public InvalidCacheLoadException(final String message) {
            super(message);
        }
    }
    
    private static final class SupplierToCacheLoader<V> extends CacheLoader<Object, V> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Supplier<V> computingSupplier;
        
        public SupplierToCacheLoader(final Supplier<V> supplier) {
            this.computingSupplier = Preconditions.checkNotNull(supplier);
        }
        
        @Override
        public V load(final Object o) {
            Preconditions.checkNotNull(o);
            return this.computingSupplier.get();
        }
    }
    
    public static final class UnsupportedLoadingOperationException extends UnsupportedOperationException
    {
        UnsupportedLoadingOperationException() {
        }
    }
}
