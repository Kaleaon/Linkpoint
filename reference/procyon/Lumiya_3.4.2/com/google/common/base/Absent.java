// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
final class Absent<T> extends Optional<T>
{
    static final Absent<Object> INSTANCE;
    private static final long serialVersionUID = 0L;
    
    static {
        INSTANCE = new Absent<Object>();
    }
    
    private Absent() {
    }
    
    private Object readResolve() {
        return Absent.INSTANCE;
    }
    
    static <T> Optional<T> withType() {
        return (Optional<T>)Absent.INSTANCE;
    }
    
    @Override
    public Set<T> asSet() {
        return Collections.emptySet();
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return o == this;
    }
    
    @Override
    public T get() {
        throw new IllegalStateException("Optional.get() cannot be called on an absent value");
    }
    
    @Override
    public int hashCode() {
        return 2040732332;
    }
    
    @Override
    public boolean isPresent() {
        return false;
    }
    
    @Override
    public Optional<T> or(final Optional<? extends T> optional) {
        return Preconditions.checkNotNull((Optional<T>)optional);
    }
    
    @Override
    public T or(final Supplier<? extends T> supplier) {
        return Preconditions.checkNotNull((T)supplier.get(), (Object)"use Optional.orNull() instead of a Supplier that returns null");
    }
    
    @Override
    public T or(final T t) {
        return Preconditions.checkNotNull(t, (Object)"use Optional.orNull() instead of Optional.or(null)");
    }
    
    @Nullable
    @Override
    public T orNull() {
        return null;
    }
    
    @Override
    public String toString() {
        return "Optional.absent()";
    }
    
    @Override
    public <V> Optional<V> transform(final Function<? super T, V> function) {
        Preconditions.checkNotNull(function);
        return Optional.absent();
    }
}
