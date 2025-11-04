// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(serializable = true)
final class CompoundOrdering<T> extends Ordering<T> implements Serializable
{
    private static final long serialVersionUID = 0L;
    final ImmutableList<Comparator<? super T>> comparators;
    
    CompoundOrdering(final Iterable<? extends Comparator<? super T>> iterable) {
        this.comparators = ImmutableList.copyOf(iterable);
    }
    
    CompoundOrdering(final Comparator<? super T> comparator, final Comparator<? super T> comparator2) {
        this.comparators = ImmutableList.of(comparator, comparator2);
    }
    
    @Override
    public int compare(final T t, final T t2) {
        for (int size = this.comparators.size(), i = 0; i < size; ++i) {
            final int compare = this.comparators.get(i).compare((Object)t, (Object)t2);
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof CompoundOrdering && this.comparators.equals(((CompoundOrdering)o).comparators));
    }
    
    @Override
    public int hashCode() {
        return this.comparators.hashCode();
    }
    
    @Override
    public String toString() {
        return "Ordering.compound(" + this.comparators + ")";
    }
}
