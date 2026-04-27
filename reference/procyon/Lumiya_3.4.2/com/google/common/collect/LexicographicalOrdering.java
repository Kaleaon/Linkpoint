// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(serializable = true)
final class LexicographicalOrdering<T> extends Ordering<Iterable<T>> implements Serializable
{
    private static final long serialVersionUID = 0L;
    final Comparator<? super T> elementOrder;
    
    LexicographicalOrdering(final Comparator<? super T> elementOrder) {
        this.elementOrder = elementOrder;
    }
    
    @Override
    public int compare(final Iterable<T> iterable, final Iterable<T> iterable2) {
        final Iterator<T> iterator = iterable.iterator();
        final Iterator<T> iterator2 = iterable2.iterator();
        while (iterator.hasNext()) {
            if (!iterator2.hasNext()) {
                return 1;
            }
            final int compare = this.elementOrder.compare(iterator.next(), iterator2.next());
            if (compare != 0) {
                return compare;
            }
        }
        if (!iterator2.hasNext()) {
            return 0;
        }
        return -1;
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return o == this || (o instanceof LexicographicalOrdering && this.elementOrder.equals(((LexicographicalOrdering)o).elementOrder));
    }
    
    @Override
    public int hashCode() {
        return this.elementOrder.hashCode() ^ 0x7BB78CF5;
    }
    
    @Override
    public String toString() {
        return this.elementOrder + ".lexicographical()";
    }
}
