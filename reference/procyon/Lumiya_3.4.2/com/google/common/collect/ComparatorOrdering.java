// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(serializable = true)
final class ComparatorOrdering<T> extends Ordering<T> implements Serializable
{
    private static final long serialVersionUID = 0L;
    final Comparator<T> comparator;
    
    ComparatorOrdering(final Comparator<T> comparator) {
        this.comparator = Preconditions.checkNotNull(comparator);
    }
    
    @Override
    public int compare(final T t, final T t2) {
        return this.comparator.compare(t, t2);
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return o == this || (o instanceof ComparatorOrdering && this.comparator.equals(((ComparatorOrdering)o).comparator));
    }
    
    @Override
    public int hashCode() {
        return this.comparator.hashCode();
    }
    
    @Override
    public String toString() {
        return this.comparator.toString();
    }
}
