// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import java.util.Map;

@GwtCompatible
public abstract class ForwardingMapEntry<K, V> extends ForwardingObject implements Entry<K, V>
{
    protected ForwardingMapEntry() {
    }
    
    @Override
    protected abstract Entry<K, V> delegate();
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return this.delegate().equals(o);
    }
    
    @Override
    public K getKey() {
        return this.delegate().getKey();
    }
    
    @Override
    public V getValue() {
        return this.delegate().getValue();
    }
    
    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }
    
    @Override
    public V setValue(final V value) {
        return this.delegate().setValue(value);
    }
    
    protected boolean standardEquals(@Nullable final Object o) {
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
    
    protected int standardHashCode() {
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
    
    @Beta
    protected String standardToString() {
        return this.getKey() + "=" + this.getValue();
    }
}
