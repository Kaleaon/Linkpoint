// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(serializable = true)
final class ExplicitOrdering<T> extends Ordering<T> implements Serializable
{
    private static final long serialVersionUID = 0L;
    final ImmutableMap<T, Integer> rankMap;
    
    ExplicitOrdering(final ImmutableMap<T, Integer> rankMap) {
        this.rankMap = rankMap;
    }
    
    ExplicitOrdering(final List<T> list) {
        this(Maps.indexMap(list));
    }
    
    private int rank(final T t) {
        final Integer n = this.rankMap.get(t);
        if (n != null) {
            return n;
        }
        throw new IncomparableValueException(t);
    }
    
    @Override
    public int compare(final T t, final T t2) {
        return this.rank(t) - this.rank(t2);
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return o instanceof ExplicitOrdering && this.rankMap.equals(((ExplicitOrdering)o).rankMap);
    }
    
    @Override
    public int hashCode() {
        return this.rankMap.hashCode();
    }
    
    @Override
    public String toString() {
        return "Ordering.explicit(" + this.rankMap.keySet() + ")";
    }
}
