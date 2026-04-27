package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface SetMultimap<K, V> extends Multimap<K, V> {
    Map<K, Collection<V>> asMap();

    Set<Map.Entry<K, V>> entries();

    boolean equals(@Nullable Object obj);

    Set<V> get(@Nullable K k);

    Set<V> removeAll(@Nullable Object obj);

    Set<V> replaceValues(K k, Iterable<? extends V> iterable);
}
