// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(serializable = true)
final class NullsFirstOrdering<T> extends Ordering<T> implements Serializable
{
    private static final long serialVersionUID = 0L;
    final Ordering<? super T> ordering;
    
    NullsFirstOrdering(final Ordering<? super T> ordering) {
        this.ordering = ordering;
    }
    
    @Override
    public int compare(@Nullable final T t, @Nullable final T t2) {
        if (t == t2) {
            return 0;
        }
        if (t == null) {
            return -1;
        }
        if (t2 != null) {
            return this.ordering.compare((Object)t, (Object)t2);
        }
        return 1;
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return o == this || (o instanceof NullsFirstOrdering && this.ordering.equals(((NullsFirstOrdering)o).ordering));
    }
    
    @Override
    public int hashCode() {
        return this.ordering.hashCode() ^ 0x39153A74;
    }
    
    @Override
    public <S extends T> Ordering<S> nullsFirst() {
        return (Ordering<S>)this;
    }
    
    @Override
    public <S extends T> Ordering<S> nullsLast() {
        return this.ordering.nullsLast();
    }
    
    @Override
    public <S extends T> Ordering<S> reverse() {
        return this.ordering.reverse().nullsLast();
    }
    
    @Override
    public String toString() {
        return this.ordering + ".nullsFirst()";
    }
}
