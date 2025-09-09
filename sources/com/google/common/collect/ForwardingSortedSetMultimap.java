package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSortedSetMultimap<K, V> extends ForwardingSetMultimap<K, V> implements SortedSetMultimap<K, V> {
    protected ForwardingSortedSetMultimap() {
    }

    /* access modifiers changed from: protected */
    public abstract SortedSetMultimap<K, V> delegate();

    public SortedSet<V> get(@Nullable K k) {
        return delegate().get(k);
    }

    public SortedSet<V> removeAll(@Nullable Object obj) {
        return delegate().removeAll(obj);
    }

    public SortedSet<V> replaceValues(K k, Iterable<? extends V> iterable) {
        return delegate().replaceValues(k, iterable);
    }

    public Comparator<? super V> valueComparator() {
        return delegate().valueComparator();
    }
}
