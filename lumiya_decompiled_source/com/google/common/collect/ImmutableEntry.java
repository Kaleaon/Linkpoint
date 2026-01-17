package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import javax.annotation.Nullable;
@GwtCompatible(serializable = true)
/* loaded from: classes.dex */
class ImmutableEntry<K, V> extends AbstractMapEntry<K, V> implements Serializable {
    private static final long serialVersionUID = 0;
    final K key;
    final V value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ImmutableEntry(@Nullable K k, @Nullable V v) {
        this.key = k;
        this.value = v;
    }

    @Override // com.google.common.collect.AbstractMapEntry, java.util.Map.Entry
    @Nullable
    public final K getKey() {
        return this.key;
    }

    @Override // com.google.common.collect.AbstractMapEntry, java.util.Map.Entry
    @Nullable
    public final V getValue() {
        return this.value;
    }

    @Override // com.google.common.collect.AbstractMapEntry, java.util.Map.Entry
    public final V setValue(V v) {
        throw new UnsupportedOperationException();
    }
}
