package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
@GwtCompatible
/* loaded from: classes.dex */
public interface ListMultimap<K, V> extends Multimap<K, V> {
    @Override // 
    Map<K, Collection<V>> asMap();

    @Override // 
    boolean equals(@Nullable Object obj);

    @Override // com.google.common.collect.Multimap
    List<V> get(@Nullable K k);

    @Override // com.google.common.collect.Multimap
    List<V> removeAll(@Nullable Object obj);

    @Override // com.google.common.collect.Multimap
    List<V> replaceValues(K k, Iterable<? extends V> iterable);
}
