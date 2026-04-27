package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractListMultimap<K, V> extends AbstractMapBasedMultimap<K, V> implements ListMultimap<K, V> {
    private static final long serialVersionUID = 6588350623831699109L;

    protected AbstractListMultimap(Map<K, Collection<V>> map) {
        super(map);
    }

    public Map<K, Collection<V>> asMap() {
        return super.asMap();
    }

    /* access modifiers changed from: package-private */
    public abstract List<V> createCollection();

    /* access modifiers changed from: package-private */
    public List<V> createUnmodifiableEmptyCollection() {
        return ImmutableList.of();
    }

    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    public List<V> get(@Nullable K k) {
        return (List) super.get(k);
    }

    public boolean put(@Nullable K k, @Nullable V v) {
        return super.put(k, v);
    }

    public List<V> removeAll(@Nullable Object obj) {
        return (List) super.removeAll(obj);
    }

    public List<V> replaceValues(@Nullable K k, Iterable<? extends V> iterable) {
        return (List) super.replaceValues(k, iterable);
    }
}
