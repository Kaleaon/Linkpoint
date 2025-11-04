// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.cache;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import java.util.Map;

@GwtCompatible
public final class RemovalNotification<K, V> implements Entry<K, V>
{
    private static final long serialVersionUID = 0L;
    private final RemovalCause cause;
    @Nullable
    private final K key;
    @Nullable
    private final V value;
    
    private RemovalNotification(@Nullable final K key, @Nullable final V value, final RemovalCause removalCause) {
        this.key = key;
        this.value = value;
        this.cause = Preconditions.checkNotNull(removalCause);
    }
    
    public static <K, V> RemovalNotification<K, V> create(@Nullable final K k, @Nullable final V v, final RemovalCause removalCause) {
        return new RemovalNotification<K, V>(k, v, removalCause);
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        boolean b = false;
        if (!(o instanceof Entry)) {
            return false;
        }
        final Entry entry = (Entry)o;
        if (Objects.equal(this.getKey(), entry.getKey()) && Objects.equal(this.getValue(), entry.getValue())) {
            b = true;
        }
        return b;
    }
    
    public RemovalCause getCause() {
        return this.cause;
    }
    
    @Nullable
    @Override
    public K getKey() {
        return this.key;
    }
    
    @Nullable
    @Override
    public V getValue() {
        return this.value;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        final Object key = this.getKey();
        final Object value = this.getValue();
        int hashCode2;
        if (key != null) {
            hashCode2 = key.hashCode();
        }
        else {
            hashCode2 = 0;
        }
        if (value != null) {
            hashCode = value.hashCode();
        }
        return hashCode ^ hashCode2;
    }
    
    @Override
    public final V setValue(final V v) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        return this.getKey() + "=" + this.getValue();
    }
    
    public boolean wasEvicted() {
        return this.cause.wasEvicted();
    }
}
