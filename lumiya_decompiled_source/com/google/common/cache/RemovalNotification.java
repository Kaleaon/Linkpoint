package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.util.Map;
import javax.annotation.Nullable;
@GwtCompatible
/* loaded from: classes.dex */
public final class RemovalNotification<K, V> implements Map.Entry<K, V> {
    private static final long serialVersionUID = 0;
    private final RemovalCause cause;
    @Nullable
    private final K key;
    @Nullable
    private final V value;

    private RemovalNotification(@Nullable K k, @Nullable V v, RemovalCause removalCause) {
        this.key = k;
        this.value = v;
        this.cause = (RemovalCause) Preconditions.checkNotNull(removalCause);
    }

    public static <K, V> RemovalNotification<K, V> create(@Nullable K k, @Nullable V v, RemovalCause removalCause) {
        return new RemovalNotification<>(k, v, removalCause);
    }

    @Override // java.util.Map.Entry
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Map.Entry) {
            Map.Entry entry = (Map.Entry) obj;
            return Objects.equal(getKey(), entry.getKey()) && Objects.equal(getValue(), entry.getValue());
        }
        return false;
    }

    public RemovalCause getCause() {
        return this.cause;
    }

    @Override // java.util.Map.Entry
    @Nullable
    public K getKey() {
        return this.key;
    }

    @Override // java.util.Map.Entry
    @Nullable
    public V getValue() {
        return this.value;
    }

    @Override // java.util.Map.Entry
    public int hashCode() {
        K key = getKey();
        V value = getValue();
        return (value != null ? value.hashCode() : 0) ^ (key != null ? key.hashCode() : 0);
    }

    @Override // java.util.Map.Entry
    public final V setValue(V v) {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return getKey() + "=" + getValue();
    }

    public boolean wasEvicted() {
        return this.cause.wasEvicted();
    }
}
