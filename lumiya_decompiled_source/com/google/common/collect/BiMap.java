package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
@GwtCompatible
/* loaded from: classes.dex */
public interface BiMap<K, V> extends Map<K, V> {
    @Nullable
    V forcePut(@Nullable K k, @Nullable V v);

    BiMap<V, K> inverse();

    @Override // com.google.common.collect.BiMap
    @Nullable
    V put(@Nullable K k, @Nullable V v);

    @Override // com.google.common.collect.BiMap
    void putAll(Map<? extends K, ? extends V> map);

    @Override // java.util.Map
    Set<V> values();
}
