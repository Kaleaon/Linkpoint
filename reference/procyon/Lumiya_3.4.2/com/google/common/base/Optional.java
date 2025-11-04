// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import java.util.Set;
import com.google.common.annotations.Beta;
import java.util.Iterator;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;
import java.io.Serializable;

@CheckReturnValue
@GwtCompatible(serializable = true)
public abstract class Optional<T> implements Serializable
{
    private static final long serialVersionUID = 0L;
    
    Optional() {
    }
    
    public static <T> Optional<T> absent() {
        return Absent.withType();
    }
    
    public static <T> Optional<T> fromNullable(@Nullable final T t) {
        Optional<Object> absent;
        if (t != null) {
            absent = new Present<Object>(t);
        }
        else {
            absent = absent();
        }
        return (Optional<T>)absent;
    }
    
    public static <T> Optional<T> of(final T t) {
        return new Present<T>(Preconditions.checkNotNull(t));
    }
    
    @Beta
    public static <T> Iterable<T> presentInstances(final Iterable<? extends Optional<? extends T>> iterable) {
        Preconditions.checkNotNull(iterable);
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new AbstractIterator<T>() {
                    private final Iterator<? extends Optional<? extends T>> iterator = Preconditions.checkNotNull(iterable.iterator());
                    
                    @Override
                    protected T computeNext() {
                        while (this.iterator.hasNext()) {
                            final Optional optional = (Optional)this.iterator.next();
                            if (optional.isPresent()) {
                                return (T)optional.get();
                            }
                        }
                        return this.endOfData();
                    }
                };
            }
        };
    }
    
    public abstract Set<T> asSet();
    
    @Override
    public abstract boolean equals(@Nullable final Object p0);
    
    public abstract T get();
    
    @Override
    public abstract int hashCode();
    
    public abstract boolean isPresent();
    
    public abstract Optional<T> or(final Optional<? extends T> p0);
    
    @Beta
    public abstract T or(final Supplier<? extends T> p0);
    
    public abstract T or(final T p0);
    
    @Nullable
    public abstract T orNull();
    
    @Override
    public abstract String toString();
    
    public abstract <V> Optional<V> transform(final Function<? super T, V> p0);
}
