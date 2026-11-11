package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
@GwtCompatible
/* loaded from: classes.dex */
public interface SetMultimap<K, V> extends Multimap<K, V> {
    @Override // com.google.common.collect.Multimap, com.google.common.collect.ListMultimap
    Map<K, Collection<V>> asMap();

    @Override // com.google.common.collect.Multimap
    Set<Map.Entry<K, V>> entries();

    @Override // com.google.common.collect.Multimap, com.google.common.collect.ListMultimap
    boolean equals(@Nullable Object obj);

    @Override // com.google.common.collect.Multimap
    Set<V> get(@Nullable K k);

    @Override // com.google.common.collect.Multimap
    Set<V> removeAll(@Nullable Object obj);

    @Override // com.google.common.collect.Multimap
    Set<V> replaceValues(K k, Iterable<? extends V> iterable);
}
