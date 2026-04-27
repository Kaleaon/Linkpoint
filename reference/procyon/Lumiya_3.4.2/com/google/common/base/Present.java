// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
final class Present<T> extends Optional<T>
{
    private static final long serialVersionUID = 0L;
    private final T reference;
    
    Present(final T reference) {
        this.reference = reference;
    }
    
    @Override
    public Set<T> asSet() {
        return Collections.singleton(this.reference);
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return o instanceof Present && this.reference.equals(((Present)o).reference);
    }
    
    @Override
    public T get() {
        return this.reference;
    }
    
    @Override
    public int hashCode() {
        return this.reference.hashCode() + 1502476572;
    }
    
    @Override
    public boolean isPresent() {
        return true;
    }
    
    @Override
    public Optional<T> or(final Optional<? extends T> optional) {
        Preconditions.checkNotNull(optional);
        return this;
    }
    
    @Override
    public T or(final Supplier<? extends T> supplier) {
        Preconditions.checkNotNull(supplier);
        return this.reference;
    }
    
    @Override
    public T or(final T t) {
        Preconditions.checkNotNull(t, (Object)"use Optional.orNull() instead of Optional.or(null)");
        return this.reference;
    }
    
    @Override
    public T orNull() {
        return this.reference;
    }
    
    @Override
    public String toString() {
        return "Optional.of(" + this.reference + ")";
    }
    
    @Override
    public <V> Optional<V> transform(final Function<? super T, V> function) {
        return new Present<V>(Preconditions.checkNotNull(function.apply(this.reference), (Object)"the Function passed to Optional.transform() must not return null."));
    }
}
