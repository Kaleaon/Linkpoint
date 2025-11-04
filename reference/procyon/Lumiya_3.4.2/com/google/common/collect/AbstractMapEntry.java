// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Objects;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import java.util.Map;

@GwtCompatible
abstract class AbstractMapEntry<K, V> implements Entry<K, V>
{
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
    
    @Override
    public abstract K getKey();
    
    @Override
    public abstract V getValue();
    
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
    public V setValue(final V v) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        return this.getKey() + "=" + this.getValue();
    }
}
